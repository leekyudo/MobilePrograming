package com.example.kd.mobileprograming;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class Select extends AppCompatActivity {


    ImageView CameraButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);

        getWindow().getDecorView().setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |

                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );


        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
                CameraButton.startAnimation(animation);
            }
        };

        CameraButton = (ImageView) findViewById(R.id.camera);
        timer.schedule(timerTask,0,4000);

    }


    public void camera(View view){
        Intent intent = new Intent(getApplicationContext(),ChooseTag.class);
        startActivity(intent);
        overridePendingTransition(0,R.anim.alpha);

    }
    public void togallery(View view){
        Intent intent = new Intent(getApplicationContext(),Gallery.class);
        startActivity(intent);
    }
}
