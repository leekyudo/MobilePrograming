package com.example.kd.mobileprograming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Select extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);
    }
    public void camera(View view){
        Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
        startActivity(intent);
    }
    public void galary(View view){}
}
