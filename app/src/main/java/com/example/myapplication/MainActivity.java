package com.example.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static String EXTERNAL_STORAGE_PATH = "";
    private static String TOOK_FILE = "pic";
    private static int fileIndex = 0;
    private static String filename = "";

    private Camera camera = null;
    SurfaceView surfaceView;
    SurfaceHolder holder;
    boolean previewing = false;

    ExifInterface exif = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int orientation = getResources().getConfiguration().orientation;

        Log.d("오리엔테이션", Integer.toString(orientation));

        getWindow().setFormat(PixelFormat.UNKNOWN);
        Button picure = (Button)findViewById(R.id.picBtn);
        surfaceView = (SurfaceView)findViewById(R.id.viewer);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        camera.startPreview();


        picure.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){

                camera.takePicture(mySutterCallback,
                        myPictureCallback_RAW, myPictureCallback_JPG);
            }
        });

        checkDangerousPermissions();
    }

    Camera.ShutterCallback mySutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

        }
    };

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

            //Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            Uri target = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());


            OutputStream imageFileOS;

            try{

                imageFileOS = getContentResolver().openOutputStream(target);
                imageFileOS.write(bytes);
                imageFileOS.flush();
                imageFileOS.close();

                //Toast.makeText(this, "Image Saved: " + uriTarget.toString(), Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        }
    };


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        if(previewing){
            camera.stopPreview();
            previewing = false;
        }

        if(camera != null)
        {
            try{
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(90);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder){
        camera = Camera.open();
    }


    public void surfaceDestroyed(SurfaceHolder holder){
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
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

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }
}
