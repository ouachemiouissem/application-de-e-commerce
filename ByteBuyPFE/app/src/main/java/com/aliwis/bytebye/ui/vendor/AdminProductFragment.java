package com.aliwis.bytebye.ui.vendor;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.adapter.ProductAdapter;
import com.aliwis.bytebye.databinding.DialogEditProductBinding;
import com.aliwis.bytebye.databinding.FragmentAdminProductBinding;
import com.aliwis.bytebye.interfaces.OnProductClicked;
import com.aliwis.bytebye.model.ProductModel;
import com.aliwis.bytebye.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;


public class AdminProductFragment extends Fragment implements OnProductClicked {


    FragmentAdminProductBinding binding;
    DatabaseReference productRef;
    ArrayList<ProductModel> productList;

    DialogEditProductBinding editProductBinding;
    Dialog editProductDialog;

    String[] categories;
    ArrayList<String> categoriesArray;
    ProductAdapter adapter;

    ActivityResultLauncher<Intent> addProductLauncher;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        binding = FragmentAdminProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        initListeners();
        getProductsList();
        initEditProductDialog();
    }

    private void initEditProductDialog() {
        editProductBinding = DialogEditProductBinding.inflate(getLayoutInflater());
        editProductDialog = new Dialog(requireActivity());
        editProductDialog.setContentView(editProductBinding.getRoot());
        editProductDialog.getWindow().setBackgroundDrawableResource(R.drawable.edit_text_background);
        editProductDialog.setCancelable(false);
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        adapter = new ProductAdapter(requireActivity(), productList, this, false);
        binding.productsList.setLayoutManager(manager);
        binding.productsList.setAdapter(adapter);
    }

    private void initListeners() {
        binding.addbtn.setOnClickListener(view -> {
            addProductLauncher.launch(new Intent(requireActivity(), AddProduct.class));
        });
    }

    private void initVariables() {
        productList = new ArrayList<>();
        productRef = FirebaseDatabase
                .getInstance(Constants.DATABASE_URL)
                .getReference()
                .child("Products");
        categories = new String[]{"Pc De Bureau", "Gaming", "Développeur", "Étudiant", "Professionnel", "Accesoires"};
        categoriesArray = new ArrayList<>();
        categoriesArray.addAll(Arrays.asList(categories));

        addProductLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    ProductModel model = (ProductModel) result.getData().getSerializableExtra("model");
                    productList.add(model);
                    adapter.notifyItemInserted(productList.size());
                }
            }
        });
    }

    private void getProductsList() {
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !isVisible()) {
                    return;
                }
                for (DataSnapshot oneSnapshot : snapshot.getChildren()) {
                    Object data = oneSnapshot.getValue();
                    if (!(data instanceof String)) {
                        ProductModel productModel = oneSnapshot.getValue(ProductModel.class);
                        productList.add(productModel);
                    }
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void showDetails(ProductModel productModel) {

    }

    @Override
    public void onEditProduct(ProductModel productModel, int position) {
        editProductBinding.editProductName.setText(productModel.getName());
        editProductBinding.editProductDescription.setText(productModel.getDescription());
        editProductBinding.editProductQuantity.setText(productModel.getQuantity() + "");
        editProductBinding.editProductPrice.setText(productModel.getPrice() + "");
        editProductBinding.editProductCategory.setText(productModel.getCategory());

        editProductBinding.editProductCategory.setOnClickListener(view -> chooseCategory(productModel.getCategory()));

        editProductBinding.editSave.setOnClickListener(v -> {
            String productName = editProductBinding.editProductName.getText().toString();
            String productDescription = editProductBinding.editProductDescription.getText().toString();
            String productCategory = editProductBinding.editProductCategory.getText().toString();
            int productPrice = Integer.parseInt(editProductBinding.editProductPrice.getText().toString());
            int productQuantity = Integer.parseInt(editProductBinding.editProductQuantity.getText().toString());

            ProductModel model = new ProductModel(
                    productName,
                    productDescription,
                    productModel.getImage(),
                    productCategory,
                    productModel.getProductID(),
                    productPrice, productQuantity
            );

            productRef.child(productModel.getProductID()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    productList.set(position, model);
                    adapter.updateList(productList);
//                    adapter.updateItem(productModel, position);
                    Toast.makeText(requireActivity(), "Mise a jour réussite", Toast.LENGTH_SHORT).show();
                    editProductDialog.dismiss();
                }
            });
        });

        editProductBinding.editCancel.setOnClickListener(v -> editProductDialog.dismiss());

        editProductDialog.show();
    }

    private void chooseCategory(String category) {
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(requireActivity());
        mbuilder.setTitle("S'il vous plaît, choisissez la catégorie.");
        int index = categoriesArray.indexOf(category);
        mbuilder.setSingleChoiceItems(categories, index, (dialogInterface, position) -> {
            editProductBinding.editProductCategory.setText(categories[position]);
            dialogInterface.dismiss();
        });
        mbuilder.show();
    }
}