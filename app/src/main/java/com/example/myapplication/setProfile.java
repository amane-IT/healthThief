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


// set_profile.xml에 표시된 프로필을 작성하고 '시작하기' 버튼을 클릭하면 infoDB에 정보가 저장된다.
public class setProfile extends AppCompatActivity{

    Button start;

    EditText edit_name;
    EditText edit_age;
    EditText edit_weight;
    EditText edit_height;

    String db_name;
    int db_age;
    int db_weight;
    int db_height;
    int db_sex = 0;
    int db_scal;

    private DbHelper DbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_profile);

        final Intent intent = new Intent(setProfile.this, fragmentSetting.class);
        final Intent Home = new Intent(this, MainActivity.class);

        start = (Button)findViewById(R.id.startFoodiary);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("setProfile","BUTTON_CLICKED");
                // 이름, 나이, 몸무게, 성별
                edit_name = (EditText) findViewById(R.id.name);
                edit_age = (EditText) findViewById(R.id.age);
                edit_weight = (EditText) findViewById(R.id.weight);
                edit_height = (EditText) findViewById(R.id.height);

                db_name = edit_name.getText().toString();
                db_age = Integer.parseInt(edit_age.getText().toString());
                db_weight = Integer.parseInt(edit_weight.getText().toString());
                db_height = Integer.parseInt(edit_height.getText().toString());


                RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        if(i == R.id.sex1){
                            db_sex = 1;
                        }
                        else if(i == R.id.sex2){
                            db_sex = 2;
                        }
                    }
                };

                db_scal = suggestCal(db_age,db_weight,db_height,db_sex);

                if(DbHelper == null){
                    DbHelper = new DbHelper(setProfile.this,"TEST",null,DbHelper.DB_VERSION);
                }

                // 내 정보 db에 삽입
                Info info = new Info();
                info.setMyName(db_name);
                info.setAge(db_age);
                info.setWeight(db_weight);
                info.setHeight(db_height);
                info.setSex(db_sex);
                info.setScal(db_scal);
                DbHelper.insertInfo(info);

                Log.i("CHECK INFO NAME: ",db_name);

                Home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Home.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(Home);
                finish();
            }
        });

    }

    //권장 칼로리 계산
    /*
    여성: 655 + (9.6 x 체중(kg)) + (1.8 x 신장(cm)) -(4.7 x 나이)
    남성: 66 + (13.7 x 체중(kg)) + (5 x 신장(cm)) -(6.5 x 나이)
     */
    public int suggestCal(int a, int w, int h, int s){
        int c=0;

        if (s==1){
            c = (int)(66 + (13.7*w)+(5*h)-(6.5*a));
        }
        else{
            c = (int)(655+(9.6*w)+(1.8*h)-(4.7*a));
        }
        return c;
    }
}
