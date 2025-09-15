package com.aliwis.bytebye.ui.client;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.utils.Constants;
import com.aliwis.bytebye.databinding.ActivityUserHomeBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;


public class ClientHome extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    ActivityUserHomeBinding binding;

    FirebaseUser mUser;

    MenuItem lastMenuItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new UserHomeFragment());

        initVariables();
        refreshToken();
    }

    private void initVariables() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.userHomeBottomNavBar.setOnItemSelectedListener(this);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC);
    }

    private void refreshToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(s -> {
                    // save user device token into the database
                    DatabaseReference UsersRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL)
                            .getReference()
                            .child("Users")
                            .child(mUser.getUid());
                    UsersRef.child("token").setValue(s);
                });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.userHomeFragmentContainer, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item == lastMenuItem) {
            return true;
        }
        lastMenuItem = item;
        if (item.getItemId() == R.id.userHomeBottomBar_Home) {
            replaceFragment(new UserHomeFragment());
            return true;
        }
        if (item.getItemId() == R.id.userHomeBottomBar_Product) {
            replaceFragment(new UserCartFragment());
            return true;
        }
        if (item.getItemId() == R.id.userHomeBottomBar_Orders) {
            replaceFragment(new UserOrderFragment());
            return true;
        }
        if (item.getItemId() == R.id.userHomeBottomBar_Profile) {
            replaceFragment(new UserProfileFragment());
            return true;
        }
        return false;
    }
}