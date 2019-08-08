package com.zoftino.barcodescanner;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addProduct(View v){
        Intent intent = new Intent(this, ProductAdditionActivity.class);
        startActivity(intent);
    }

    public void getProduct(View v){
        Intent intent = new Intent(this, ProductReaderActivity.class);
        startActivity(intent);
    }
}
