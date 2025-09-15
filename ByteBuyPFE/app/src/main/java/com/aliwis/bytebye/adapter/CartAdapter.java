package com.aliwis.bytebye.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliwis.bytebye.databinding.ItemProductListBinding;
import com.aliwis.bytebye.interfaces.OnDeleteProductFromCart;
import com.aliwis.bytebye.model.ProductModel;
import com.aliwis.bytebye.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ProductViewHolder> {

    Context context;
    ArrayList<ProductModel> productModels;
    DatabaseReference cartRef;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    OnDeleteProductFromCart onDeleteProductFromCart;
    boolean isClient;

    public CartAdapter(Context context, ArrayList<ProductModel> productModels, OnDeleteProductFromCart onDeleteProductFromCart, boolean isClient) {
        this.context = context;
        this.productModels = productModels;
        this.onDeleteProductFromCart = onDeleteProductFromCart;
        if (context != null) {
            cartRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference().child("Cart").child(mAuth.getCurrentUser().getUid());
        }
        this.isClient = isClient;

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                ItemProductListBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductModel productModel = productModels.get(position);
        holder.setData(context, productModel, isClient);
    }

    @Override
    public int getItemCount() {
        return productModels.size();
    }

    public void updateList(ArrayList<ProductModel> productModels, int position) {
        this.productModels = productModels;
        notifyItemRemoved(position);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductListBinding binding;

        public ProductViewHolder(@NonNull ItemProductListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(Context context, ProductModel model, Boolean isClient) {
            if (isClient) {
                binding.editProduct.setVisibility(View.GONE);
            } else {
                binding.editProduct.setVisibility(View.VISIBLE);
            }
            Glide.with(context).load(model.getImage()).into(binding.productImage);
            binding.productName.setText(model.getName());
            binding.productCategory.setText(model.getCategory());
            binding.productPrice.setText((model.getPrice() * model.getQuantity()) + " DZD");
            binding.productDescription.setText(model.getDescription());

            binding.deleteProduct.setOnClickListener(view -> onDeleteProductFromCart.onDeleteProductFromCart(model, getAdapterPosition()));
        }
    }

}
