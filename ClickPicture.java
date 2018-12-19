package com.example.kd.mobileprograming;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ClickPicture extends AppCompatActivity {

    ImageView imageView;
    String path;
    static int x;
    @Override
    protected void onCreate (Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.clickimage_layout);

        getWindow().getDecorView().setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |

                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );


        Intent intent = getIntent();
        path = intent.getStringExtra("imagePath");

            Log.e("전달받은 이미지 경로",path);

        imageView = (ImageView)findViewById(R.id.clickimageView);


        try{
            Matrix rotate = new Matrix();
            rotate.postRotate(90);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap rotateBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),rotate,false);
            imageView.setImageBitmap(rotateBmp);

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        String tmp=null;
        try{
            String pathtemp = path;
            ExifInterface exif = new ExifInterface(path);
            Gps_Time loc = new Gps_Time();
            tmp = getExif(loc.readGeoTagImage(pathtemp));

        }catch (IOException e) {
            e.printStackTrace();

        }

        String date="";
        try {
            ExifInterface exif = new ExifInterface(path);
            date += exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
        }



        TextView back = (TextView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        char x =path.charAt(path.length()-8);
        String tag_text;
        switch (x)
        {
            case 'v' : tag_text="배경";
                        break;
            case 'd' : tag_text="문서";
                        break;
            case 'o' : tag_text="기타";
                        break;
            case 'c' : tag_text="인물";
                        break;
            case 'f' : tag_text="음식";
                        break;
            default: tag_text="태그정보 없음";
        }

        TextView adress = (TextView)findViewById(R.id.adress);
        adress.setText("  위치 : "+tmp);

        TextView time = (TextView)findViewById(R.id.date);
        time.setText("  시간 : "+date);

        TextView tag = (TextView)findViewById(R.id.tag);
        tag.setText("  태그 : "+tag_text);

        TextView deletePicture = (TextView)findViewById(R.id.delete);
        deletePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(path);
            }
        });
    }

    public String getExif(Location loc) {
        String myAttribute = "";
        double lat;
        double lng;
        lat=loc.getLatitude();
        lng=loc.getLongitude();
        myAttribute=getAddress(lat,lng);
        return myAttribute;
    }

    public String getAddress(double lat, double lng){
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

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

    // 파일 삭제 기능
    // 경로를 전달받아 파일을 생성한 후 delete함수 이용하여 삭제
    // 삭제 후 갤러리로 돌아가 Adapter다시 적용
    private void delete(String filepath){

        File file = new File(""+filepath);

        if(file.exists()) {
            file.delete();
            Toast.makeText(this, "파일 삭제 성공", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,Gallery.class);
            startActivity(intent);

        }
        else{ Toast.makeText(this,"파일 삭제 성공",Toast.LENGTH_SHORT).show();
        }



    }

}
