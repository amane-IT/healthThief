package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
            //최초 실행시 permissions 확인
            checkDangerousPermissions();
            // 최초 실행시 프로필 작성
            Intent goProfile = new Intent(this,MakingDiary.class);
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
    }

    // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.cameraItem:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);

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


    /* onCreate 에 카메라 있음

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static final String TAG = "";

    private Camera camera = null;
    SurfaceView surfaceView;
    SurfaceHolder holder;
    boolean previewing = false;

    private Camera.CameraInfo mCameraInfo;
    private int mDisplayOrientation;

    private int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
    MyCameraPreview myCameraPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mCameraInfo = new Camera.CameraInfo();
        mDisplayOrientation = getWindowManager().getDefaultDisplay().getRotation();

        myCameraPreview = new MyCameraPreview(this, CAMERA_FACING);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        Button picure = (Button)findViewById(R.id.picBtn);
        surfaceView = (SurfaceView)findViewById(R.id.viewer);
        holder = surfaceView.getHolder();
        holder.addCallback(myCameraPreview);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        camera.startPreview();


        picure.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                myCameraPreview.takePicture();
            }
        });

        checkDangerousPermissions();
    }


     */


    private void checkDangerousPermissions(){
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i=0; i<permissions.length; i++){
            permissionCheck = ContextCompat.checkSelfPermission(this,permissions[i]);
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                break;
            }

        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"권한없음",Toast.LENGTH_LONG).show();

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
                    Toast.makeText(this, permissions[i]+"권한이 승인됨", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,permissions[i]+"권한이 승인되지 않음",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
