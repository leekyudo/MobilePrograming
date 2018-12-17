package com.example.kd.mobileprograming;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;


public class Gallery extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        getWindow().getDecorView().setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |

                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );

        final GalleryAdapter galleryAdapter = new GalleryAdapter(this);


        Button button = (Button)findViewById(R.id.bt_apply);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<6; i++) { //
                    Switch last = (Switch) findViewById(R.id.timeAsending);
                    Switch place = (Switch) findViewById(R.id.place);

                    CheckBox ch_char = (CheckBox) findViewById(R.id.ch_char);
                    CheckBox ch_docu = (CheckBox) findViewById(R.id.ch_docu);
                    CheckBox ch_food = (CheckBox) findViewById(R.id.ch_food);
                    CheckBox ch_othe = (CheckBox) findViewById(R.id.ch_othe);
                    CheckBox ch_view = (CheckBox) findViewById(R.id.ch_view);
                    String charator = (ch_char.isChecked()) ? "char" : "xxxx";
                    String documents = (ch_docu.isChecked()) ? "docu" : "xxxx";
                    String food = (ch_food.isChecked()) ? "food" : "xxxx";
                    String others = (ch_othe.isChecked()) ? "othe" : "xxxx";
                    String view = (ch_view.isChecked()) ? "view" : "xxxx";

                    galleryAdapter.setSortingTag(charator, documents, food, others, view);
                    galleryAdapter.setSortingbyDate(last.isChecked());

                    galleryAdapter.setSortingbyPlace();

                    GridView gridView = (GridView) findViewById(R.id.GridView);

                    gridView.setAdapter(galleryAdapter);

                }
            }
        });


        GridView gridView = (GridView) findViewById(R.id.GridView);
        gridView.setAdapter(new GalleryAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { // 각 이미지 클릭시 동작
                galleryAdapter.clickImageViewer(i);

            }
        });
    }




    public class GalleryAdapter extends BaseAdapter {

        private String picturePath;
        private ArrayList<String> pictureList;
        private File[] pictureFileList;
        private Context mContext;

        public int COMPARETYPE_DATE = 0;
        private int diff=0;

        public void setSortingbyPlace(){
            String exifAttribute;




            for(int i=0;i<this.pictureFileList.length;i++){
                try {
                    String pathtemp = picturePath+"/"+this.pictureFileList[i].getName();
                    ExifInterface exif = new ExifInterface(pathtemp);
                    Gps_Time loc = new Gps_Time();

                    exifAttribute = getExif(loc.readGeoTagImage(pathtemp));
                    Log.e("file : ", getExif(loc.readGeoTagImage(pathtemp)));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("file : ", "nothing");
                }

            }


        }

        private String getExif(Location loc) {
            String myAttribute = "";
            double lat;
            double lng;
            lat=loc.getLatitude();
            lng=loc.getLongitude();
            myAttribute=getAddress(lat,lng);
            return myAttribute;
        }
        private String getTagString(String tag, ExifInterface exif) {
            return (tag + " : " + exif.getAttribute(tag) + "\n");
        }

        public String getAddress(double lat, double lng){
            String address = null;

            //위치정보를 활용하기 위한 구글 API 객체
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            //주소 목록을 담기 위한 HashMap
            List<Address> list = null;

            try{
                list = geocoder.getFromLocation(lat, lng, 1);
            } catch(Exception e){
                e.printStackTrace();
            }

            if(list == null){
                Log.e("getAddress", "주소 데이터 얻기 실패");
                return null;
            }

            if(list.size() > 0){
                Address addr = list.get(0);
                address =  addr.getLocality() + " "
                        + addr.getThoroughfare();
            }

            return address;



        }



        public GalleryAdapter(Context c) {
            picturePath = Environment.getExternalStorageDirectory() + "/Capture"; // 디렉토리 경로
            pictureList = new ArrayList<String>();
            pictureFileList = (new File(picturePath).listFiles()); // 디렉토리의 파일목록을 file배열로 변환
            mContext = c;

        }

        public void setSortingbyDate(Boolean bool){

            for(int i=0; i<pictureFileList.length;i++){
                pictureFileList=sortFileList(pictureFileList,COMPARETYPE_DATE);
            }

            if(bool==false){
                File[] temp=new File[diff];
                for(int i=0;i<diff;i++){
                    temp[i]=pictureFileList[diff-1-i];
                }
                for(int i =0; i<diff;i++) {
                    pictureFileList[i]=temp[i];
                }

            }

        }


        public File[] sortFileList(File[] files, final int compareType)
        {

            Arrays.sort(files,
                    new Comparator<Object>()
                    {
                        @Override
                        public int compare(Object object1, Object object2) {

                            String s1 = "";
                            String s2 = "";
                            if(compareType == COMPARETYPE_DATE) {

                                s1 = ((File) object1).getName();
                                s2 = ((File) object2).getName();

                            }
                            return s1.compareTo(s2);

                        }
                    });

            return files;
        }


        public void setSortingTag(String charator,String documents,String food,String others,String view){

            File[] pictureFileList = (new File(picturePath).listFiles()); // 정렬을 위한 파일 배열 생성
            picturePath = Environment.getExternalStorageDirectory() + "/Capture"; //
            File[] files = new File(picturePath).listFiles();
            String compare;
            Vector<File> vector = new Vector<File>(0); // 태그 sort한 결과를 벡터에 전달
            int j=0;
            for(File file:files){

                compare=file.getName();
                if(CompareString(compare,charator)||CompareString(compare,documents)||CompareString(compare,food)||CompareString(compare,others)||CompareString(compare,view)){
                    vector.add(file);
                }
                else{
                    j++;
                }
            }

            this.diff=vector.size();
            File[] temp= new File[vector.size()];
            temp=(File[])vector.toArray(pictureFileList); // 배열로 전환
            for(int i=0;i<diff;i++){
                this.pictureFileList[i]=temp[i];
            }


    }

        public boolean CompareString(String a, String b){
            int flag=0;
            for(int i=12;i<16;i++){
                if(a.charAt(i)==b.charAt(i-12)){
                    flag++;
                }
            }
            return (flag==4)?true:false;
        }

        public void setPictureFileList(File[] pictureFileList) {
            this.pictureFileList = pictureFileList;
        }




        public final void clickImageViewer(int index) {
            Intent intent = new Intent(mContext, ClickPicture.class);
            String imagePath = pictureFileList[index].getAbsolutePath();
            Log.e("이미지 경로 : ",imagePath);
            intent.putExtra("imagePath", imagePath);

            startActivity(intent);
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

            if (view == null) {

                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(3, 3, 3, 3);
            } else {
                imageView = (ImageView) view;
            }


            final int pos = position;
            if(pos>=diff){
                return imageView;
            }
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 8; // 이미지 크기 줄임
            Bitmap bmp = BitmapFactory.decodeFile(pictureFileList[pos].getAbsolutePath(), bo); // position에 다른 파일의 절대경로와 옵션을 비트맵에 저장
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 95, 95, true); // 크기 조절
            imageView.setImageBitmap(resized); // 이미지 뷰에 할당

            return imageView;
        }


    }
}

