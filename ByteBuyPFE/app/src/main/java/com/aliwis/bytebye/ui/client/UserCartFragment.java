package com.aliwis.bytebye.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aliwis.bytebye.adapter.CartAdapter;
import com.aliwis.bytebye.utils.Constants;
import com.aliwis.bytebye.databinding.FragmentUserCartBinding;
import com.aliwis.bytebye.interfaces.OnDeleteProductFromCart;
import com.aliwis.bytebye.model.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserCartFragment extends Fragment implements OnDeleteProductFromCart {

    FragmentUserCartBinding binding;
    DatabaseReference cartRef;
    FirebaseAuth mAuth;
    int totalPrice = 0;
    ArrayList<ProductModel> productsList;
    CartAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        binding = FragmentUserCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null ) {
            initVariables();
            fetchDataFromDB();
            initListeners();
        }
    }

    private void initListeners() {
        binding.checkout.setOnClickListener(view -> {
            Intent i = new Intent(requireActivity(), ConfirmOrderAct.class);
            i.putExtra("products", productsList);
            i.putExtra("totalPrice", totalPrice);
            startActivity(i);
        });
    }

    private void initVariables() {
        productsList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        cartRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL)
                .getReference()
                .child("Cart")
                .child(mAuth.getCurrentUser().getUid());
    }

    private void fetchDataFromDB() {
        // getting data from the database
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !isVisible()) {
                    return;
                }
                for (DataSnapshot oneSnapshot : snapshot.getChildren()) {
                    ProductModel productModel = oneSnapshot.getValue(ProductModel.class);
                    productsList.add(productModel);
                }
                if (productsList.isEmpty()) {
                    binding.emptyLinear.setVisibility(View.VISIBLE);
                    binding.bottom.setVisibility(View.INVISIBLE);
                } else {
                    binding.emptyLinear.setVisibility(View.INVISIBLE);
                    binding.bottom.setVisibility(View.VISIBLE);
                }
                calcPrice();
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity().getApplicationContext());
        adapter = new CartAdapter(getActivity().getApplicationContext(), productsList, this ,true);
        binding.cartItems.setAdapter(adapter);
        binding.cartItems.setLayoutManager(manager);
    }

    private void calcPrice() {
        totalPrice = 0;
        for (ProductModel productModel : productsList) {
            totalPrice += productModel.getPrice() * productModel.getQuantity();
        }
        updateTotalPrice(totalPrice);
    }


    @Override
    public void onDeleteProductFromCart(ProductModel productModel, int position) {
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(requireActivity());
        mbuilder.setTitle("Delete " + productModel.getName());
        mbuilder.setMessage("Do you really want to delete " + productModel.getName() + " ?");
        mbuilder.setPositiveButton("Yes", (dialogInterface, i) -> cartRef
                .child(productModel
                        .getProductID())
                .removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Product deleted", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();

                        productsList.remove(productModel);
                        adapter.updateList(productsList, position);

                        totalPrice -= (productModel.getPrice() * productModel.getQuantity());
                        updateTotalPrice(totalPrice);
                    } else {
                        Toast.makeText(requireActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                }));
        mbuilder.setNegativeButton("No", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        mbuilder.show();
    }

    private void updateTotalPrice(int totalPrice) {
        binding.cartPrice.setText(totalPrice + " DZD");
    }
}