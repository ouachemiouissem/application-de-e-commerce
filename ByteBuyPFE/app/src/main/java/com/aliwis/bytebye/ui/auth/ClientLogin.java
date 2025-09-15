package com.aliwis.bytebye.ui.auth;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.databinding.ActivityLoginBinding;
import com.aliwis.bytebye.ui.client.ClientHome;
import com.aliwis.bytebye.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClientLogin extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializationOfFields();

        //login
        binding.loginButton.setOnClickListener(view -> {
            if (checkFields()) {
                signIn();
            }
        });


        //from login to register
        binding.goToRegister.setOnClickListener(view -> {
            Intent i = new Intent(ClientLogin.this, ClientRegister.class);
            startActivity(i);
        });


        //forgot password
        binding.forgotPassword.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ClientLogin.this);
            View dialogView = getLayoutInflater().inflate(R.layout.activity_forgot_password, null);
            EditText forgotpassword_edittext = dialogView.findViewById(R.id.forgotpassword_edit);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialogView.findViewById(R.id.btnReset).setOnClickListener(view1 -> {
                String email = forgotpassword_edittext.getText().toString();
                if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ClientLogin.this, "Entrer votre email", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ClientLogin.this, "Vérifier votre email", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(ClientLogin.this, "Impossible d'envoyer", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(view12 -> dialog.dismiss());
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });

        //show password
        binding.loginCheckbox.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) (compoundButton, isChecked) -> {
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String userId = currentUser.getUid();

                        DatabaseReference usersRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference().child("Users").child(userId);
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String userRole = dataSnapshot.child("role").getValue(String.class);
                                    if (userRole != null && userRole.equals("client")) {
                                        Intent intent = new Intent(ClientLogin.this, ClientHome.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(ClientLogin.this, "adressse mail ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else {
                        // Handle sign-in failures
                        Toast.makeText(ClientLogin.this, "adressse mail ou mot de passe incorrect ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializationOfFields() {
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean checkFields() {
        if (binding.loginEmail.getText().toString().isEmpty()) {
            binding.loginEmail.setError("please fill the email");
            return false;
        } else if (binding.loginPassword.getText().toString().isEmpty()) {
            binding.loginPassword.setError("Please fill the password");
            return false;
        } else {
            return true;
        }
    }

}