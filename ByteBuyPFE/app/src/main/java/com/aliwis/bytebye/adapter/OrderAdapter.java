package com.aliwis.bytebye.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.interfaces.OnOrder;
import com.aliwis.bytebye.model.OrderModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    Context context;
    ArrayList<OrderModel> orderModels;
    Boolean isAdmin = false;

    OnOrder onOrderAdmin;
    OnOrder.User onOrderUser;

    public OrderAdapter(Context context, ArrayList<OrderModel> orderModels, OnOrder.User onOrderUser) {
        this.context = context;
        this.orderModels = orderModels;
        this.onOrderUser = onOrderUser;
    }

    public OrderAdapter(Context context, ArrayList<OrderModel> orderModels, OnOrder onOrderAdmin) {
        this.context = context;
        this.orderModels = orderModels;
        this.isAdmin = true;
        this.onOrderAdmin = onOrderAdmin;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_order, parent, false);
        return new OrderViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderModel orderModel = orderModels.get(position);
        if (isAdmin) {
            holder.acceptRefuse.setVisibility(View.VISIBLE);
        } else {
            holder.acceptRefuse.setVisibility(View.GONE);
        }
        holder.orderFullname.setText(orderModel.getFullName());
        holder.orderPrice.setText(orderModel.getTotalPrice() + " DZD");
        holder.orderCity.setText(orderModel.getWilaya() + ", " + orderModel.getCommune());
        holder.orderAddress.setText(orderModel.getAddress());
        holder.orderPhoneNumber.setText(orderModel.getPhoneNumber());
        Glide.with(context).load(orderModel.getProductModels().get(0).getImage())
                .into(holder.orderPicture);
        if (orderModel.getState() == 0) {
            // waiting order
            holder.orderState.setText("En attente");
            holder.orderState.setTextColor(context.getColor(R.color.orange));
        } else if (orderModel.getState() == 1) {
            // accepted
            holder.orderState.setText("Accepter");
            holder.orderState.setTextColor(context.getColor(R.color.green));
            holder.acceptRefuse.setVisibility(View.GONE);

        } else if (orderModel.getState() == 2) {
            // Refused
            holder.orderState.setText("Refuser");
            holder.orderState.setTextColor(context.getColor(R.color.red));
            holder.acceptRefuse.setVisibility(View.GONE);
        } else {
            // completed
            holder.orderState.setText("Completer");
            holder.orderState.setTextColor(context.getColor(R.color.blue));
            holder.acceptRefuse.setVisibility(View.GONE);
        }

        if (isAdmin) {
            holder.accept.setOnClickListener(view -> onOrderAdmin.onOrderAccepted(orderModel));
            holder.refuse.setOnClickListener(v -> onOrderAdmin.onOrderRefused(orderModel));
            holder.itemView.setOnClickListener(v -> onOrderAdmin.orderDetails(orderModel));
        } else
            holder.itemView.setOnClickListener(v -> onOrderUser.orderDetails(orderModel));
    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    public void updateList(ArrayList<OrderModel> orderModels) {
        this.orderModels = orderModels;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderFullname, orderCity, orderAddress, orderPhoneNumber, orderPrice, orderState;
        public ImageView orderPicture;
        public Button accept, refuse;
        public LinearLayout acceptRefuse;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderFullname = itemView.findViewById(R.id.full_name);
            orderCity = itemView.findViewById(R.id.order_city);
            orderAddress = itemView.findViewById(R.id.order_address);
            orderPhoneNumber = itemView.findViewById(R.id.order_phonenumber);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderState = itemView.findViewById(R.id.order_state);
            orderPicture = itemView.findViewById(R.id.order_img);
            accept = itemView.findViewById(R.id.accept);
            refuse = itemView.findViewById(R.id.refuse);
            acceptRefuse = itemView.findViewById(R.id.accept_refuse);
        }
    }

}