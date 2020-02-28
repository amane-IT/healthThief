package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    // 하단 탭 각 메뉴들 fragment 지정. 카메라는 fragment가 아닌 새 창을 만드는거 같아서... 일단 빼둠
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private fragmentDiary fragmentDiary = new fragmentDiary();
    private fragmentReport fragmentReport = new fragmentReport();
    private fragmentSetting fragmentSetting = new fragmentSetting();


    //첫 실행 체크
    public boolean CheckAppFirstExecute(){
        SharedPreferences pref = getSharedPreferences("IsFirst", Activity.MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst",false);
        if(!isFirst){
            // 최초 실행시 프로필 작성
            Intent goProfile = new Intent(this, setProfile.class);
            startActivity(goProfile);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst",true);
            editor.apply();
        }
        return !isFirst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentDiary).commitAllowingStateLoss();


        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        CheckAppFirstExecute();
        checkDangerousPermissions();
    }

    // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                /*
                    Intent intent = new Intent(getApplicationContext(),MyCamera.class);
                    startActivity(intent);
                 */
                case R.id.diaryItem:
                    transaction.replace(R.id.frameLayout, fragmentDiary).commitAllowingStateLoss();
                    break;
                case R.id.reportItem:
                    transaction.replace(R.id.frameLayout, fragmentReport).commitAllowingStateLoss();
                    break;
                case R.id.settingItem:
                    transaction.replace(R.id.frameLayout, fragmentSetting).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    private void checkDangerousPermissions(){
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_NETWORK_STATE,
                };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i=0; i<permissions.length; i++){
            permissionCheck = ContextCompat.checkSelfPermission(this,permissions[i]);
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                break;
            }

        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.i("PERMISSION","권한 있음");
        } else {
            Log.i("PERMISSION","권한 없음");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[0])){
                Toast.makeText(this, "권한 설명 필요함", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,permissions,1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode ==1 ){
            for (int i=0; i<permissions.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Log.i("PERMISSION",permissions[i]+"권한이 승인됨");
                } else {
                    Log.i("PERMISSION",permissions[i]+"권한이 승인되지 않음");
                }
            }
        }
    }

}
