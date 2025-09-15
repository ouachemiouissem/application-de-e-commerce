package com.aliwis.bytebye.interfaces;

import com.aliwis.bytebye.model.ProductModel;

public interface OnProductClicked {
    void showDetails(ProductModel productModel);

    void onEditProduct(ProductModel productModel,int position);
}
