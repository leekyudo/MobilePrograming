package com.example.kd.mobileprograming;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GalleryAdapter extends BaseAdapter {

    String picturePath;
    ArrayList<String> pictureList;
    File[] pictureFileList;
    private Context mContext;

    public  GalleryAdapter(Context c){
        picturePath=Environment.getExternalStorageDirectory()+"/Capture"; // 디렉토리 경로
        pictureList = new ArrayList<String>();
        pictureFileList = (new File(picturePath).listFiles()); // 디렉토리의 파일목록을 file배열로 변환
        mContext=c;
    }
    @Override
    public int getCount() {
        return pictureFileList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;

        if(view ==null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300,300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(3,3,3,3);
        }
        else{
            imageView =(ImageView)view ;}

            final int pos = position;
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 32; // 이미지 크기 줄임
            Bitmap bmp = BitmapFactory.decodeFile(pictureFileList[pos].getAbsolutePath(), bo); // position에 다른 파일의 절대경로와 옵션을 비트맵에 저장
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 95, 95, true); // 크기 조절
            imageView.setImageBitmap(resized); // 이미지 뷰에 할당

        return imageView;
    }

}
