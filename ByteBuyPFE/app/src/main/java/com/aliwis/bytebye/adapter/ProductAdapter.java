package com.aliwis.bytebye.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aliwis.bytebye.databinding.ItemProductDetailsBinding;
import com.aliwis.bytebye.databinding.ItemProductListBinding;
import com.aliwis.bytebye.interfaces.OnProductClicked;
import com.aliwis.bytebye.model.ProductModel;
import com.aliwis.bytebye.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DETAILS = 1;
    private static final int VIEW_TYPE_SHOW = 2;
    private final boolean isDetails;
    Context context;
    ArrayList<ProductModel> productList;
    DatabaseReference productsRef;
    OnProductClicked onProductClicked;

    public ProductAdapter(Context context, ArrayList<ProductModel> productList, OnProductClicked onProductClicked, boolean isDetails) {
        this.context = context;
        this.productList = productList;
        this.onProductClicked = onProductClicked;
        this.isDetails = isDetails;

        productsRef = FirebaseDatabase
                .getInstance(Constants.DATABASE_URL)
                .getReference()
                .child("Products");
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DETAILS) {
            return new DetailsProductViewHolder(
                    ItemProductDetailsBinding.inflate(LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ));
        } else {
            return new ProductViewHolder(
                    ItemProductListBinding.inflate(LayoutInflater.from(parent.getContext()),
                            parent,
                            false)
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductModel productModel = (ProductModel) productList.get(position);
        if (getItemViewType(position) == VIEW_TYPE_DETAILS) {
            ((DetailsProductViewHolder) holder).setData(productModel, context);
        } else {
            ((ProductViewHolder) holder).setData(productModel, context);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isDetails) {
            return VIEW_TYPE_DETAILS;
        } else return VIEW_TYPE_SHOW;
    }


    public void updateList(ArrayList<ProductModel> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductListBinding binding;

        public ProductViewHolder(@NonNull ItemProductListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ProductModel model, Context context) {
            binding.productName.setText(model.getName());
            binding.productCategory.setText(model.getCategory());
            binding.productDescription.setText(model.getDescription());
            binding.productPrice.setText(model.getPrice() +" DZD");

            Glide.with(context).load(model.getImage()).into(binding.productImage);
            binding.deleteProduct.setOnClickListener(v -> {
                // delete product
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(context);
                mbuilder.setTitle("Supprimer " + model.getName());
                mbuilder.setMessage("tu veux vraiment supprimer " + model.getName() + " ?");
                mbuilder.setPositiveButton("Oui", (dialogInterface, i) -> productsRef
                        .child(model.getProductID())
                        .removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Produit supprimé", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }));
                mbuilder.setNegativeButton("Non", (dialogInterface, i) -> {
                });
                mbuilder.show();
            });

            binding.editProduct.setOnClickListener(v -> onProductClicked.onEditProduct(model, getAdapterPosition()));
        }
    }

    class DetailsProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductDetailsBinding binding;

        public DetailsProductViewHolder(@NonNull ItemProductDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ProductModel model, Context context) {
            binding.productName.setText(model.getName());
            binding.productCategory.setText(model.getCategory());
            binding.productDescription.setText(model.getDescription());
            binding.productPrice.setText(model.getPrice() +" DZD");
            binding.productQuantite.setText(model.getQuantity());
            Glide.with(context).load(model.getImage()).into(binding.productImage);

            binding.deleteProduct.setVisibility(View.GONE);
            binding.editProduct.setVisibility(View.GONE);
        }
    }
}
