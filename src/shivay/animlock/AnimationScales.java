package shivay.animlock;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

final class AnimationScales {
    static final float WANTED_SCALE = 0.5f;

    static final String[] SCALE_KEYS = {
        Settings.Global.WINDOW_ANIMATION_SCALE,
        Settings.Global.TRANSITION_ANIMATION_SCALE,
        Settings.Global.ANIMATOR_DURATION_SCALE,
    };

    private static final int CHANGE_JOB_ID = 1;
    private static final int PERIODIC_JOB_ID = 2;
    private static final long PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L;

    private AnimationScales() {
    }

    static int restore(Context context) {
        ContentResolver resolver = context.getContentResolver();
        int restoredCount = 0;
        for (String key : SCALE_KEYS) {
            float current = Settings.Global.getFloat(resolver, key, 1.0f);
            if (current == WANTED_SCALE) {
                continue;
            }
            Settings.Global.putFloat(resolver, key, WANTED_SCALE);
            restoredCount++;
        }
        return restoredCount;
    }

    static void scheduleJobs(Context context) {
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        ComponentName service = new ComponentName(context, RestoreJobService.class);

        // A content-trigger job fires once per change and cannot survive a reboot,
        // so RestoreJobService reschedules it each run and BootReceiver after boot.
        JobInfo.Builder changeJob = new JobInfo.Builder(CHANGE_JOB_ID, service)
            .setTriggerContentUpdateDelay(0)
            .setTriggerContentMaxDelay(1000);
        for (String key : SCALE_KEYS) {
            changeJob.addTriggerContentUri(
                new JobInfo.TriggerContentUri(Settings.Global.getUriFor(key), 0));
        }
        scheduler.schedule(changeJob.build());

        // Scheduling a periodic job again makes it run at once, so only add it when missing.
        if (scheduler.getPendingJob(PERIODIC_JOB_ID) != null) {
            return;
        }
        JobInfo periodicJob = new JobInfo.Builder(PERIODIC_JOB_ID, service)
            .setPeriodic(PERIODIC_INTERVAL_MILLIS)
            .setPersisted(true)
            .build();
        scheduler.schedule(periodicJob);
    }
}
