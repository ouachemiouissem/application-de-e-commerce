package com.aliwis.bytebye.interfaces;

import com.aliwis.bytebye.model.ProductModel;

public interface OnDeleteProductFromCart {
    void onDeleteProductFromCart(ProductModel productModel, int position);
}
