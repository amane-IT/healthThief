package com.example.myapplication;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;


public class getMonthly extends AppCompatActivity{

    getMonthly gm = this;
    Button back;
    MaterialCalendarView calendarView;
    ArrayList<Diary> diaries;

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    DbHelper DBHelper = new DbHelper(this,"TEST",null,DbHelper.DB_VERSION);

    HashSet<CalendarDay> green;
    HashSet<CalendarDay> yellow;
    HashSet<CalendarDay> red;
    HashSet<CalendarDay> gray;

    double scal, proper_c, proper_p, proper_f, scal2, proper_c2,proper_p2,proper_f2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_month);

        //infoDB에서 탄단지칼 갖고오기
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor info_c = db.rawQuery("SELECT SCAL FROM INFODB",null);
        info_c.moveToFirst();
        scal = (info_c.getInt(0))*0.9;
        proper_c = (scal*0.5/4)*0.7;
        proper_p = (scal*0.3/4)*0.7;
        proper_f = (scal*0.2/9)*0.7;
        scal2 = (info_c.getInt(0))*1.3;
        proper_c2 = (scal*0.5/4)*1.3;
        proper_p2 = (scal*0.3/4)*1.3;
        proper_f2 = (scal*0.2/9)*1.3;


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
                .setMaximumDate(CalendarDay.from(2020, 3, 30))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendarView.addDecorators(
                new GrayDecorator(),
                new GreenDecorator(),
                new YellowDecorator(),
                new RedDecorator()
        );


        // 해당 날짜마다 칼로리량에 따른 색깔을 배경색으로 나타내고 총합 칼로리를 날짜 밑에 보여줌
        // 클릭하면 다이얼로그로 탄단지와 먹은 음식을 보여줌 (탄/단/지/아침 뫄/저녁 롸)

        // dialog 사이즈 조정
        // https://wimir-dev.tistory.com/11

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

                if(!diaries.isEmpty()){

                    Bundle args = new Bundle();
                    args.putString("date",String.valueOf(dateToDialog));

                    getTrend getTrend = new getTrend();
                    getTrend.setArguments(args);
                    getTrend.show(gm.getSupportFragmentManager(),"tag");

                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(gm);
                    builder.setTitle("Sorry!").setMessage("There is no Data!");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        });
    }

    // 인포 디비에서 권장 칼로리 영양소 섭취량 계산 + 각 값의 플마 10% 값(적정값)을 구함
    // 다이어리 디비에서 1달전~현재까지의 데이터(토탈-칼,토탈-탄,토탈-단,토탈-지,날짜)를 가져온다
    // for문으로 하루씩 더해가면서 날마다 sql문을 돌림
    // CursorCount =0 이면 회색
    //     ''      =1 이면
    // 칼/탄/단/지가 적정값중 4-3개를 충족하면 초록
    //                        2-1개를 충족하면 노랑
    //                          0개를 충족하면 빨강






    // Calendar Background Custom Decorate
    public class RedDecorator implements DayViewDecorator {

        private final Drawable drawable;
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Calendar stime = Calendar.getInstance();
        Calendar etime = Calendar.getInstance();
        String sdate, edate;
        TextView red_tv;
        double kcal, carbo, protein, fat;

        public RedDecorator() {
            drawable = new ColorDrawable(0xffff6666);
            red = new HashSet<CalendarDay>();

            stime.set(stime.DAY_OF_MONTH, 1);
            etime.set(Calendar.DATE,etime.getActualMaximum(Calendar.DATE));
            etime.add(Calendar.DATE,1);
            sdate = format.format(stime.getTime());
            edate = format.format(etime.getTime());
            red_tv = (TextView) findViewById(R.id.month_red);

            while(!sdate.equals(edate)) {
                //Log.d("DATE CALLING IS: " ,sdate);
                Cursor cursor = db.rawQuery("SELECT DATE FROM FOODIARYDB WHERE DATE=" + sdate, null);
                //Log.d("SIZE OF CURSOR: ", String.valueOf(cursor.getCount()));

                if (cursor.getCount()!=0){

                    Cursor c = db.rawQuery("SELECT DATE, total(CAL), total(CARBO), total(PROTEIN), total(FAT) FROM FOODIARYDB WHERE DATE="+sdate,null);
                    c.moveToFirst();
                    kcal = c.getDouble(1);
                    carbo = c.getDouble(2);
                    protein = c.getDouble(3);
                    fat = c.getDouble(4);
                    int r = checkTF(kcal,carbo,protein,fat);
                    //Log.d("RED R SIZE: ",String.valueOf(r));

                    if(r==0){
                        CalendarDay hday = CalendarDay.from(stime);
                        red.add(hday);
                        //Log.d("HASHSET ADD: ","SUCCESS");
                    }

                }

                stime.add(Calendar.DATE,1);
                sdate = format.format(stime.getTime());
                cursor.close();
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            int a = red.size();
            red_tv.setText(String.valueOf(a));
            Log.d("RED SIZE OF HASHSET: ",String.valueOf(a));
            return red.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            // change Background
            view.setBackgroundDrawable(drawable);
        }
    }

    public class GreenDecorator implements DayViewDecorator {

        private final Drawable drawable;
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Calendar stime = Calendar.getInstance();
        Calendar etime = Calendar.getInstance();
        String sdate, edate;
        TextView green_tv;
        double kcal, carbo, protein, fat;

        public GreenDecorator() {
            drawable = new ColorDrawable(0xff4aa02c);
            green = new HashSet<CalendarDay>();

            stime.set(stime.DAY_OF_MONTH, 1);
            etime.set(Calendar.DATE,etime.getActualMaximum(Calendar.DATE));
            etime.add(Calendar.DATE,1);
            sdate = format.format(stime.getTime());
            edate = format.format(etime.getTime());
            green_tv = (TextView) findViewById(R.id.month_green);

            while(!sdate.equals(edate)) {
                //Log.d("DATE CALLING IS: " ,sdate);
                Cursor cursor = db.rawQuery("SELECT DATE FROM FOODIARYDB WHERE DATE=" + sdate, null);
                //Log.d("SIZE OF CURSOR: ", String.valueOf(cursor.getCount()));

                if (cursor.getCount()!=0){

                    Cursor c = db.rawQuery("SELECT DATE, total(CAL), total(CARBO), total(PROTEIN), total(FAT) FROM FOODIARYDB WHERE DATE="+sdate,null);
                    c.moveToFirst();
                    kcal = c.getDouble(1);
                    carbo = c.getDouble(2);
                    protein = c.getDouble(3);
                    fat = c.getDouble(4);
                    int r = checkTF(kcal,carbo,protein,fat);
                    Log.d("GREEN R SIZE: ",String.valueOf(r));

                    if(r==3 || r==4){
                        CalendarDay hday = CalendarDay.from(stime);
                        green.add(hday);
                        //Log.d("HASHSET ADD: ","SUCCESS");
                    }

                }

                stime.add(Calendar.DATE,1);
                sdate = format.format(stime.getTime());
                cursor.close();
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            int a = green.size();
            green_tv.setText(String.valueOf(a));
            Log.d("GREEN SIZE OF HASHSET: ",String.valueOf(a));
            return green.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(drawable);
        }
    }

    public class YellowDecorator implements DayViewDecorator {

        private final Drawable drawable;
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Calendar stime = Calendar.getInstance();
        Calendar etime = Calendar.getInstance();
        String sdate, edate;
        TextView yellow_tv;
        double kcal, carbo, protein, fat;

        public YellowDecorator() {
            drawable = new ColorDrawable(0xfffff380);
            yellow = new HashSet<CalendarDay>();

            stime.set(stime.DAY_OF_MONTH, 1);
            etime.set(Calendar.DATE,etime.getActualMaximum(Calendar.DATE));
            etime.add(Calendar.DATE,1);
            sdate = format.format(stime.getTime());
            edate = format.format(etime.getTime());
            yellow_tv = (TextView) findViewById(R.id.month_yellow);

            while(!sdate.equals(edate)) {
                //Log.d("DATE CALLING IS: " ,sdate);
                Cursor cursor = db.rawQuery("SELECT DATE FROM FOODIARYDB WHERE DATE=" + sdate, null);
                //Log.d("SIZE OF CURSOR: ", String.valueOf(cursor.getCount()));

                if (cursor.getCount()!=0){

                   Cursor c = db.rawQuery("SELECT DATE, total(CAL), total(CARBO), total(PROTEIN), total(FAT) FROM FOODIARYDB WHERE DATE="+sdate,null);
                   c.moveToFirst();
                   kcal = c.getDouble(1);
                   carbo = c.getDouble(2);
                   protein = c.getDouble(3);
                   fat = c.getDouble(4);
                   int r = checkTF(kcal,carbo,protein,fat);
                   //Log.d("YELLOW R SIZE: ",String.valueOf(r));

                   if(r==1 || r==2){
                                      CalendarDay hday = CalendarDay.from(stime);
                    yellow.add(hday);
                    //Log.d("HASHSET ADD: ","SUCCESS");
                   }

                }

                stime.add(Calendar.DATE,1);
                sdate = format.format(stime.getTime());
                cursor.close();
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {

            int a = yellow.size();
            yellow_tv.setText(String.valueOf(a));
            Log.d("Y SIZE OF HASHSET: ",String.valueOf(a));
            return yellow.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(drawable);
        }
    }

    public class GrayDecorator implements DayViewDecorator {


        private final Drawable drawable;
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Calendar stime = Calendar.getInstance();
        Calendar etime = Calendar.getInstance();
        String sdate, edate;
        TextView gray_tv;

        public GrayDecorator() {
            drawable = new ColorDrawable(0xffbebebe);
            gray = new HashSet<CalendarDay>();

            stime.set(stime.DAY_OF_MONTH, 1);
            etime.set(Calendar.DATE,etime.getActualMaximum(Calendar.DATE));
            etime.add(Calendar.DATE,1);
            sdate = format.format(stime.getTime());
            edate = format.format(etime.getTime());
            gray_tv = (TextView) findViewById(R.id.month_gray);

            //Log.d("DAY START",sdate);
            //Log.d("DAY END",edate);


            while(!sdate.equals(edate)) {
                //Log.d("DATE CALLING IS: " ,sdate);
                Cursor cursor = db.rawQuery("SELECT DATE FROM FOODIARYDB WHERE DATE=" + sdate, null);
                //Log.d("SIZE OF CURSOR: ", String.valueOf(cursor.getCount()));

                if (cursor.getCount()==0){
                    CalendarDay hday = CalendarDay.from(stime);
                    gray.add(hday);
                    //Log.d("HASHSET ADD: ","SUCCESS");
                }

                stime.add(Calendar.DATE,1);
                sdate = format.format(stime.getTime());
                cursor.close();
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {

            int a = gray.size();
            gray_tv.setText(String.valueOf(a));
            //Log.d("SIZE OF HASHSET: ",String.valueOf(a));
            return gray.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(drawable);
        }
    }

    public int checkTF(double kk, double cc, double pp, double ff){
        int result=0;

        // 칼 / 탄 / 단 / 지
        if (kk >= scal && kk<= scal2) result++;

        if (cc >= proper_c && cc<= proper_c2) result++;

        if (pp >= proper_p && pp<= proper_p2) result++;

        if (ff >= proper_f && ff<= proper_f2) result++;

        return result;
    }

}
