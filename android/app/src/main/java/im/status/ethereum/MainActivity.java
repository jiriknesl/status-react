package im.status.ethereum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.facebook.react.ReactActivity;
import com.cboy.rn.splashscreen.SplashScreen;

import java.util.Properties;
import java.util.Random;

public class MainActivity extends ReactActivity {
    private static final String TAG = "MainActivity";

    protected void configureStatus() {
        // Required because of crazy APN settings redirecting localhost (found in GB)
        Properties properties = System.getProperties();
        properties.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
        properties.setProperty("https.nonProxyHosts", "localhost|127.0.0.1");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.show(this);
        super.onCreate(savedInstanceState);

        if (! shouldShowRootedNotification()) {
            configureStatus();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage(getResources().getString(R.string.root_warning))
                    .setPositiveButton(getResources().getString(R.string.root_okay), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            configureStatus();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.root_cancel), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MainActivity.this.finishAffinity();
                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            MainActivity.this.finishAffinity();
                        }
                    })
                    .create();

            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "StatusIm";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent intent = new Intent("onConfigurationChanged");
        intent.putExtra("newConfig", newConfig);
        this.sendBroadcast(intent);
    }


    private boolean shouldShowRootedNotification() {
        boolean firstRun = isFirstRun();
        if (firstRun) {
            removeFirstRun();
        }

        return RootUtil.isDeviceRooted() && firstRun || shouldShowRandomly();
    }

    private boolean isFirstRun() {
        Properties properties = System.getProperties();
        String isFirstRun = properties.getProperty("isFirstRun", "1");
        return isFirstRun == "1";
    }

    private void removeFirstRun() {
        Properties properties = System.getProperties();
        properties.setProperty("isFirstRun", "0");
    }

    private boolean shouldShowRandomly() {
        Random r = new Random();
        return r.nextInt(25) < 1;
    }
}
