package com.aliwis.bytebye.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliwis.bytebye.databinding.ActivityVendeurLoginBinding;
import com.aliwis.bytebye.ui.vendor.VendorHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class VendorLogin extends AppCompatActivity {

    ActivityVendeurLoginBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVendeurLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null && Objects.equals(mUser.getEmail(), "vendor@bytebuy.com")) {
            startActivity(new Intent(this, VendorHome.class));
        }
        //login
        binding.loginButton.setOnClickListener(view -> {
            if (checkFields()) {
                signIn();
            }
        });

        //show password
        binding.loginCheckbox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                binding.loginPassword.setTransformationMethod(null);
            } else {
                binding.loginPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });


    }


    private void signIn() {
        String email = binding.loginEmail.getText().toString();
        String password = binding.loginPassword.getText().toString();

        if (!email.equals("vendor@bytebuy.com")) {
            binding.loginEmail.setError("Incorrect email");
            binding.loginEmail.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(VendorLogin.this, VendorHome.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VendorLogin.this, "Password is incorrect ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkFields() {
        if (binding.loginEmail.getText().toString().isEmpty()) {
            binding.loginEmail.setError("Veuillez remplir le champ d'email");
            return false;
        } else if (binding.loginPassword.getText().toString().isEmpty()) {
            binding.loginPassword.setError("Veuillez remplir le champ du mot de passe");
            return false;
        } else {
            return true;
        }
    }

}