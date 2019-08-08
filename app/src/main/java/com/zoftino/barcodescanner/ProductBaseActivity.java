package com.zoftino.barcodescanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;

public class ProductBaseActivity extends AppCompatActivity {

    protected static String BARCODE_IMAGE = "barcodeImage";
    protected static final int REQUEST_BARCODE = 1;
    protected String barcodeFilePath;

    protected ImageView barcodeImage;
    protected TextView barcodeValue;


    protected FirebaseFirestore firestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barcodeImage = findViewById(R.id.barcode_img);
        barcodeValue = findViewById(R.id.barcode_value);

        firestoreDB = FirebaseFirestore.getInstance();
    }

    public void captureBarcodePic(View v) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_BARCODE);

            File barcodeFile = null;
            try {
                barcodeFile = getBarcodeImageFileHolder();
            } catch (IOException ex) {
                Toast.makeText(this, "Please try again.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.zoftino.barcodescanner.fileprovider", barcodeFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, REQUEST_BARCODE);
        }
    }

    protected File getBarcodeImageFileHolder() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(BARCODE_IMAGE, ".jpg", storageDir);
        barcodeFilePath = image.getAbsolutePath();
        return image;
    }
}
