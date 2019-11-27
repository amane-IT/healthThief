package com.example.myapplication;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity{

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static final String TAG = "";

    SurfaceView surfaceView;
    SurfaceHolder holder;

    private int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
    MyCameraPreview myCameraPreview;

    Button picture, save, cancel, next;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        myCameraPreview = new MyCameraPreview(this, CAMERA_FACING);

        getWindow().setFormat(PixelFormat.UNKNOWN);


        next = (Button)findViewById(R.id.next);

        surfaceView = (SurfaceView)findViewById(R.id.viewer);
        holder = surfaceView.getHolder();
        holder.addCallback(myCameraPreview);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        camera.startPreview();

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCameraPreview.autoFocus();
            }
        });

        Log.d(TAG, "onCreate'd");

        checkDangerousPermissions();
    }

    public void m_onClick(View v){
        picture = (Button)findViewById(R.id.picBtn);
        save = (Button)findViewById(R.id.save);
        cancel = (Button)findViewById(R.id.cancel);

        switch(v.getId())
        {
            case R.id.picBtn:
                myCameraPreview.takePicture();
                picture.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                break;

            case R.id.save:
                intent = new Intent(this, CheckingPhoto.class);
                startActivity(intent);
                break;


        }

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
