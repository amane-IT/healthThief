package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class getTrend extends AppCompatActivity {

    PieChart pieChart;
    getTrend gt =this;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_trend);

        // 전 화면으로 돌아감
        back = (Button) findViewById(R.id.trendBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentHome = new Intent(gt,MainActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentHome);
                finish();
            }
        });
        // TODO : get data from Diary DB
        // 지난 n일 동안 섭취한 칼/탄/단/지를 확인함

        //일단 하루치만... 해봅시다...

        SimpleDateFormat format = new SimpleDateFormat ("yyyyMMdd");
        Calendar time = Calendar.getInstance();
        String date = format.format(time.getTime());
        DbHelper DBHelper = new DbHelper(this,"TEST",null,DbHelper.DB_VERSION);

        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(" SELECT CARBO, PROTEIN, FAT FROM FOODIARYDB WHERE DATE ="+date,null);
        float carbon;
        float protein;
        float fat;

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            carbon = Float.valueOf(cursor.getString(0));
            protein = Float.valueOf(cursor.getString(1));
            fat = Float.valueOf(cursor.getString(2));
        }
        else{
            carbon = 20f;
            protein = 10f;
            fat = 10f;
        }

        /*
        Log.d("Count",String.valueOf(cursor.getCount()));
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            String s="0";
            nameT.setText(cursor.getString(0));
            ageT.setText(cursor.getString(1));
            weightT.setText(cursor.getString(2));
            heightT.setText(cursor.getString(3));
            s = cursor.getString(4);
            if (s=="1") {sexT.setText("남");} else {sexT.setText("여");}
            scalT.setText(cursor.getString(5));
        }

        cursor.close();
         */


        // TODO : making pieChart
        pieChart = (PieChart)findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(carbon,"탄수화물"));
        yValues.add(new PieEntry(protein,"단백질"));
        yValues.add(new PieEntry(fat,"지방"));;

        /*
        Description description = new Description();
        description.setText("세계 국가"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);
         */

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"Nutrient");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);

        // db에서 제일 최근에 먹은 음식 3일분 어치 가져오기
        // db에서  현재 날짜에서 1달 단위로 많이 먹은 음식 가져오기
        // 당일 먹은 음식 탄단지 원그래프로 알려주고 부족한 영양소 알려주기

    }
}
