package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class getMonthly extends AppCompatActivity{

    getMonthly gm = this;
    Button back;
    MaterialCalendarView calendarView;
    ArrayList<Diary> diaries;



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



        // https://github.com/prolificinteractive/material-calendarview
        // https://dpdpwl.tistory.com/3
        // https://github.com/prolificinteractive/material-calendarview/issues/617
        calendarView = findViewById(R.id.calendarView);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2020, 3, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar time = Calendar.getInstance();
        String date = format.format(time.getTime());
        int dateint = Integer.parseInt(date);
        DbHelper DBHelper = new DbHelper(this,"TEST",null,DbHelper.DB_VERSION);


        // TODO : Calendar Click Event
        // 해당 날짜마다 칼로리량에 따른 색깔을 배경색으로 나타내고 총합 칼로리를 날짜 밑에 보여줌
        // 클릭하면 다이얼로그로 탄단지와 먹은 음식을 보여줌 (탄/단/지/아침 뫄/저녁 롸)
        /**
         * 넘겨주고 싶다!! 넘겨주고 싶ㅍ다!!
         */

        // dialog 사이즈 조정
        // https://wimir-dev.tistory.com/11
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        MonthlyDialog mdialog = new MonthlyDialog(gm);
        mdialog.setCancelable(false);
        WindowManager.LayoutParams wm = mdialog.getWindow().getAttributes();
        wm.copyFrom(mdialog.getWindow().getAttributes());
        wm.width = width/2;
        wm.height = height/2;

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // custom dialog : https://mixup.tistory.com/36

                // MonthlyDialog로 해당 날짜의 db 값 넘겨줌
                // https://hyongdoc.tistory.com/184 - arraylist 넘겨주기
                // https://injunech.tistory.com/241

                //위에꺼 안됨... 이걸로 다시 시도
                //https://stackoverflow.com/questions/6124989/pass-data-from-activity-to-dialog/42511882
                int dateToDialog = Integer.parseInt(format.format(date.getDate()));
                diaries = DBHelper.getDiaryDataByDate(dateToDialog);
                Intent it = new Intent(gm, MonthlyDialog.class);
                it.putExtra("diaries", diaries);


                mdialog.show();

            }
        });



        // 다이어리db에서 날짜 기반 데이터(1달) 가져와 칼로리 및 영양소 분석
        // 특정 날짜에 컬러색 칠하는 함수 만들기... 각 컬러 갯수 return (int형)
        // 맨 위에 색깔별 갯수 알려주기..

        //클릭하면 그 날짜에 먹은 메뉴? 다이어리 팝업? 고민 중...

    }
}
