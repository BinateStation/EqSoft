package rkr.binatestation.eqsoft.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.UserDetailsModel;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (TextInputEditText) findViewById(R.id.AL_username);
        password = (TextInputEditText) findViewById(R.id.AL_password);
    }

    public void settings(View view) {
        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        finish();
    }

    public void login(View view) {
        if (TextUtils.isEmpty(username.getText().toString().trim())) {
            username.setError("Please provide your username.");
        } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
            password.setError("Please provide your password.");
        } else {
            new AsyncTask<String, Void, UserDetailsModel>() {
                @Override
                protected UserDetailsModel doInBackground(String... inputs) {
                    UserDetailsModel userDetailsModelDB = new UserDetailsModel(getBaseContext());
                    userDetailsModelDB.open();
                    UserDetailsModel userDetailsModel = userDetailsModelDB.getRow(inputs[0], inputs[1]);
                    userDetailsModelDB.close();
                    return userDetailsModel;
                }

                @Override
                protected void onPostExecute(UserDetailsModel userDetailsModel) {
                    super.onPostExecute(userDetailsModel);
                    if (userDetailsModel != null) {
                        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit()
                                .putString(Constants.KEY_USER_ID, userDetailsModel.getUserId())
                                .putString(Constants.KEY_USER_NAME, userDetailsModel.getUserName())
                                .putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                                .apply();
                        navigate();
                    } else {
                        Util.showAlert(LoginActivity.this, "Alert", "Username or password mismatch...");
                    }
                }
            }.execute(username.getText().toString().trim(), password.getText().toString().trim());
        }
    }

    private void navigate() {
        startActivity(new Intent(getBaseContext(), SplashActivity.class));
        finish();
    }
}
