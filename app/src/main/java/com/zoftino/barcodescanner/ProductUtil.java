package com.zoftino.barcodescanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.IOException;
import java.util.List;

public class ProductUtil {

    public static Task<List<FirebaseVisionBarcode>>
    readBarcodeValueTask(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image);
        return result;
    }

    public static void setBarcodeValue(Bitmap bitmap, final TextView textView) {
        readBarcodeValueTask(bitmap)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        for (FirebaseVisionBarcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            textView.setText(rawValue);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textView.setText("could not read barcode from image");
                        Log.e("Prod Util read barcode", e.toString());
                    }
                });

    }

    public static Bitmap getUprightImage(String imgUrl) {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgUrl);
        } catch (IOException e) {
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotation = 0;
        switch (orientation) {
            case 3:
                rotation = 180;
                break;
            case 6:
                rotation = 90;
                break;
            case 8:
                rotation = 270;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);

        Bitmap bitmap = BitmapFactory.decodeFile(imgUrl);
        //rotate image
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bitmap;
    }
}
