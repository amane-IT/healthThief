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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

        final Intent intent = new Intent(changeProfile.this, fragmentSetting.class);

        change = (Button)findViewById(R.id.fixDone);
        cancle = (Button)findViewById(R.id.fixCancle);

        change.setOnClickListener(new View.OnClickListener() {
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

}
