package com.example.kd.mobileprograming;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class CameraMain extends AppCompatActivity  {
    private TextureView mCameraTextureView;
    private CameraPreview mPreview;


    Activity mainActivity = this;
    ImageView imageView;
    static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        getWindow().getDecorView().setSystemUiVisibility(

                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |

                                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

                mCameraTextureView = (TextureView) findViewById(R.id.textureview);
                mPreview = new CameraPreview(mainActivity, mCameraTextureView);


        Button capture = (Button)findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                mPreview.takePicture();

            }
        });

        Button home = (Button)findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Select.class);
                startActivity(intent);
            }
        });

        Button toGallery = (Button)findViewById(R.id.toGallery);
        toGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Gallery.class);
                startActivity(intent);
            }
        });

    }


    // 카메라 권한 확인후 허용(GRANTED)되었으면 CameraPreview실행
    // 허가되지 않았으면 Toast 띄우고 종료
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            mCameraTextureView = (TextureView) findViewById(R.id.textureview);
                            mPreview = new CameraPreview(mainActivity, mCameraTextureView);
                        } else {
                            Toast.makeText(this,"Should have camera permission to run", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        mPreview.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
    }


}


