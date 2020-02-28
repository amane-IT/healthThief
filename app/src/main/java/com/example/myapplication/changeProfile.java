package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class changeProfile extends AppCompatActivity{


    Button change;
    Button cancle;

    EditText ename;
    EditText eage;
    EditText eweight;
    EditText eheight;
    EditText esex;
    int ecal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

        final Intent intent = new Intent(changeProfile.this, fragmentSetting.class);

        change = (Button)findViewById(R.id.fixDone);
        cancle = (Button)findViewById(R.id.fixCancle);


        // 변경 버튼을 클릭하면 내용이 바뀌었는지 체크하고 바뀌었다면 db에 새로 업데이트한다.
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Change Profile","BUTTON_CLICKED");

                // db에 원래 입력되어있던 정보를 가져와 edittext에 뿌린다
                ename = findViewById(R.id.name);
                eage = findViewById(R.id.age);
                eweight = findViewById(R.id.weight);
                eheight = findViewById(R.id.height);


                RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        if(i == R.id.sex1){
                            intent.putExtra("setSex","남");
                        }
                        else if(i == R.id.sex2){
                            intent.putExtra("setSex","여");
                        }
                    }
                };



                finish();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void checkChange(){



        return;
    }

}
