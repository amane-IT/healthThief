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


// set_profile.xml에 표시된 프로필을 작성하고 '시작하기' 버튼을 클릭하면 fragmentSetting.java로 데이터가 이동한다.
// 이후 fragmentSetting의 정보를 수정할때도 setProfile.java가 실행된다.
public class setProfile extends AppCompatActivity {

    Button start;
    Button setBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_profile);
        buttonClick();
    }

    public void buttonClick(){

        final Intent intent = new Intent(setProfile.this, fragmentSetting.class);
        final Intent Home = new Intent(this, MainActivity.class);

        start = (Button)findViewById(R.id.startFoodiary);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("setProfile","BUTTON_CLICKED");
                // 이름, 나이, 몸무게, 성별
                EditText name = (EditText) findViewById(R.id.name);
                intent.putExtra("setName",name.getText().toString());
                EditText age = (EditText) findViewById(R.id.age);
                intent.putExtra("setAge",age.getText().toString());
                EditText weight = (EditText) findViewById(R.id.weight);
                intent.putExtra("setWeight",weight.getText().toString());

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

                Home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Home.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(Home);
                finish();
            }
        });


        setBt = findViewById(R.id.settingDone);
        setBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이름, 나이, 몸무게, 성별
                EditText name = (EditText) findViewById(R.id.name);
                intent.putExtra("setName",name.getText().toString());
                EditText age = (EditText) findViewById(R.id.age);
                intent.putExtra("setAge",age.getText().toString());
                EditText weight = (EditText) findViewById(R.id.weight);
                intent.putExtra("setWeight",weight.getText().toString());

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
    }




}
