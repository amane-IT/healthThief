package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyCamera extends AppCompatActivity {

    private static final int INPUT_SIZE = 224;

    TextView resultTextView;
    //
    Interpreter interpreter;
    //
    String[] labels = {"냉면", "크림파스타", "마카롱", "마라탕", "수플레", "돈까스", "쌀국수", "연어초밥", "만두", "토마토파스타"};

    Bitmap originalBm;

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
    private String absolutePath;
    private File tempFile;
    private Boolean isCamera = false;
    private String realPath;

    private Button item1, item2, item3, item4, save;

    public List<foodData> foodDataList;

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

        if(!isConnect())
        {
            Toast.makeText(this, "네트워크 연결 x", Toast.LENGTH_SHORT).show();
            return;
        }

        absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/foodiary/" + System.currentTimeMillis()+ ".jpg";

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

    //인터넷 연결 확인 함수
    private boolean isConnect() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    //사진 생성
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

    //카메라에서 촬영
    public void doTakePhotoAction(){
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
                startActivityForResult(intent, 2);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, 2);

            }
        }

    }

    //앨범에서 사진 선택
    public void doTakeAlbumAction(){
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
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
             * 여기에 디비에 정보를 찾는 코드를 짜자..
             * 기타의 경우, 이름과 칼로리, 그리고 총량만 입력받고
             * 총량을 기타로 퉁쳐버리는 건 어떨까?
             * 디비에 저장할 때 이름으로 음식디비의 정보를 찾아서 저장해야 하니까
             * 해당 부분은 이차원배열에 음식 정보를 넣어놨다가 나중에 음식 추가가 끝나면
             * 다음 페이지로 넘어가기 전에 정보를 추가하는 걸로 하자
             * */

            /**
             * 추가할 음식을 물어보는 코드 */

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

                        //data = { i, 이미지 경로, 이름, 칼로리, 탄수화물, 단백질, 지방, 식이섬유, 당, 나트륨, 1인분 양 }
                        data[i][0] = Integer.toString(i);
                        data[i][1] = realPath;
                        data[i][2] = String.valueOf(resultTextView.getText());
                        data[i][3] = Integer.toString(kcal);
                        data[i][4] = Float.toString(carbo);
                        data[i][5] = Float.toString(protein);
                        data[i][6] = Float.toString(fat);
                        data[i][7] = Double.toString(serving * servings);


                        Log.d("칼로리: ", data[i][3]);
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
        //else
        if(id_view == R.id.picBtn)
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

        }

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


    }

    //이미지 크롭
    private void cropImage(Uri photoUri){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        InputStream inputStream = null;
        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //inputStream = getAssets().open("number0.png");
                    Uri uri = imageReturnedIntent.getData();
                    cropImage(uri);
                    realPath = getRealPathFromURI(uri);

                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri uri = FileProvider.getUriForFile(this, "myapplication.provider", tempFile);
                    cropImage(uri);
                }
                break;

            case Crop.REQUEST_CROP:
                //File cropFile = new File(Crop.getOutput(imageReturnedIntent).getPath());
                try {
                    inputStream = getContentResolver().openInputStream(Uri.fromFile(tempFile));
                } catch (IOException ex) {
                    Log.d("app", ex.toString());
                }
                Bitmap testBitmap = BitmapFactory.decodeStream(inputStream);
                detectImage(testBitmap);
                searchData(String.valueOf(resultTextView.getText()));
                setImage();
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




        /*for (int i = 0; i < probabilites.length; i++) {
            if (probabilites[i] > maxValue) {
                maxValue = probabilites[i];
                forth = third;
                third = second;
                second = class_;
                class_ = i;
            }
            Log.d("2: ", labels[second]);
            Log.d("3: ", labels[third]);
            Log.d("4: ", labels[forth]);
            Log.d("태그: ", labels[i]);
            Log.d("가능성: ", Float.toString(probabilites[i]));
        }
        resultTextView.setText(labels[class_]);
        item1.setText(labels[second]);
        item2.setText(labels[third]);
        item3.setText(labels[forth]);*/


    }

    //이미지 뷰에 사진 세팅
    private void setImage() {

        //크롭할 때 이미 리사이즈 하지 않았나? >> 없어도 됨!
        //ImageResizeUtils.resizeFile(tempFile, tempFile, 300, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d("경로", "setImage : " + tempFile.getAbsolutePath());

        iv_food.setImageBitmap(originalBm);
        //storeCropImage(originalBm, tempFile.getAbsolutePath());

        //detectImage(originalBm);
        realPath = tempFile.getAbsolutePath();
        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 (tempFile != null)이면 해당 파일을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
        tempFile = null;
    }

    //URI의 실제저장경로 찾는 함수
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
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

}