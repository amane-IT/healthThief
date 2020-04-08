package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

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
        chart.invalidate();
        chart.clear();

        // TODO : DB 데이터 가져오기
        // 참고링크 https://blog.naver.com/bho7982/220914947711

        // 오늘 날짜가 포함된 주의 기록을 가져온다
        SimpleDateFormat format = new SimpleDateFormat ("yyyyMMdd");
        Calendar time = Calendar.getInstance();
        time.set(time.DAY_OF_WEEK, time.getFirstDayOfWeek());
        String date = format.format(time.getTime());
        Log.d("DAY START",date);
        DbHelper DBHelper = new DbHelper(this,"TEST",null,DbHelper.DB_VERSION);
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DATE, total(CAL) FROM FOODIARYDB GROUP BY DATE HAVING DATE>=" +date,null);
        List<Entry> calories = new ArrayList<Entry>();
        int cursorCount = cursor.getCount();
        Log.d("cursorCount",String.valueOf(cursorCount));
        cursor.moveToFirst();


        //첫째날 데이터 없고 둘째날 있으면 그래프가 제대로 안 뜨는 문제 발견,,,

        // 일월화수목금토 순으로 데이터 가져오기
        // 데이터가 없는 날짜면 100 으로 처리 - 차후 변경
        int day = 0;
        while(cursorCount!=day){
            if (cursor.getString(0).equals(date)){
                Log.d("GRAPH DATE IS: ",date);
                calories.add(new Entry(day+1,cursor.getFloat(1)));
                time.add(Calendar.DATE,1);
                date = format.format(time.getTime());
                cursor.moveToNext();
                day++;
            }
            else{
                calories.add(new Entry(day+1,0));
                time.add(Calendar.DATE,1);
                date = format.format(time.getTime());
                day++;
            }

            if (day==8) break;
        }


        /*
        List<Entry> calories = new ArrayList<Entry>();
        calories.add(new Entry(1,2));
        calories.add(new Entry(2,1));
        calories.add(new Entry(3,5));
        calories.add(new Entry(4,1));
         */


        chart.setScaleEnabled(false); // 확대 축소 불가능하게

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 표시 위치 아래로
        xAxis.setLabelCount(7,true); // x축 라벨 최대 갯수
        xAxis.setAxisMaximum(7f);
        xAxis.setAxisMinimum(1f);
        xAxis.setDrawGridLines(false); // x축 그리드 삭제

        //IAxisValueFormatter AxisFormat = new AxisFormat();
        //xAxis.setValueFormatter(AxisFormat);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setMaxWidth(40f);
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum(2000f);
        yAxisLeft.setSpaceTop(10f);
        yAxisLeft.setSpaceBottom(10f);
        yAxisLeft.setDrawGridLines(false); // y축 그리드 삭제
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawAxisLine(false);


        Legend legend = chart.getLegend();
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

        chart.setDescription(null);

        LineDataSet dataSet = new LineDataSet(calories, "Weekly Calorie");
        dataSet.setDrawCircles(true);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setCircleColor(Color.GRAY);
        dataSet.setDrawFilled(true); // 그래프 아래 색 채우기
        dataSet.setColor(Color.GRAY);
        dataSet.setFillColor(Color.LTGRAY);
        dataSet.setValueTextSize(10f); // value 폰트 크기
        dataSet.setLineWidth(2f); // 그래프 선 두께

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);


        // 꺾은 선 그래프 클릭시 해당 날짜의 정보(getTrend.java) 가져오기
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                float x = e.getX();
                Log.d("CHART X VALUE",String.valueOf(x));
                x--;
                SimpleDateFormat format = new SimpleDateFormat ("yyyyMMdd");
                Calendar time = Calendar.getInstance();
                time.set(time.DAY_OF_WEEK, time.getFirstDayOfWeek());
                time.add(Calendar.DATE,(int)x);
                String date = format.format(time.getTime());
                Log.d("CHART GET DATE: ",date);

                Bundle args = new Bundle();
                args.putString("date",date);

                getTrend getTrend = new getTrend();
                getTrend.setArguments(args);
                getTrend.show(gg.getSupportFragmentManager(),"tag");
            }

            @Override
            public void onNothingSelected()
            {

            }
        });


    }

    /*
    public class  AxisFormat implements IAxisValueFormatter{
        @Override
        public String getFormattedValue(float value, AxisBase axis){
            Date date = new Date((long)value);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
            return sdf.format(date);
        }
    }
     */



}