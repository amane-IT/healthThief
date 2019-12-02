package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
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

    Button save;
    Button exit;
    AlertDialog.Builder oDialog = new AlertDialog.Builder(this);

    // String pic;
    String date;
    String meal;
    EditText edit_food = findViewById(R.id.menu1);
    TextView text_cal = findViewById(R.id.totalCal);
    EditText edit_content = findViewById(R.id.diaryContent);

    String food;
    String cal;
    String content;
    private DbOpenHelper mDbOpenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_diary);

        save= findViewById(R.id.saveDiary);
        exit= findViewById(R.id.exitDiary);
        // 디비 열어놓음
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();


        // 저장 버튼을 누를 시 쓰여진 정보가 디비에 저장됨.. 날짜는 일단 오늘 것만 되도록
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat ("yyyyMMdd");
                Calendar time = Calendar.getInstance();
                date = format.format(time.getTime());
                RadioGroup.OnCheckedChangeListener mealTypeCheck = new RadioGroup.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        if(i == R.id.breakfast){meal = "아침";}
                        else if(i == R.id.lunch){meal = "점심";}
                        else if(i == R.id.dinner){meal = "저녁";}
                    }
                };
                food = edit_food.getText().toString();
                cal = text_cal.getText().toString();
                content = edit_content.getText().toString();

                mDbOpenHelper.open();
                mDbOpenHelper.insertColum(date,meal,food,cal,null,null,null,content);
                Toast.makeText(getApplicationContext(),"다이어리 저장 성공!",Toast.LENGTH_SHORT).show();
                setInsertMode();
                mDbOpenHelper.close();
                finish();
            }
        });

        // 취소 버튼을 누를 시 한번 물어본 후, 정보가 저장되지 않고 activity가 끝난다.
        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                oDialog.setMessage("다이어리 작성을 취소하겠습니까?")
                        .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.i("Dialog", "취소");
                                Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("예", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                finish();
                            }
                        })
                        .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                        .show();

                finish();
            }
        });

    }

    public void setInsertMode(){
        edit_food.setText("");
        text_cal.setText("");
        edit_content.setText("");
    }


}
