package com.aliwis.bytebye.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliwis.bytebye.databinding.FragmentUserProfileBinding;
import com.aliwis.bytebye.ui.auth.ClientLogin;
import com.aliwis.bytebye.utils.Constants;
import com.aliwis.bytebye.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileFragment extends Fragment {

    FragmentUserProfileBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null ) {
            initVariables();
            getUserProfile();
            initListener();
            binding.updateUserInfomations.setOnClickListener(view2 -> {
                update_userInfomations();
            });
        }
    }

    private void initListener() {
        binding.logout.setOnClickListener(view -> {
            mAuth.signOut();
                startActivity(new Intent(requireActivity(), ClientLogin.class));
            });
    }

    private void getUserProfile() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !isVisible()){
                    return;
                }
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (userModel != null) {
                    binding.emailDisplay.setText(userModel.getEmail());
                    binding.usernameDisplay.setText(userModel.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void update_userInfomations() {
        String username = binding.emailDisplay.getText().toString().trim();
        String email = binding.usernameDisplay.getText().toString().trim();

        // Mise à jour des données dans Firebase
        UserModel user = new UserModel(username, email, "client");
        usersRef.setValue(user);
    }

    private void initVariables() {
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL)
                .getReference()
                .child("Users")
                .child(mAuth.getCurrentUser().getUid());
    }
}