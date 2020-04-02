package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public class MyCamera extends AppCompatActivity {

    private static final int INPUT_SIZE = 224;

    TextView resultTextView;
    //
    Interpreter interpreter;
    //
    String[] labels = {"냉면", "크림파스타", "마카롱", "마라탕", "수플레", "돈까스", "쌀국수", "연어초밥", "만두", "토마토파스타"};
    int first = 0;

    String[][] data = new String[3][8];
    int i = 0;
    int kcal = 0;
    int serving = 100;
    float carbo = 0;
    float protein = 0;
    float fat = 0;
    double servings = 0;

    private ImageView iv_food;
    private int id_view;

    private Button item1, item2, item3, item4, save;
    private ImageButton returnBtn;

    public List<foodData> foodDataList;

    private File tempFile;

    private int PICK_PICTURE_FROM_GALLERY = 1111;
    private int PICK_PICTURE_FROM_CAMERA = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_camera);

        resultTextView = findViewById(R.id.ML_result);

        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);
        item4 = findViewById(R.id.item4);
        item4.setText("기타");

        returnBtn = (ImageButton)findViewById(R.id.returnBtn);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getWindow().setFormat(PixelFormat.UNKNOWN);

        //db_manager = new DB_Manager();

        iv_food = (ImageView)findViewById(R.id.food_image);
        Button choice = (Button)findViewById(R.id.picBtn);

        initLoadDB();

        try {
            Interpreter.Options options = new Interpreter.Options();
            interpreter = new Interpreter(loadModelFile("mobilenet_v2.tflite"), options);
        }
        catch(IOException ex){
            Log.d("app", ex.toString());
        }

        checkDangerousPermissions();
    }

    //퍼미션 체크하는 함수
    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_NETWORK_STATE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    private MappedByteBuffer loadModelFile(String path) throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(path);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer modelFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

        return modelFile;
    }

    //버튼 누르면 실행되는 함수
    public void onClick(View v) {
        id_view = v.getId();
        final Dialog foodDialog = new Dialog(this);

        if(id_view == R.id.save) {

            /**
             * 추가할 음식을 물어보는 코드 */


            if(iv_food.getDrawable() != null)
            {
                //몇인분을 먹었나요
                foodDialog.setContentView(R.layout.serving_dialog);
                RadioGroup radioGroup = foodDialog.findViewById(R.id.serve);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.quart)
                            servings = 0.25;
                        else if (checkedId == R.id.half)
                            servings = 0.5;
                        else if (checkedId == R.id.one)
                            servings = 1;
                        else if (checkedId == R.id.two)
                            servings = 2;

                        Log.d("serve: ", Double.toString(servings));
                    }
                });

                Button btn_ok = foodDialog.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("serve: ", Double.toString(servings));
                        foodDialog.dismiss();

                        DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Button next = (Button) findViewById(R.id.next);
                                next.setVisibility(View.VISIBLE);
                                save = (Button) findViewById(R.id.save);
                                save.setVisibility(View.INVISIBLE);
                                dialogInterface.dismiss();
                            }
                        };

                        //음식 추가를 3개 이상하려 할 경우
                        if (i >= 3) {
                            new AlertDialog.Builder(MyCamera.this)
                                    .setTitle("더이상 추가할 수 없습니다.")
                                    .setPositiveButton("돌아가기", cancel)
                                    .show();
                        }

                        //음식 추가는 임의로 3가지까지만..
                        else if (i < 3) {


                            Log.d("food_data: ", foodDataList.get(1).getName());
                            Log.d("serve: ", Double.toString(servings));

                            //사진은.. 확인 버튼 눌렀을시에만 저장됩니다.
                            Drawable d = iv_food.getDrawable();
                            Bitmap photo = ((BitmapDrawable)d).getBitmap();
                            String strFilePath = Environment.getExternalStorageDirectory() + "/foodiary/";
                            long now = currentTimeMillis();
                            Date mDate = new Date(now);
                            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(mDate);
                            String fileName = "foodiary" + timeStamp + ".jpg";
                            SaveBitmapToFileCache(photo, strFilePath, fileName);

                            //data = { i, 이미지 경로, 이름, 칼로리, 탄수화물, 단백질, 지방, 식이섬유, 당, 나트륨, 1인분 양 }
                            data[i][0] = Integer.toString(i);
                            data[i][1] = strFilePath + fileName;
                            data[i][2] = String.valueOf(resultTextView.getText());
                            data[i][3] = Integer.toString(kcal);
                            data[i][4] = Float.toString(carbo);
                            data[i][5] = Float.toString(protein);
                            data[i][6] = Float.toString(fat);
                            data[i][7] = Double.toString(serving * servings);


                            Log.d("경로: ", data[i][1]);
                            Log.d("탄수: ", data[i][4]);
                            Log.d("단백질: ", data[i][5]);
                            i++;


                        }

                        DialogInterface.OnClickListener add_data = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //여기에 정보를 이차원 배열에 추가하는 코드를 짜자
                                resultTextView.setText("");
                                iv_food.setImageBitmap(null);
                            }
                        };

                        DialogInterface.OnClickListener save_data = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Button next = (Button) findViewById(R.id.next);
                                next.setVisibility(View.VISIBLE);
                                dialogInterface.dismiss();
                            }
                        };

                        new AlertDialog.Builder(MyCamera.this)
                                .setTitle("더 추가하시겠습니까?")
                                .setPositiveButton("네", add_data)
                                .setNegativeButton("아니오", save_data)
                                .show();
                    }
                });

                foodDialog.show();
            }

            DialogInterface.OnClickListener returnBack = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            };


            //음식을 선택하지 않은 경우
            if (iv_food.getDrawable() == null) {
                new AlertDialog.Builder(this)
                        .setTitle("선택된 것이 없습니다.")
                        .setPositiveButton("돌아가기", returnBack)
                        .show();
            }
        }


        /**
         * 다이어리화면으로 데이터 전송 & 넘어감
         * */
        if(id_view == R.id.next)
        {
            Intent goDiary = new Intent(MyCamera.this, WriteDiary.class);
            Log.d("넘어감: ", "간다");
            for (int j = 0; j < i; j++)
            {
                Log.d("food_data: ", foodDataList.get(1).getName());

                String dkey = "food_image" + Integer.toString(j);
                String nkey = "food_name" + Integer.toString(j);
                String kkey = "food_kcal" + Integer.toString(j);
                String Carbokey = "food_carbo" + j;
                String Proteinkey = "food_protein" + j;
                String Fatkey = "food_fat" + j;
                String Servingkey = "food_serving" + j;

                Log.d("Carbo Data: ", data[j][4]);
                goDiary.putExtra(dkey, data[j][1]);
                goDiary.putExtra(nkey, data[j][2]);
                goDiary.putExtra(kkey, data[j][3]);
                goDiary.putExtra(Carbokey, data[j][4]);
                goDiary.putExtra(Proteinkey, data[j][5]);
                goDiary.putExtra(Fatkey, data[j][6]);
                goDiary.putExtra(Servingkey, data[j][7]);
                Log.d("넘어감: ", "간다");
            }
            goDiary.putExtra("idx", i);
            startActivity(goDiary);
            //startActivityForResult(intent, 1001);
            Log.d("넘어감: ", "간다");

        }


        /**
         * 앨범이나 카메라에서 사진 가져오는 기능
         * */
        if(id_view == R.id.picBtn)
        {
            foodDialog.setContentView(R.layout.pick_dialog);
            Button gallery = (Button)foodDialog.findViewById(R.id.gallery);

            //갤러리에서 사진 선택
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_PICTURE_FROM_GALLERY);
                    foodDialog.dismiss();
                }
            });

            //카메라에서 사진 선택
            Button camera = (Button)foodDialog.findViewById(R.id.camera);
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {

                        tempFile = createImageFile();
                    } catch (IOException e) {
                        finish();
                        e.printStackTrace();
                    }
                    if (tempFile != null) {

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(),
                                    "myapplication.provider", tempFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, PICK_PICTURE_FROM_CAMERA);

                        } else {
                            Uri photoUri = Uri.fromFile(tempFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, PICK_PICTURE_FROM_CAMERA);

                        }
                    }
                    foodDialog.dismiss();
                }
            });

            /**CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
             */

            foodDialog.show();
        }

        /**
         * 머신러닝 결과 후보군들
         * */
        if (id_view == R.id.item1){
            resultTextView.setText(item1.getText());
            searchData(String.valueOf(resultTextView.getText()));
        }
        if (id_view == R.id.item2){
            resultTextView.setText(item2.getText());
            searchData(String.valueOf(resultTextView.getText()));
        }
        if (id_view == R.id.item3){
            resultTextView.setText(item3.getText());
            searchData(String.valueOf(resultTextView.getText()));
        }
        if (id_view == R.id.item4){
            if(iv_food.getDrawable() != null) {
                foodDialog.setContentView(R.layout.add_food_dialog);
                foodDialog.setTitle("음식을 추가해 주세요.");
                Button close = (Button) foodDialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText food_ed = (EditText) foodDialog.findViewById(R.id.food_name);
                        resultTextView.setText(food_ed.getText());
                        EditText kcal_ed = (EditText) foodDialog.findViewById(R.id.kcal);
                        kcal = Integer.parseInt(kcal_ed.getText().toString());
                        foodDialog.dismiss();
                    }
                });

                foodDialog.show();
            }
            else
            {
                DialogInterface.OnClickListener back = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };

                new AlertDialog.Builder(this)
                        .setTitle("선택된 음식이 없습니다.")
                        .setPositiveButton("돌아가기", back)
                        .show();
            }
            carbo = 0;
            protein = 0;
            fat = 0;
            serving = 100;
        }
        if (id_view == R.id.returnBtn)
        {
            resultTextView.setText(labels[first]);
            searchData(String.valueOf(resultTextView.getText()));
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == PICK_PICTURE_FROM_GALLERY)
        {
            Uri uri = imageReturnedIntent.getData();
            if (uri != null) {
                CropImage.activity(uri)
                        .start(this);
            }
            else
            {
                Intent intent = new Intent(this, MyCamera.class);
                startActivity(intent);
            }
        }

        if (requestCode == PICK_PICTURE_FROM_CAMERA)
        {
            Uri uri = FileProvider.getUriForFile(this, "myapplication.provider", tempFile);
            CropImage.activity(uri)
                    .start(this);
        }

        //이미지를 받아오면 크롭을 하자
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                iv_food.setImageURI(resultUri);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                detectImage(bitmap);
                searchData(String.valueOf(resultTextView.getText()));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    //이미지 딥러닝
    private void detectImage(Bitmap bitmap) {
        int width = INPUT_SIZE;
        int height = INPUT_SIZE;
        int color = 3;
        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        ByteBuffer input = bitmap2bytebuffer(scaledBitmap, width, height, color);
        float[][] output = new float[1][10];
        //
        interpreter.run(input, output);
        //
        float[] probabilites = output[0];
        int[] rank = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        int class_ = 0;
        int second = 0;
        int third = 0;
        int forth = 0;


        for (int i = 0; i < probabilites.length; i++) {

            double standard_rate = probabilites[i];//기준학생점수

            for (int j = i; j < probabilites.length; j++) {

                double compare_rate = probabilites[j];//비교학생점수

                if (standard_rate < compare_rate) {

                    rank[i]++;

                } else if (standard_rate > compare_rate) {

                    rank[j]++;

                }
            }
        }

        for (int i = 0; i < rank.length; i++)
        {
            if(rank[i] == 1)
            {
                resultTextView.setText(labels[i]);
                first = i;
            }

            if(rank[i] == 2)
            {
                item1.setText(labels[i]);
            }

            if(rank[i] == 3)
            {
                item2.setText(labels[i]);
            }

            if(rank[i] == 4)
            {
                item3.setText(labels[i]);
            }

        }
    }

    private ByteBuffer bitmap2bytebuffer(Bitmap bitmap, int width, int height, int color) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width*height*color*4); //A float has 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int R,G,B;
        for (int i=0;i<width*height;i++) {
            int pixel = pixels[i];
            if (color == 1) {
                //https://github.com/frogermcs/MNIST-TFLite/blob/master/android/app/src/main/java/com/frogermcs/mnist/MnistClassifier.java
                //byteBuffer.putFloat(pixel);
                float rChannel = (pixel >> 16) & 0xFF;
                float gChannel = (pixel >> 8) & 0xFF;
                float bChannel = (pixel) & 0xFF;
                float pixelValue = (rChannel + gChannel + bChannel) / 3 / 255.f;
                byteBuffer.putFloat(pixelValue);
            }
            else {
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                byteBuffer.putFloat(R);
                byteBuffer.putFloat(G);
                byteBuffer.putFloat(B);
            }
        }

        return byteBuffer;
    }

    //디비 초기화
    private void initLoadDB(){
        DataAdapter mDBHelper = new DataAdapter(this);
        mDBHelper.createDatabase();
        mDBHelper.open();

        //DB에 있는 값들을 model에 적용하여 넣음
        foodDataList = mDBHelper.getTableData();

        Log.d("food_data: ", Float.toString(foodDataList.get(8).getCarbo()));

        //DB닫기
        mDBHelper.close();
    }

    //찾아라 머신러닝 결과값
    private void searchData(String name)
    {
        Log.d("크기: ", String.valueOf(foodDataList.size()));
        Log.d("이름: ", name);

        for (int k = 0; k < foodDataList.size(); k++)
        {
            Log.d("k: ", foodDataList.get(k).getName());

            if(foodDataList.get(k).getName().equals(name))
            {
                Log.d("food_data: ", foodDataList.get(k).getName());

                kcal = foodDataList.get(k).getKcal();
                carbo = foodDataList.get(k).getCarbo();
                protein = foodDataList.get(k).getProtein();
                fat = foodDataList.get(k).getFat();
                serving = foodDataList.get(k).getServing();
                break;
            }
            else
            {
                kcal = 0;
                carbo = 0;
                protein = 0;
                fat = 0;
                serving = 100;
            }
            Log.d("칼로리: ", Integer.toString(kcal));
            Log.d("탄수: ", Float.toString(carbo));
            Log.d("단백질: ", Float.toString(protein));
        }
    }

    //비트맵->파일 저장 함수
    public  void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath,
                                       String filename) {

        File file = new File(strFilePath);

        // If no folders
        if (!file.exists()) {
            file.mkdirs();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        File fileCacheItem = new File(strFilePath + filename);
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Toast.makeText(this, fileCacheItem.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.d("경로1111: ", fileCacheItem.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( foodiary시간_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "foodiary" + timeStamp + "_";

        // 이미지가 저장될 파일 이름 ( foodiary )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/foodiary/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d("경로", "createImageFile : " + image.getAbsolutePath());

        return image;
    }
}