package im.status.ethereum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import com.facebook.react.ReactActivity;
import com.cboy.rn.splashscreen.SplashScreen;

import java.util.Properties;
import java.util.Random;

public class MainActivity extends ReactActivity {
    private static final String TAG = "MainActivity";

    private static final String REJECTED_ROOTED_NOTIFICATION = "rejectedRootedNotification";

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
                    .setNeutralButton(getResources().getString(R.string.root_reject), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showFirstRejectDialogue();
                            showSecondRejectDialogue();
                            rejectRootedNotification();
                            dialog.dismiss();
                            configureStatus();
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

    private void showRejectDialogue(int confirmString) {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
            .setMessage(getResources().getString(confirmString))
            .setPositiveButton(getResources().getString(R.string.root_okay), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
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

    private void showFirstRejectDialogue() {
        showRejectDialogue(R.string.root_reject_first_confirm);
    }

    private void showSecondRejectDialogue() {
        showRejectDialogue(R.string.root_reject_second_confirm);
    }

    private boolean shouldShowRootedNotification() {
        return RootUtil.isDeviceRooted() && ! userRejectedRootedNotification();
    }

    private boolean userRejectedRootedNotification() {
        SharedPreferences preferences = getPreferences(0);
        return preferences.getBoolean(REJECTED_ROOTED_NOTIFICATION, false);
    }

    private void rejectRootedNotification() {
        SharedPreferences preferences = getPreferences(0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(REJECTED_ROOTED_NOTIFICATION, true);
        editor.commit();
    }

}
