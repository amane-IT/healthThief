package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class getMonthly extends AppCompatActivity {

    getMonthly gm = this;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_month);

        // 전 화면으로 돌아감
        back = (Button) findViewById(R.id.monthBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(gm,MainActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentHome);
                finish();
            }
        });

        //   https://github.com/Applandeo/Material-Calendar-View

        // 다이어리db에서 날짜 기반 데이터(1달) 가져와 칼로리 및 영양소 분석
        // 특정 날짜에 컬러색 칠하는 함수 만들기... 각 컬러 갯수 return (int형)
        // 맨 위에 색깔별 갯수 알려주기..

        //클릭하면 그 날짜에 먹은 메뉴? 다이어리 팝업? 고민 중...

    }

}
