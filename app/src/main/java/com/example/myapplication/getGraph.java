package com.example.myapplication;

import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class getGraph extends AppCompatActivity {

    getGraph gg = this;
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
                Intent intentHome = new Intent(gg,MainActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentHome);
                finish();
            }
    });

    }



    // 일일 총 칼로리 섭취량을 주기별로 선 그래프로 표기
    private void setChart(){

    }


}

