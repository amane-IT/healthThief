package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class DbHelper extends SQLiteOpenHelper {

    private Context context;
    public static final int DB_VERSION = 6; //DB onCreate String s 바뀔시 숫자 up

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        String s;
        s = " CREATE TABLE IF NOT EXISTS FOODIARYDB ( "
                +" _ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                +" DATE INTEGER NOT NULL, "
                +" MEAL STRING NOT NULL, "
                +" FOOD STRING NOT NULL, "
                +" CAL STRING NOT NULL, "
                +" DIARY STRING, "
                +" IMAGE STRING NOT NULL )";

        db.execSQL(s);
        Toast.makeText(context,"Diary Table Created",Toast.LENGTH_SHORT).show();

        String s2;
        s2 = " CREATE TABLE IF NOT EXISTS INFODB ( "
                +" _ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                +" NAME STRING NOT NULL, "
                +" AGE INTEGER NOT NULL, "
                +" WEIGHT INTEGER NOT NULL, "
                +" HEIGHT INTEGER NOT NULL, "
                +" SEX INTEGER NOT NULL, "
                +" SCAL INTEGER NOT NULL )";
        db.execSQL(s2);
        Toast.makeText(context,"Info Table Created",Toast.LENGTH_SHORT).show();

    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Toast.makeText(context, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();


    }


    public void testDB() {
        SQLiteDatabase db = getReadableDatabase();
    }

    public void insertDiary(Diary diary){

        SQLiteDatabase db = getWritableDatabase();
        String s;
        s = " INSERT INTO FOODIARYDB ( "
                + " DATE, MEAL, FOOD, CAL, DIARY, IMAGE )"
                + " VALUES ( ?, ?, ?, ?, ?, ? )";

        db.execSQL(s,new Object[]{
                Integer.parseInt(diary.getDate()),
                diary.getMeal(),
                diary.getFood(),
                diary.getCal(),
                diary.getDiary(),
                diary.getImage()});
        Log.i("DIARY DATA INSERT : ","SUCCESS");
        Toast.makeText(context,"Diary Insert 완료",Toast.LENGTH_SHORT).show();
    }

    public void insertInfo(Info info){

        SQLiteDatabase db = getWritableDatabase();
        String s;
        s = " INSERT INTO INFODB ( "
                + " NAME, AGE, WEIGHT, HEIGHT, SEX, SCAL )"
                + " VALUES ( ?, ?, ?, ?, ?,? )";

        db.execSQL(s,new Object[]{
                info.getMyName(),
                info.getAge(),
                info.getWeight(),
                info.getHeight(),
                info.getSex(),
                info.getScal()});
        Log.i("INFO DATA INSERT : ","SUCCESS");
        Toast.makeText(context,"Info Insert 완료",Toast.LENGTH_SHORT).show();

    }


    // 해당 날짜가 가진 모든 diary의  데이터(날짜/아점저/음식/칼로리/사진)를 가져오는 메소드
    public ArrayList<Diary> getDiaryDataByDate(int data){
        String s;
        String d = Integer.toString(data);
        s = " SELECT DATE, MEAL, FOOD, CAL, IMAGE FROM FOODIARYDB WHERE DATE = "+d;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(s,null);
        ArrayList<Diary> diaryList = new ArrayList<>();
        Diary diary = null;

        while(cursor.moveToNext()){
            diary = new Diary();
            Log.i("DIARY DATA : ", cursor.getString(0));
            Log.i("DIARY DATA : ", cursor.getString(1));
            Log.i("DIARY DATA : ", cursor.getString(2));
            Log.i("DIARY DATA : ", cursor.getString(3));
            diary.setDate(cursor.getString(0));
            diary.setMeal(cursor.getString(1));
            diary.setFood(cursor.getString(2));
            diary.setCal(cursor.getString(3));
            diary.setImage(cursor.getString(4));

            diaryList.add(diary);
        }
        cursor.close();
        return diaryList;
    }

    public ArrayList<Info> getInfoData(){
        String s;
        s = " SELECT NAME, AGE, WEIGHT, HEIGHT, SEX, SCAL FROM INFODB WHERE _ID = 0";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(s,null);
        ArrayList<Info> infoList = new ArrayList<>();
        Info info = null;

        while(cursor.moveToNext()){
            info = new Info();
            /*
            Log.i("DIARY DATA : ", cursor.getString(0));
            Log.i("DIARY DATA : ", cursor.getString(1));
            Log.i("DIARY DATA : ", cursor.getString(2));
            Log.i("DIARY DATA : ", cursor.getString(3));
             */
            info.setMyName(cursor.getString(0));
            info.setAge(cursor.getInt(1));
            info.setWeight(cursor.getInt(2));
            info.setHeight(cursor.getInt(3));
            info.setSex(cursor.getInt(4));
            info.setScal(cursor.getInt(5));

            infoList.add(info);
            cursor.close();
        }

        Log.i("GET INFO DATA: ","SUCCESS");
        return infoList;
    }

}
