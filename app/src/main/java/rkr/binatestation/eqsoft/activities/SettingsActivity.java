package rkr.binatestation.eqsoft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.network.DataSync;
import rkr.binatestation.eqsoft.utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    AppCompatRadioButton syncByUSB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        syncByUSB = (AppCompatRadioButton) findViewById(R.id.AS_syncByUSB);
    }

    public void actionDone(View view) {
        if (syncByUSB.isChecked()) {
            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(Constants.KEY_SYNC_TYPE, "syncByUsb").apply();
            new DataSync(view.getContext()) {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();
                }
            }.execute(2);
        } else {
            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(Constants.KEY_SYNC_TYPE, "syncByWeb").apply();
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
            finish();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
