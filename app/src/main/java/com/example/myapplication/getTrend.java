package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class getTrend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_trend);




        // db에서 제일 최근에 먹은 음식 3일분 어치 가져오기
        // db에서  현재 날짜에서 1달 단위로 많이 먹은 음식 가져오기
        // db에서 1달치 영양소 평균으로 평가 내려주기 (ex) 단백질이 부족해요! 오늘은 고기 한판...? )

    }
}
