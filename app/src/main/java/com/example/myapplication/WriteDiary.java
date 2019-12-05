package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;

// 카메랑 실행 이후 aws 에서 사진과 음식 이름(string)을 받으며 자동으로 실행됨 << 되는지 체크해야함!
// 다이어리 작성창. 저장을 누르면 디비에 저장됨

public class WriteDiary extends AppCompatActivity {

    WriteDiary w = this;
    Button save;
    Button exit;

    // String pic;
    String date;
    EditText edit_food;
    TextView text_cal;
    EditText edit_content;
    AlertDialog.Builder oDialog;
    String meal;
    String food;
    String cal;
    String content;
    RadioGroup rg;
    //private DbOpenHelper mDbOpenHelper;

    private DbHelper DbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_diary);


        save= findViewById(R.id.saveDiary);
        exit= findViewById(R.id.exitDiary);

        meal = null;
        edit_food = findViewById(R.id.menu1);
        text_cal = findViewById(R.id.totalCal);
        edit_content = findViewById(R.id.diaryContent);
        rg = findViewById(R.id.mealType);
        oDialog = new AlertDialog.Builder(this);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.breakfast){meal = "아침";}
                else if(checkedId == R.id.lunch){meal = "점심";}
                else if(checkedId == R.id.dinner){meal = "저녁";}
                Log.i("CHECK MEAL : ",meal);
            }
        });

        // 저장 버튼을 누를 시 쓰여진 정보가 디비에 저장됨.. 날짜는 일단 오늘 것만 되도록
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat ("yyyyMMdd");
                Calendar time = Calendar.getInstance();
                date = format.format(time.getTime());

                food = edit_food.getText().toString();
                cal = text_cal.getText().toString();
                content = edit_content.getText().toString();
                Log.i("CHECK INFO YOU WROTE : ",date+", "+food+", "+cal+", "+content);

                if(DbHelper == null){
                    DbHelper = new DbHelper(WriteDiary.this,"TEST",null,DbHelper.DB_VERSION);
                }
                // DB에 새 다이어리 data 삽입
                Diary diary = new Diary();
                diary.setDate(date);
                diary.setMeal(meal);
                diary.setFood(food);
                diary.setCal(cal);
                diary.setDiary(content);
                DbHelper.insertDiary(diary);

               goHome();
            }
        });

        // 취소 버튼을 누를 시 한번 물어본 후, 정보가 저장되지 않고 activity가 끝난다.
        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //dialog 속성 설정
                oDialog.setMessage("다이어리 작성을 취소하겠습니까?")
                        .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                        .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.i("Dialog", "계속 작성하기");
                                Toast.makeText(getApplicationContext(), "Go on!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(getApplicationContext(),"Cancel Diary",Toast.LENGTH_SHORT).show();
                                goHome();
                            }
                        });
                AlertDialog dialog = oDialog.create();
                dialog.show();
            }
        });

    }

    // 작성을 완료 또는 취소하고 홈으로 돌아간다.
    public void goHome(){
        Intent intentHome = new Intent(w,MainActivity.class);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentHome);
        finish();
    }

    public void setInsertMode(){
        edit_food.setText("");
        text_cal.setText("");
        edit_content.setText("");
    }


}
