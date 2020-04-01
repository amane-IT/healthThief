package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        LineChart chart = (LineChart) findViewById(R.id.chart);
        // TODO : DB 데이터 가져오기
        // 참고링크 https://blog.naver.com/bho7982/220914947711


        SimpleDateFormat format = new SimpleDateFormat ("MM/dd");
        Calendar time = Calendar.getInstance();
        String date = format.format(time.getTime());
        float datef = Float.parseFloat(date);
        float datef2 = Float.parseFloat(date)-7;
        String date2 = String.valueOf(datef2);
        DbHelper DBHelper = new DbHelper(this,"TEST",null,DbHelper.DB_VERSION);

        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(" SELECT CALORIE FROM FOODIARYDB WHERE DATE BETWEEN"+date2+"AND"+date,null);

        while(cursor.moveToNext()){



        }

        List<Entry> calories = new ArrayList<Entry>();
        calories.add(new Entry(1,2));
        calories.add(new Entry(2,1));
        calories.add(new Entry(3,5));
        calories.add(new Entry(4,1));


        LineDataSet dataSet = new LineDataSet(calories, "Weekly Calorie");
        //dataSet.setColor();
        //dataSet.setValueTextColor();

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    // 일일 총 칼로리 섭취량을 주기별로 선 그래프로 표기
    private void setChart(){

    }

}

