package com.aliwis.bytebye;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        initVariables();
        /*if (checkIfUserAlreadyLogin()) {
            return;
        }*/
        splashScreen();
    }

    /* private boolean checkIfUserAlreadyLogin() {
       if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
            return true;
        }
       return false;
    }
    */

    private void initVariables() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void splashScreen() {
        new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
        }, 3000);
    }
}