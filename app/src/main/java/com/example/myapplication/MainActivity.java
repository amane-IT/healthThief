package com.example.myapplication;

import android.Manifest;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static final String TAG = "";

    private Camera camera = null;
    SurfaceView surfaceView;
    SurfaceHolder holder;

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
