package com.example.myapplication;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class getMonthly extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_month);


        //   https://github.com/Applandeo/Material-Calendar-View

        // 다이어리db에서 날짜 기반 데이터(1달) 가져와 칼로리 및 영양소 분석
        // 특정 날짜에 컬러색 칠하는 함수 만들기... 각 컬러 갯수 return (int형)
        // 맨 위에 색깔별 갯수 알려주기..

        //클릭하면 그 날짜에 먹은 메뉴? 다이어리 팝업? 고민 중...

    }

}
