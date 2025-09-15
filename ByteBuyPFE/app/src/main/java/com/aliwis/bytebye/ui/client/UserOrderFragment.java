package com.aliwis.bytebye.ui.client;

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
import com.aliwis.bytebye.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserOrderFragment extends Fragment implements OnOrder.User {

    FragmentAdminOrdersBinding binding;
    DatabaseReference ordersRef;
    FirebaseAuth mAuth;
    ArrayList<OrderModel> orderModels = new ArrayList<>();

    DialogOrderDetailsBinding orderDetailsBinding;
    Dialog detailsDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        binding = FragmentAdminOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        getUserOrders();
        initDetailsDialog();
    }

    private void initDetailsDialog() {
        orderDetailsBinding = DialogOrderDetailsBinding.inflate(getLayoutInflater());
        detailsDialog = new Dialog(requireActivity());
        detailsDialog.setContentView(orderDetailsBinding.getRoot());
        detailsDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
    }

    private void initVariables() {
        mAuth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference().child("Orders");
    }

    private void getUserOrders() {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !isVisible()) {
                    return;
                }
                for (DataSnapshot oneSnapShot : snapshot.getChildren()) {
                    OrderModel orderModel = oneSnapShot.getValue(OrderModel.class);
                    if (orderModel != null && orderModel.getUserId().equals(mAuth.getCurrentUser().getUid()))
                        orderModels.add(0, orderModel);
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        OrderAdapter adapter = new OrderAdapter(requireActivity(), orderModels, this);
        binding.orderList.setLayoutManager(manager);
        binding.orderList.setAdapter(adapter);
    }

    @Override
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
        ProductAdapter adapter = new ProductAdapter(requireActivity(), orderModel.getProductModels(), null,true);
        orderDetailsBinding.productsRecyclerView.setLayoutManager(manager);
        orderDetailsBinding.productsRecyclerView.setAdapter(adapter);

        detailsDialog.show();
    }
}
