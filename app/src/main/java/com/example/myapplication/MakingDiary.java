package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

// 다이어리 작성창. 저장하면 디비에 내용이 저장되야 하지만.. 일단은 바뀌는 걸로만?

public class MakingDiary extends AppCompatActivity {

    Button save;
    Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_diary);

        save= findViewById(R.id.saveDiary);
        exit= findViewById(R.id.exitDiary);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }



}
