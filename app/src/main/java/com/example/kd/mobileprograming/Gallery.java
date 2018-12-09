package com.example.kd.mobileprograming;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


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

        public GalleryAdapter(Context c) {
            picturePath = Environment.getExternalStorageDirectory() + "/Capture"; // 디렉토리 경로
            pictureList = new ArrayList<String>();
            pictureFileList = (new File(picturePath).listFiles()); // 디렉토리의 파일목록을 file배열로 변환
            mContext = c;

        }

        public final void clickImageViewer(int index) {
            Intent intent = new Intent(mContext, ClickPicture.class);
            String imagePath = pictureFileList[index].getAbsolutePath();
            Log.e("?!!!! 경로",imagePath);
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
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 32; // 이미지 크기 줄임
            Bitmap bmp = BitmapFactory.decodeFile(pictureFileList[pos].getAbsolutePath(), bo); // position에 다른 파일의 절대경로와 옵션을 비트맵에 저장
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 95, 95, true); // 크기 조절
            imageView.setImageBitmap(resized); // 이미지 뷰에 할당

            return imageView;
        }


    }
}

