package com.aliwis.bytebye.ui.vendor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aliwis.bytebye.databinding.AdminAddProductBinding;
import com.aliwis.bytebye.model.ProductModel;
import com.aliwis.bytebye.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddProduct extends AppCompatActivity {

    AdminAddProductBinding binding;

    Uri imageFile;
    StorageReference productImagesRef;
    DatabaseReference productRef;
    String imageURL;
    String[] categories;
    ProgressDialog loading;
    ActivityResultLauncher<Intent> openGalleryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AdminAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initVariables();
        initListeners();
    }

    private void initListeners() {
        binding.selectImage.setOnClickListener(view -> openGalleryResult.launch(openGallery()));
        binding.addProduct.setOnClickListener(view -> {
            if (checkFields()) {
                uploadImageToGoogleCloud();
            }
        });
        binding.selectCategory.setOnClickListener(view -> {
            AlertDialog.Builder mbuilder = new AlertDialog.Builder(AddProduct.this);
            mbuilder.setTitle("il vous plaît, choisissez la catégorie.");
            mbuilder.setSingleChoiceItems(categories, -1, (dialogInterface, position) -> {
                binding.selectCategory.setText(categories[position]);
                dialogInterface.dismiss();
            });
            mbuilder.show();
        });
    }

    private void initVariables() {
        productRef = FirebaseDatabase
                .getInstance(Constants.DATABASE_URL)
                .getReference()
                .child("Products");
        productImagesRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("Product images");
        categories = new String[]{"Pc De Bureau", "Gaming", "Développeur", "Étudiant", "Professionnel", "Accesoires"};

        loading = new ProgressDialog(this);
        loading.setTitle("Uploading your product");
        loading.setMessage("Please wait while we are adding your product");

        openGalleryResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        if (data != null) {
                            imageFile = data.getData();
                        }
                        binding.selectImage.setImageURI(imageFile);
                    }
                });
    }

    private Intent openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }

    private String generateID() {
        return "image" + System.currentTimeMillis();
    }

    private void saveProductDataIntoDb(ProductModel productModel) {
        productRef.setValue(productModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddProduct.this, "Produit ajoutée", Toast.LENGTH_SHORT).show();
                        Intent returnedValue = new Intent();
                        returnedValue.putExtra("model", productModel);
                        setResult(RESULT_OK, returnedValue);
                        finish();
                    } else {
                        Toast.makeText(AddProduct.this, "Erreur", Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                });
    }

    private boolean checkFields() {
        if (binding.productName.getText().toString().isEmpty()) {
            setError(binding.productName, "Please set product name");
            return false;
        }
        if (binding.productDesc.getText().toString().isEmpty()) {
            setError(binding.productDesc, "Please set description");
            return false;
        }
        if (binding.productPrice.getText().toString().isEmpty()) {
            setError(binding.productPrice, "Please select price");
            return false;
        }
        if (binding.productQuantity.getText().toString().isEmpty()) {
            setError(binding.productQuantity, "Please set quantity");
            return false;
        }
        if (imageFile == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setError(EditText editText, String errorMessage) {
        editText.requestFocus();
        editText.setError(errorMessage);
    }

    private void uploadImageToGoogleCloud() {
        loading.show();
        String id = generateID();
        productImagesRef.child(id + ".jpeg")
                .putFile(imageFile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                            imageURL = uri.toString();
                            productRef = productRef.push(); // Generate a unique key
                            String id1 = productRef.getKey(); // Get the generated key from firebase
                            ProductModel productModel = new ProductModel(
                                    binding.productName.getText().toString(),
                                    binding.productDesc.getText().toString(),
                                    imageURL,
                                    binding.selectCategory.getText().toString(),
                                    id1,
                                    Integer.parseInt(binding.productPrice.getText().toString()),
                                    Integer.parseInt(binding.productQuantity.getText().toString())
                            );
                            saveProductDataIntoDb(productModel);
                        });
                    } else {
                        loading.dismiss();
                        Toast.makeText(AddProduct.this, "Erreur", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}