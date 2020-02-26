package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class getGraph extends AppCompatActivity {

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_graph);

        // https://github.com/PhilJay/MPAndroidChart

        // 전 화면으로 돌아감
        back = (Button) findViewById(R.id.graphBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
    });


        }
}

