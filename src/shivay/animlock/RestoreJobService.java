package shivay.animlock;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public final class RestoreJobService extends JobService {
    private static final String TAG = "AnimationLock";

    @Override
    public boolean onStartJob(JobParameters params) {
        int restoredCount = AnimationScales.restore(this);
        Log.i(TAG, "job " + params.getJobId() + " restored " + restoredCount + " scale keys");
        AnimationScales.scheduleJobs(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
