package com.zoftino.barcodescanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.io.File;

public class ProductAdditionActivity extends ProductBaseActivity {

    private EditText prdName;
    private EditText prdPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.product_addition);
        super.onCreate(savedInstanceState);


        prdName = findViewById(R.id.product_name);
        prdPrice = findViewById(R.id.product_price);
    }

    public void saveProduct(View v) {
        addProductToDb(createProductObj());
    }

    private Product createProductObj(){
        final Product product = new Product();
        product.setProdId((String)barcodeValue.getText());
        product.setProdName(prdName.getText().toString());

        float price = Float.valueOf(prdPrice.getText().toString());
        product.setPrice(price);

        return product;
    }

    private void addProductToDb(Product product){
        firestoreDB.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        refreshUi();
                        Toast.makeText(ProductAdditionActivity.this,
                                "Product has been added to db",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductAdditionActivity.this,
                                "Product could not be added to db, try again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void refreshUi(){
        prdPrice.setText("");
        prdName.setText("");
        barcodeImage.setImageBitmap(null);
        barcodeValue.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BARCODE && resultCode == RESULT_OK) {
            File imgFile = new  File(barcodeFilePath);
            if(imgFile.exists())            {
                barcodeImage.setImageURI(Uri.fromFile(imgFile));
                Bitmap bitmap = ProductUtil.getUprightImage(barcodeFilePath);
                ProductUtil.setBarcodeValue(bitmap, barcodeValue);
            }
        }
    }
}
