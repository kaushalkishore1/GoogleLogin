package com.coolacharya.googlelogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.coolacharya.googlelogin.googleSignIn.GooglePlusSignInHelper;
import com.coolacharya.googlelogin.googleSignIn.GoogleResponseListener;
import com.coolacharya.googlelogin.managers.SessionManager;
import com.coolacharya.googlelogin.managers.SharedPrefs;
import com.google.android.gms.plus.model.people.Person;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleResponseListener {
    @BindView(R.id.btn_google_plus)
    com.shaishavgandhi.loginbuttons.GooglePlusButton _google_plus;
    private GooglePlusSignInHelper mGHelper;
    private SessionManager session;
    public static String userid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mGHelper = new GooglePlusSignInHelper(this, this);
        _google_plus.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGHelper.disconnectApiClient();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //handle permissions
        mGHelper.onPermissionResult(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //handle results
        mGHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_google_plus:
                mGHelper.performSignIn();
                break;
        }

    }

    @Override
    public void onGSignInFail() {
        Toast.makeText(this, "Google sign in failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGSignInSuccess(Person personData) {
        Toast.makeText(this, "Google+ user data: full name name=" + personData.getName() + " user name=" + personData.getId() + " " + personData.getImage().toString(), Toast.LENGTH_SHORT).show();
        SharedPrefs.save(LoginActivity.this, SharedPrefs.Userid, personData.getId());
        SharedPrefs.save(LoginActivity.this, SharedPrefs.Username, personData.getDisplayName());
        SharedPrefs.save(LoginActivity.this, SharedPrefs.Email, personData.getUrl());
        SharedPrefs.save(LoginActivity.this, SharedPrefs.Gender, personData.getGender());
        SharedPrefs.save(LoginActivity.this, SharedPrefs.Profilepic, personData.getImage().toString());
        session.setLogin(true, personData.getId());
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}
