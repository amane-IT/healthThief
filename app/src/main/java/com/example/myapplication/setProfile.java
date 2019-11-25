package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class setProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
    }

    // 프로필을 작성하고 시작하기 버튼을 클릭하면 fragmentSetting.java로 데이터가 이동한다.
    // 이후 fragmentSetting의 정보를 수정할때도 setProfile.java가 실행된다.

}
