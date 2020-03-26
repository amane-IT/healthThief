package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        // 하루동안 섭취한 탄/단/지를 원그래프로 확인
        // 부족한 영양소를 setText 로 알려줌 (탄단지 비율 5:3:2)

        TextView advice = (TextView) findViewById(R.id.giveAdvice);
        TextView nutri_advice = (TextView) findViewById(R.id.giveAdvice_nut);

        SimpleDateFormat format = new SimpleDateFormat ("yyyyMMdd");
        Calendar time = Calendar.getInstance();
        String date = format.format(time.getTime());

        DbHelper DBHelper = new DbHelper(this,"TEST",null,DbHelper.DB_VERSION);
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(" SELECT CARBO, PROTEIN, FAT FROM FOODIARYDB WHERE DATE ="+date,null);
        float carbon = 0;
        float protein =0;
        float fat = 0;
        int total_cal = 0;

        if(cursor.getCount()>0){
            // TODO : 섭취 탄단지 합계 구하기
            while(cursor.moveToNext()){
                carbon += Float.valueOf(cursor.getString(0));
                protein += Float.valueOf(cursor.getString(1));
                fat += Float.valueOf(cursor.getString(2));
                total_cal = (int)carbon*4 + (int)protein*4 + (int)fat*9;
            }
            // TODO : 부족한 영양소 찾기 + 칼로리 섭취량 확인
            Cursor info_c = db.rawQuery("SELECT WEIGHT, SCAL FROM INFODB",null);
            info_c.moveToFirst();
            int weight = info_c.getInt(0);
            int scal = info_c.getInt(1);

            // 1. 섭취 칼로리 과다/부족 확인 - 탄단지 열량 합산 & info 디비의 scal 과 비교 - 추후 범위 수정
            String calString = String.valueOf(total_cal);
            String lessAdvice = calString+"kcal : 더 마니 머겅" ;
            String muchAdvice = calString+"kcal : 너무 마니 머거슴";
            if (scal>total_cal) advice.setText(lessAdvice);
            else advice.setText(muchAdvice);

            // 2. 탄단지 맞춰 섭취했는지 확인 - scal 이용 이상적인 탄단지 섭취량 확인 & 비교 - 추후 범위 수정
            int proper_c = (int)(scal*0.5/4);
            int proper_p = (int)(scal*0.3/4);
            int proper_f = (int)(scal*0.2/9);
            String cadvice = "";
            String padvice = "";
            String fadvice = "";
            if (proper_c >= carbon)  cadvice = "과다"; else cadvice = "부족";
            if (proper_p >= protein) padvice = "과다"; else padvice = "부족";
            if (proper_f >= fat) fadvice = "과다"; else fadvice = "부족";
            String nutri_total_advice = "탄수화물: " + cadvice +  " 단백질: " + padvice + "지방:"+fadvice;
            nutri_advice.setText(nutri_total_advice);
        }
        else{
            carbon = 20f;
            protein = 20f;
            fat = 10f;
            advice.setText("먹은 게 없엉");
            nutri_advice.setText("탄단지도 없엉");

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
