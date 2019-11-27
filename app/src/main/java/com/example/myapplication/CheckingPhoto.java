package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import static android.os.Environment.getExternalStorageDirectory;

public class CheckingPhoto extends Activity {

    ImageView imageView;
    Button yes, no;

    MyCameraPreview myCameraPreview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check);


        myCameraPreview = new MyCameraPreview(this);

        String photoName = myCameraPreview.returnTime();

        Log.i("사진이름2: ", photoName);

        imageView = (ImageView) findViewById(R.id.photos);
        String name = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Foodiary/" + photoName + ".jpg";

        Log.d("이름2", name);
        try {
            File imgFile = new File(name);
            Log.d("경로2: ", imgFile.getAbsolutePath());
            Log.d("있냐?", Boolean.toString(imgFile.exists()));

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);


            float[][] bytes_img = new float[1][16384];
            for (int y = 0; y < 128; y++) {
                for (int x = 0; x < 128; x++) {
                    int pixel = myBitmap.getPixel(x, y);
                    bytes_img[0][y * 128 + x] = (pixel & 0xff) / (float) 255;
                }
            }
            Interpreter tf_lite = getTfliteInterpreter("model.tflite");

            float[][] output = new float[1][10];
            tf_lite.run(bytes_img, output);

            Log.d("predict", Arrays.toString(output[0]));

            //int[] id_array = {R.id.result_0, R.id.result_1, R.id.result_2, R.id.result_3, R.id.result_4,
            //        R.id.result_5, R.id.result_6, R.id.result_7, R.id.result_8, R.id.result_9};

        } catch (Exception e) {
            e.printStackTrace();
        }
        final Intent intent = new Intent(this, MainActivity.class);
        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

    }

    private Interpreter getTfliteInterpreter(String s) {
        try {
            return new Interpreter(loadModelFile(CheckingPhoto.this, s));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


}
