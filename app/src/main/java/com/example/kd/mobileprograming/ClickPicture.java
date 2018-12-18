package com.example.kd.mobileprograming;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class ClickPicture extends AppCompatActivity {

    ImageView imageView;
    String path;
    @Override
    protected void onCreate (Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.clickimage_layout);

        Intent intent = getIntent();
        path = intent.getStringExtra("imagePath");

            Log.e("전달받은 이미지 경로",path);

        imageView = (ImageView)findViewById(R.id.clickimageView);

        try{
        BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 2; // 이미지 크기 줄임
            Bitmap bitmap = BitmapFactory.decodeFile(path,bo);
            imageView.setImageBitmap(bitmap);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


    }

    
}
