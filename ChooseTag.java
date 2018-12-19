package com.example.kd.mobileprograming;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ChooseTag extends AppCompatActivity {

    static String Tag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosetag_layout);

        getWindow().getDecorView().setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |

                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |

                        View.SYSTEM_UI_FLAG_FULLSCREEN |

                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        final Button Char = (Button) findViewById(R.id.Character);
        final Button View_a = (Button) findViewById(R.id.View);
        final Button Docu = (Button) findViewById(R.id.Documents);
        final Button Food = (Button) findViewById(R.id.Food);
        final Button Other = (Button) findViewById(R.id.Others);

        Char.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Char.setBackgroundResource(R.drawable.round_clicktag);
                Tag = "char";
                Intent intent = new Intent(getApplicationContext(), CameraMain.class);
                startActivity(intent);
            }
        });

        View_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View_a.setBackgroundResource(R.drawable.round_clicktag);
                Tag = "view";
                Intent intent = new Intent(getApplicationContext(), CameraMain.class);
                startActivity(intent);
            }
        });

        Docu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Docu.setBackgroundResource(R.drawable.round_clicktag);
                Tag = "docu";
                Intent intent = new Intent(getApplicationContext(), CameraMain.class);
                startActivity(intent);
            }
        });
        Food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food.setBackgroundResource(R.drawable.round_clicktag);
                Tag = "food";
                Intent intent = new Intent(getApplicationContext(), CameraMain.class);
                startActivity(intent);
            }
        });

        Other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Other.setBackgroundResource(R.drawable.round_clicktag);
                Tag = "othe";
                Intent intent = new Intent(getApplicationContext(), CameraMain.class);
                startActivity(intent);
            }
        });
    }


}