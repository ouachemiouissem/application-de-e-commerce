package com.aliwis.bytebye.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliwis.bytebye.databinding.RegisterBinding;
import com.aliwis.bytebye.model.UserModel;
import com.aliwis.bytebye.ui.client.ClientHome;
import com.aliwis.bytebye.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ClientRegister extends AppCompatActivity {

    RegisterBinding binding;

    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initVariables();
        binding.registerButton.setOnClickListener(view -> createAnAccount());
        binding.goToLogin.setOnClickListener(view -> {
            Intent i = new Intent(ClientRegister.this, ClientLogin.class);
            startActivity(i);
        });
        binding.checkbox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                binding.registerPassword.setTransformationMethod(null);
            } else {
                binding.registerPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
    }

    private void createAnAccount() {
        String email = binding.registerEmail.getText().toString();
        String password = binding.registerPassword.getText().toString();
        String username = binding.registerUsername.getText().toString();
        String role = "client";
        if (checkEmail()) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (checkUsername(username)) {
                    if (checkPassword(password)) {
                        UserModel user = new UserModel(username, email, role);
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // account created successfully !!
                                        saveUserDataInDB(user);
                                    } else {
                                        //error occured (email already exist ,internet , wrong format of email , weak password)...
                                        Toast.makeText(ClientRegister.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        } else {
            binding.registerEmail.setError("Écrivez votre adresse e-mail correctement");
        }
    }


    private void saveUserDataInDB(UserModel user) {
        usersRef.child(mAuth.getCurrentUser().getUid())
                .setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //data has been written into the database successfully
                        Intent i = new Intent(ClientRegister.this, ClientHome.class);
                        startActivity(i);
                    } else {
                        // error occured (wrong format of data ,permission , internet connetion...etc)
                        Toast.makeText(ClientRegister.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initVariables() {
        //Firebase auth init
        mAuth = FirebaseAuth.getInstance();
        //Database ref init
        usersRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference().child("Users");
    }

    private boolean checkEmail() {
        if (binding.registerEmail.getText().toString().isEmpty()) {
            binding.registerEmail.setError("Veuillez ne pas laisser le champ de l'e-mail vide");
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPassword(String pwd) {
        boolean hasLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialChar = false;
        if (binding.registerPassword.getText().toString().isEmpty()) {
            binding.registerPassword.setError("Veuillez remplir le champ de mot de passe vide");
            return false;
        }

        if (pwd.length() <= 8) {
            binding.registerPassword.setError("la longueur du mot de passe doit étre supérieur a 8");
            return false;
        }


        // Vérifie si le mot de passe contient au moins une lettre, un chiffre et un caractère spécial
        for (int i = 0; i < pwd.length(); i++) {
            char c = pwd.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }
        // Affiche des erreurs pour les critères manquants
        if (!hasLetter && !hasNumber && !hasSpecialChar) {
            binding.registerPassword.setError("Le mot de passe doit contenir au moins une lettre et un chiffre et un caractère spécial.");
        } else if (!hasNumber && !hasSpecialChar) {
            binding.registerPassword.setError("Le mot de passe doit contenir au moins un chiffre et un caractère spécial.");
        }
        if (!hasLetter && !hasSpecialChar) {
            binding.registerPassword.setError("Le mot de passe doit contenir au moins une lettre et un caractère spécial.");
        } else if (!hasLetter && !hasNumber) {
            binding.registerPassword.setError("Le mot de passe doit contenir au moins une lettre et un chiffre.");
        } else if (!hasLetter) {
            binding.registerPassword.setError("Le mot de passe doit contenir au moins une lettre.");
        } else if (!hasNumber) {
            binding.registerPassword.setError("Le mot de passe doit contenir au moins un chiffre.");
        } else if (!hasSpecialChar) {
            binding.registerPassword.setError("Le mot de passe doit contenir au moins un caractère spécial.");
        }        // Le mot de passe est valide s'il passe toutes les vérifications ci-dessus
        return hasLetter && hasNumber && hasSpecialChar;
    }

    private boolean checkUsername(String user_name) {
        if (user_name.equals("")) {
            binding.registerUsername.setError("Veuillez remplir le champ du nom d'utilisateur");
            return false;
        }
        return true;
    }
}

