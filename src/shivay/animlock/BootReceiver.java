package shivay.animlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "AnimationLock";

    @Override
    public void onReceive(Context context, Intent intent) {
        int restoredCount = AnimationScales.restore(context);
        Log.i(TAG, intent.getAction() + " restored " + restoredCount + " scale keys");
        AnimationScales.scheduleJobs(context);
    }
}
