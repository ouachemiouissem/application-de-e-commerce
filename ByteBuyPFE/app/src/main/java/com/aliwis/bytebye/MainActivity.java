package com.aliwis.bytebye;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aliwis.bytebye.databinding.HomeBinding;
import com.aliwis.bytebye.ui.auth.ClientLogin;
import com.aliwis.bytebye.ui.auth.ClientRegister;
import com.aliwis.bytebye.ui.auth.VendorLogin;
import com.aliwis.bytebye.ui.client.ClientHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    HomeBinding binding;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initListeners();
        checkIfIsLogin();
    }

    private void checkIfIsLogin() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !Objects.equals(user.getEmail(), "vendor@bytebuy.com")) {
            startActivity(ClientHome.class);
        }
    }

    private void initListeners() {
        binding.registerButton.setOnClickListener(v -> startActivity(VendorLogin.class));

        binding.loginButton.setOnClickListener(v -> startActivity(ClientLogin.class));
    }

    private <T> void startActivity(Class<T> toClass) {
        startActivity(new Intent(MainActivity.this, toClass));
    }

}