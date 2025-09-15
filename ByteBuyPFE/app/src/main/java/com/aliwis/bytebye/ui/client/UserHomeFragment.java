package com.aliwis.bytebye.ui.client;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.adapter.UserProductAdapter;
import com.aliwis.bytebye.interfaces.OnProductClicked;
import com.aliwis.bytebye.model.ProductModel;
import com.aliwis.bytebye.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserHomeFragment extends Fragment implements OnProductClicked {

    com.aliwis.bytebye.databinding.FragmentUserHomeBinding binding;
    DatabaseReference productRef, cartRef;
    ArrayList<ProductModel> productsList = new ArrayList<>();
    ArrayList<ProductModel> searchList = new ArrayList<>();
    String searchText;
    com.aliwis.bytebye.databinding.DialogProductDetailsBinding productDetailsBinding;
    Dialog productDetailsDialog;
    UserProductAdapter adapter;
    List<RelativeLayout> categories;

    int availableQuantity = 0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        binding = com.aliwis.bytebye.databinding.FragmentUserHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();
        fetchDataFromDB();
        initProductDetailsDialog();
        initCategoriesChooseListener();
        new Handler().postDelayed(this::initSearchViewListener, 1500);
    }

    private void initCategoriesChooseListener() {
        View.OnClickListener categoriesClickListener = v -> setCategory(v, v.getContentDescription().toString());
        binding.tous.setOnClickListener(categoriesClickListener);
        binding.pcBureau.setOnClickListener(categoriesClickListener);
        binding.Gaming.setOnClickListener(categoriesClickListener);
        binding.developpeur.setOnClickListener(categoriesClickListener);
        binding.etudiant.setOnClickListener(categoriesClickListener);
        binding.professionnel.setOnClickListener(categoriesClickListener);
        binding.Accesoire.setOnClickListener(categoriesClickListener);
    }

    private void setCategory(View view, String category) {
        for (RelativeLayout relativeLayout : categories) {
            if (relativeLayout.getId() != view.getId()) {
                relativeLayout.setBackgroundTintList(AppCompatResources.getColorStateList(requireActivity(), R.color.white));
            }
        }
        view.setBackgroundTintList(AppCompatResources.getColorStateList(requireActivity(), R.color.royal_of_blue));

        if (category.equals("tous")) {
            adapter.updateList(productsList);
        } else {
            searchList.clear();
            for (ProductModel productModel : productsList) {
                if (productModel.getCategory().equalsIgnoreCase(category)) {
                    searchList.add(productModel);
                }
            }
            adapter.updateList(searchList);
        }
    }

    private void initSearchViewListener() {
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                if (searchText.isEmpty()) {
                    adapter.updateList(productsList);
                } else {
                    searchList.clear();
                    for (ProductModel productModel : productsList) {
                        if (productModel.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            searchList.add(productModel);
                        }
                    }
                    adapter.updateList(searchList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initProductDetailsDialog() {
        productDetailsBinding = com.aliwis.bytebye.databinding.DialogProductDetailsBinding.inflate(getLayoutInflater());
        productDetailsDialog = new Dialog(requireActivity());
        productDetailsDialog.setContentView(productDetailsBinding.getRoot());
        productDetailsDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        productDetailsBinding.plusQuantity.setOnClickListener(view -> {
            int quantity = Integer.parseInt(productDetailsBinding.productQuantity.getText().toString());
            if (quantity == availableQuantity) return;
            productDetailsBinding.productQuantity.setText("" + ++quantity);
        });
        productDetailsBinding.minusQuantity.setOnClickListener(view -> {
            int quantity = Integer.parseInt(productDetailsBinding.productQuantity.getText().toString());
            if (quantity == 1) return;
            productDetailsBinding.productQuantity.setText("" + --quantity);
        });
    }

    private void initVariables() {
        productsList = new ArrayList<>();

        productRef = FirebaseDatabase
                .getInstance(Constants.DATABASE_URL)
                .getReference()
                .child("Products");
        cartRef = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference()
                .child("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        categories = Arrays.asList(
                binding.pcBureau,
                binding.Gaming,
                binding.developpeur,
                binding.etudiant,
                binding.professionnel,
                binding.Accesoire);
    }

    private void fetchDataFromDB() {
        // getting data from the database
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || !isVisible()) {
                    return;
                }
                for (DataSnapshot oneSnapshot : snapshot.getChildren()) {
                    ProductModel productModel = oneSnapshot.getValue(ProductModel.class);
                    if (productModel != null) {
                        productsList.add(productModel);
                    }
                }
                initRecyclerView(productsList);
                setCategory(binding.tous, "tous");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initRecyclerView(ArrayList<ProductModel> productsList) {
        GridLayoutManager manager = new GridLayoutManager(requireActivity(), 2);
        adapter = new UserProductAdapter(requireActivity(), productsList, this);
        binding.productsRecyclerView.setLayoutManager(manager);
        binding.productsRecyclerView.setAdapter(adapter);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC);

    }

    private void addProductToCart(ProductModel productModel) {
        if (productModel.getQuantity() == 0) {
            Toast.makeText(requireActivity(), "Quantity unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        productModel.setQuantity(Integer.parseInt(productDetailsBinding.productQuantity.getText().toString()));

        cartRef.child(productModel.getProductID()).setValue(productModel).addOnCompleteListener((OnCompleteListener<Void>) task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Produit ajouté dans le panier !", Toast.LENGTH_SHORT).show();
                productDetailsDialog.dismiss();
            } else {
                Toast.makeText(requireActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                productDetailsDialog.dismiss();
            }
        });
    }

    @Override
    public void showDetails(ProductModel productModel) {
        productDetailsBinding.productName.setText(productModel.getName());
        productDetailsBinding.descreption.setText(productModel.getDescription());
        productDetailsBinding.productCategory.setText(productModel.getCategory());
        productDetailsBinding.productPrice.setText(productModel.getPrice() + " DZD");
        Glide.with(requireActivity()).load(productModel.getImage()).into(productDetailsBinding.productImage);
        productDetailsBinding.productQuantityAvailable.setText(getResources().getString(R.string.quantityAvailable, productModel.getQuantity()));

        productDetailsBinding.addToCart.setOnClickListener(v -> {
            addProductToCart(productModel);
        });
        productDetailsDialog.show();

        availableQuantity = productModel.getQuantity();
    }

    @Override
    public void onEditProduct(ProductModel productModel, int position) {

    }
}
