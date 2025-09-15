package com.aliwis.bytebye.ui.vendor;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.adapter.OrderAdapter;
import com.aliwis.bytebye.adapter.ProductAdapter;
import com.aliwis.bytebye.databinding.DialogOrderDetailsBinding;
import com.aliwis.bytebye.databinding.FragmentAdminOrdersBinding;
import com.aliwis.bytebye.interfaces.OnOrder;
import com.aliwis.bytebye.model.OrderModel;
import com.aliwis.bytebye.model.ProductModel;
import com.aliwis.bytebye.notification.FCMSend;
import com.aliwis.bytebye.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminOrdersFragment extends Fragment implements OnOrder {


    FragmentAdminOrdersBinding binding;
    DatabaseReference ordersRef, usersRef;
    FirebaseAuth mAuth;
    DialogOrderDetailsBinding orderDetailsBinding;
    Dialog detailsDialog;

    ArrayList<OrderModel> orderModels = new ArrayList<>();
    OrderAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        binding = FragmentAdminOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            initVariables();
            getAdminOrders();
            initDetailsDialog();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        adapter = new OrderAdapter(requireActivity(), orderModels, this);
        binding.orderList.setLayoutManager(manager);
        binding.orderList.setAdapter(adapter);
    }

    private void initVariables() {
        mAuth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL)
                .getReference()
                .child("Orders");
        usersRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL)
                .getReference()
                .child("Users");
    }

    private void getAdminOrders() {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !isVisible()) {
                    return;
                }
                for (DataSnapshot oneSnapShot : snapshot.getChildren()) {
                    OrderModel orderModel = oneSnapShot.getValue(OrderModel.class);
                    if (orderModel != null) orderModels.add(0, orderModel);
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initDetailsDialog() {
        orderDetailsBinding = DialogOrderDetailsBinding.inflate(getLayoutInflater());
        detailsDialog = new Dialog(requireActivity());
        detailsDialog.setContentView(orderDetailsBinding.getRoot());
        detailsDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
    }

    @Override
    public void onOrderAccepted(OrderModel orderModel) {
        ordersRef.child(orderModel.getOrderId()).child("state").setValue(1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // send notification
                sendNotification(orderModel.getUserId(), "Commande accepté",
                        "Votre commande a été accepté merci!");
                int index = orderModels.indexOf(orderModel);
                orderModel.setState(1);
                orderModels.set(index, orderModel);
                adapter.updateList(orderModels);
            }
        });
    }

    @Override
    public void onOrderRefused(OrderModel orderModel) {
        ordersRef.child(orderModel.getOrderId()).child("state").setValue(2).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // send notification
                sendNotification(orderModel.getUserId(), "Commande refusé", "Votre commande a été refusé, merci!");
                int index = orderModels.indexOf(orderModel);
                orderModel.setState(2);
                orderModels.set(index, orderModel);
                adapter.updateList(orderModels);
            }
        });
    }

    public void orderDetails(OrderModel orderModel) {
        orderDetailsBinding.clientName.setText(orderModel.getFullName());
        orderDetailsBinding.clientAdresse.setText(orderModel.getAddress());
        orderDetailsBinding.clientNumphone.setText(orderModel.getPhoneNumber());
        int total = 0;
        for (ProductModel product : orderModel.getProductModels()) {
            total += product.getPrice();
        }

        orderDetailsBinding.productsPrice.setText(total + " DZD");

        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        ProductAdapter adapter = new ProductAdapter(requireActivity(), orderModel.getProductModels(), null, true);
        orderDetailsBinding.productsRecyclerView.setLayoutManager(manager);
        orderDetailsBinding.productsRecyclerView.setAdapter(adapter);

        detailsDialog.show();
    }

    /* private void initOrderDetailsDialog() {
         orderDetailsBinding = com.aliwis.bytebye.databinding.DialogOrderDetailsBinding.inflate(getLayoutInflater());
         orderDetailsBinding = new Dialog(requireActivity());
         orderDetailsBinding.setContentView(orderDetailsBinding.getRoot());
         orderDetailsBinding.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

     }*/
    private void sendNotification(String userId, String title, String message) {
        usersRef.child(userId).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.getValue(String.class);
                FCMSend.pushNotification(requireActivity(), token, title, message, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}