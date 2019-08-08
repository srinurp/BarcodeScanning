package com.zoftino.barcodescanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.io.File;
import java.util.List;

public class ProductReaderActivity extends ProductBaseActivity {

    private TextView prdName;
    private TextView prdPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.product_reader);
        super.onCreate(savedInstanceState);

        prdName = findViewById(R.id.product_name);
        prdPrice = findViewById(R.id.product_price);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BARCODE && resultCode == RESULT_OK) {
            File imgFile = new File(barcodeFilePath);
            if (imgFile.exists()) {
                Bitmap bitmap = ProductUtil.getUprightImage(barcodeFilePath);
                Task<List<FirebaseVisionBarcode>> result =
                        ProductUtil.readBarcodeValueTask(bitmap);
                readProductFromDb(result);
            }
        }
    }

    private void getProduct(String prodId) {
        firestoreDB.collection("products")
                .whereEqualTo("prodId", prodId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                displayProductDetails(document.toObject(Product.class));
                                break;
                            }
                        } else {
                            Toast.makeText(ProductReaderActivity.this,
                                    "Failed to get product data, please try again.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("get product",
                                    "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void displayProductDetails(Product product) {
        prdName.setText(product.getProdName());
        prdPrice.setText(String.valueOf(product.getPrice()));
        barcodeValue.setText(product.getProdId());
    }

    private void readProductFromDb(Task<List<FirebaseVisionBarcode>> result) {
        result.addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                if(barcodes.size() == 0){
                    Toast.makeText(ProductReaderActivity.this,
                            "No barcodes found, please try again.",
                            Toast.LENGTH_SHORT).show();
                }
                for (FirebaseVisionBarcode barcode : barcodes) {
                    String rawValue = barcode.getRawValue();
                    getProduct(rawValue);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductReaderActivity.this,
                                "Can not read barcode, please try again.",
                                Toast.LENGTH_SHORT).show();
                        Log.d("get product",
                                "Error reading barcode: "+ e.toString());
                    }
        });
    }
}
