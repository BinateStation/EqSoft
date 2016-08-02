package rkr.binatestation.eqsoft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.UserDetailsModel;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserDetailsModel userDetailsTable = new UserDetailsModel(getBaseContext());
        userDetailsTable.open();
        userDetailsTable.insert(new UserDetailsModel(
                "12345",
                "username",
                "password"
        ));
        userDetailsTable.close();
    }

    public void login(View view) {
        startActivity(new Intent(getBaseContext(), HomeActivity.class));
        finish();
    }
}