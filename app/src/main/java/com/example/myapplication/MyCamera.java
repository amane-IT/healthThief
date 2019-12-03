package com.example.myapplication;

import android.Manifest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCamera extends AppCompatActivity implements View.OnClickListener{


    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;

    private Uri mImageCaptureUri;
    private ImageView iv_food;
    private int id_view;
    private String absolutePath;
    private File tempFile;
    private Boolean isCamera = false;


    //private DB_Manager db_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_camera);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFormat(PixelFormat.UNKNOWN);

        //db_manager = new DB_Manager();

        iv_food = (ImageView)findViewById(R.id.food_image);
        Button choice = (Button)findViewById(R.id.picBtn);

        choice.setOnClickListener(this);

        if(!isConnect())
        {
            Toast.makeText(this, "네트워크 연결 x", Toast.LENGTH_SHORT).show();
            return;
        }


        HttpMgrThread httpThread = new HttpMgrThread();
        httpThread.reqHttp();

    }

    private boolean isConnect() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    //카메라에서 촬영
    public void doTakePhotoAction(){
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //임시 파일 경로 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        //mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        mImageCaptureUri = FileProvider.getUriForFile(this, "myapplication.provider", new File(Environment.getExternalStorageDirectory(), url));
        Log.d("경로", String.valueOf(mImageCaptureUri));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
         */
        isCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {

            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "myapplication.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }

    }

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "foodiary" + timeStamp + "_";

        // 이미지가 저장될 파일 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/foodiary/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d("경로", "createImageFile : " + image.getAbsolutePath());

        return image;
    }


    public void doTakeAlbumAction(){
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        switch (requestCode)
        {
            case PICK_FROM_ALBUM: {
                //이후 처리는 카메라와 같음 -> break없이 진행
                Uri photoUri = data.getData();
                Log.d("Foodiary", photoUri.getPath().toString());
                cropImage(photoUri);
                break;
            }

            case PICK_FROM_CAMERA:
            {
                Uri photoUri = Uri.fromFile(tempFile);
                cropImage(photoUri);
                break;
            }
            case Crop.REQUEST_CROP: {
                File cropFile = new File(Crop.getOutput(data).getPath());
                setImage();
            }
        }
    }

    private void cropImage(Uri photoUri) {
        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if(tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }

        //크롭 후 저장할 Uri
        Uri savingUri = Uri.fromFile(tempFile);

        Crop.of(photoUri, savingUri).asSquare().start(this);
    }

    private void setImage() {

        //크롭할 때 이미 리사이즈 하지 않았나? >> 없어도 됨!
       //ImageResizeUtils.resizeFile(tempFile, tempFile, 300, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d("경로", "setImage : " + tempFile.getAbsolutePath());

        iv_food.setImageBitmap(originalBm);

        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 (tempFile != null)이면 해당 파일을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
        tempFile = null;
    }

    @Override
    public void onClick(View v) {
        id_view = v.getId();
        if(id_view == R.id.savePic) {
            //SharedPreferences prefs = getSharedPreferences()
            //이미지 저장하고 서버로 보내자
            //그리고 다이어리 작성창 실행
            Intent intent = new Intent(this, WriteDiary.class);
            startActivity(intent);
        }
        else if(id_view == R.id.picBtn)
        {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doTakePhotoAction();
                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doTakeAlbumAction();
                }
            };

            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("업로드 이미지 선택")
                    .setPositiveButton("사진 촬영", cameraListener)
                    .setNeutralButton("앨범선택", albumListener)
                    .setNegativeButton("취소", cancelListener)
                    .show();
            /*
                case R.id.picBtn:
                myCameraPreview.takePicture();
                picture.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                break;
             */
        }
    }

    // 크롭된 이미지 저장
    private  void storeCropImage(Bitmap bitmap, String filePath){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/foodiary";
        File directory = new File(dirPath);

        if(!directory.exists())
            directory.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try{
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, FileProvider.getUriForFile(this, "myapplication.provider", copyFile)));

            out.flush();
            out.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
