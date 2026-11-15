package shivay.animlock;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public final class ApplyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AnimationScales.restore(this);
            AnimationScales.scheduleJobs(this);
            Toast.makeText(this, "Animation speed locked at 0.5x", Toast.LENGTH_SHORT).show();
        } catch (SecurityException missingGrant) {
            Toast.makeText(this,
                "Needs WRITE_SECURE_SETTINGS. Grant it once over adb.",
                Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
