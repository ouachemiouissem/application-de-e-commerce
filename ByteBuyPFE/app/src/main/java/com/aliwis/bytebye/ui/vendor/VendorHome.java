package com.aliwis.bytebye.ui.vendor;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.utils.Constants;
import com.aliwis.bytebye.databinding.AdminHomeBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class VendorHome extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    AdminHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.adminHomeBottomNavBar.setOnItemSelectedListener(this);

        replaceFragment(new AdminHomeFragment());
        refreshAdminToken();
    }

    private void refreshAdminToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    // save user device token into the database
                    DatabaseReference adminRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL)
                            .getReference()
                            .child("Admin")
                            .child("token");
                    adminRef.setValue(token);
                });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminHomeFragmentContainer, fragment)
                .commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.adminHomeBottomBar_Home) {
            replaceFragment(new AdminHomeFragment());
            return true;
        }
        if (item.getItemId() == R.id.adminHomeBottomBar_Product) {
            replaceFragment(new AdminProductFragment());
            return true;
        }
        if (item.getItemId() == R.id.adminHomeBottomBar_Orders) {
            replaceFragment(new AdminOrdersFragment());
            return true;
        }
        return false;
    }

}

