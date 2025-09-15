package com.aliwis.bytebye.ui.client;


import static com.aliwis.bytebye.utils.Constants.DATABASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aliwis.bytebye.utils.Constants;
import com.aliwis.bytebye.databinding.ActivityConfirmOrderBinding;
import com.aliwis.bytebye.model.OrderModel;
import com.aliwis.bytebye.model.ProductModel;
import com.aliwis.bytebye.notification.FCMSend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ConfirmOrderAct extends AppCompatActivity {

    ActivityConfirmOrderBinding binding;
    DatabaseReference ordersRef, cartRef, productRef;
    ArrayList<ProductModel> productModels;
    int total_price;
    FirebaseAuth mAuth;
    private boolean alreadySentTheOrder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initVariables();
        initListeners();
        initViews();
    }

    private void initViews() {
        binding.totalPriceDisplay.setText(total_price + " DZD");
    }

    private void initListeners() {
        binding.send.setOnClickListener(view -> {
            if (alreadySentTheOrder) {
                Toast.makeText(ConfirmOrderAct.this, "You have already make the order", Toast.LENGTH_SHORT).show();
            } else {
                sendOrder();
            }
        });
    }

    private void initVariables() {
        productModels = (ArrayList<ProductModel>) getIntent().getSerializableExtra("products");
        mAuth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference().child("Orders");
        productRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("Products");
        cartRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference().child("Cart").child(mAuth.getCurrentUser().getUid());
        total_price = getIntent().getIntExtra("totalPrice", -1);
    }

    private void sendOrder() {
        String id = ordersRef.push().getKey();
        OrderModel orderModel = new OrderModel(
                productModels,
                binding.fullName.getText().toString(),
                binding.address.getText().toString(),
                binding.phoneNumber.getText().toString(),
                binding.wilaya.getText().toString(),
                binding.commune.getText().toString(),
                mAuth.getCurrentUser().getUid(),
                total_price,
                0,
                id);

        assert id != null;
        ordersRef.child(id).setValue(orderModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cartRef.removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        alreadySentTheOrder = true;
                        sendNotification();
                        Toast.makeText(ConfirmOrderAct.this, "Order recieved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ConfirmOrderAct.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (ProductModel productModel : orderModel.getProductModels()) {
                    int quantity = snapshot.child(productModel.getProductID()).child("quantity").getValue(Integer.class);
                    snapshot.child(productModel.getProductID()).child("quantity").getRef().setValue((quantity - productModel.getQuantity()));
                }

                startActivity(new Intent(ConfirmOrderAct.this, ClientHome.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification() {
        DatabaseReference adminRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference().child("Admin").child("token");

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //admin token device
                String token = Objects.requireNonNull(snapshot.getValue()).toString();
                FCMSend.pushNotification(getApplicationContext(), token, "Nouvelle commande", "tas Reçu une Nouvelle commande  !", null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}