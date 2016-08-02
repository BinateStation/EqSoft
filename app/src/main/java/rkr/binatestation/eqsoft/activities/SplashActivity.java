package rkr.binatestation.eqsoft.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class SplashActivity extends AppCompatActivity {
    private static final String tag = SplashActivity.class.getName();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStoragePermissionGranted()) {
            navigate();
        }
    }

    private void navigate() {
        if (getSharedPreferences(getPackageName(), MODE_PRIVATE).contains(Constants.KEY_SYNC_TYPE)) {
            if (getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
                startActivity(new Intent(getBaseContext(), HomeActivity.class));
                finish();
            } else {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(tag, "Permission is granted");
                return true;
            } else {

                Log.v(tag, "Permission is revoked");
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(tag, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(tag, "Permission: " + permissions[0] + "was " + grantResults[0]);
                navigate();
            } else {
                Util.showAlert(SplashActivity.this, "Alert", "Permission for storage and phone state is just for saving your piclo, please allow it.", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
