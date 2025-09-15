package com.aliwis.bytebye.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.interfaces.OnProductClicked;
import com.aliwis.bytebye.model.ProductModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserProductAdapter extends RecyclerView.Adapter<UserProductAdapter.GridProductHolder> {

    private final Context context;
    private final OnProductClicked onProductClicked;
    private ArrayList<ProductModel> productsList;

    public UserProductAdapter(Context context, ArrayList<ProductModel> productsList, OnProductClicked onProductClicked) {
        this.context = context;
        this.productsList = productsList;
        this.onProductClicked = onProductClicked;
    }

    @NonNull
    @Override
    public GridProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_vertical, parent, false);
        return new GridProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridProductHolder holder, @SuppressLint("RecyclerView") final int position) {
        ProductModel productModel = productsList.get(position);
        holder.bind(productModel);

        holder.itemView.setOnClickListener(view -> onProductClicked.showDetails(productModel));
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public void updateList(ArrayList<ProductModel> productsList) {
        this.productsList = productsList;
        notifyDataSetChanged();
    }

    class GridProductHolder extends RecyclerView.ViewHolder {
        public ImageView product_image;
        public TextView product_name, product_category, product_price, product_description;

        public GridProductHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_image = itemView.findViewById(R.id.product_image);
            product_description = itemView.findViewById(R.id.product_description);
            product_category = itemView.findViewById(R.id.product_category);
            product_price = itemView.findViewById(R.id.product_price);
        }

        void bind(ProductModel productModel) {
            product_name.setText(productModel.getName());
            product_description.setText(productModel.getDescription());
            product_price.setText(String.format("%s DA", productModel.getPrice()));
            Glide.with(context).load(productModel.getImage()).into(product_image);
        }
    }

}
