package com.aliwis.bytebye.interfaces;

import com.aliwis.bytebye.model.OrderModel;

public interface OnOrder {
    void orderDetails(OrderModel orderModel);

    interface User{
        void orderDetails(OrderModel orderModel);
    }
    void onOrderAccepted(OrderModel orderModel);

    void onOrderRefused(OrderModel orderModel);

}
