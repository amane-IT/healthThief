package com.example.myapplication;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


// 날짜별 다이어리 리스트(간소화된 정보)를 보여준다.
public class fragmentDiary  extends Fragment {
    View rootView;
    ImageButton camBt;
    Button dateBt;

    private DbHelper DbHelper;
    RecyclerView rvDiary;


    // 날짜를 지정하면 getDiaryData를 통해 버튼에 오늘의 날짜를 보여준다.
    // 그리고 날짜에 해당하는 db 서치 함수 실행
    public DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            int month = monthOfYear+1;
            dateBt.setText(year+"년 "+month+"월 "+dayOfMonth+"일");

            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Calendar time = Calendar.getInstance();
            String date = f.format(time.getTime());

            Log.i("CHOOSEN DATE : ",date);
            getDiaryData(date);

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstaceState){
        super.onActivityCreated(savedInstaceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_diary,container,false);
        camBt = rootView.findViewById(R.id.diarayCam);
        dateBt = rootView.findViewById(R.id.chooseDate);
        rvDiary = rootView.findViewById(R.id.recycler);

        // default 로 버튼엔 오늘 날짜가 적힌다.
        SimpleDateFormat format = new SimpleDateFormat ("yyyy년 MM월 dd일");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        Calendar time = Calendar.getInstance();
        String format_time = format.format(time.getTime());
        String format_time2 = format2.format(time.getTime());
        getDiaryData(format_time2);
        dateBt.setText(format_time);

        /** 디비 생성*/
        String dbname = "foodiaryDB";
        DbHelper = new DbHelper(getActivity(),dbname,null,1);
        DbHelper.testDB();

        dateBt.setOnClickListener(new View.OnClickListener(){
            // fragment_diary 의 최상단 버튼을 클릭하면 날짜를 선택할 수 있다.
            @Override
            public void onClick(View v)
            {
                Log.i("Diary BUTTON","Date Picking");
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, 2019, 12, 01);
                dialog.show();
            }
        });

        camBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Diary Cam","Start Camera");
                Intent intent = new Intent(getActivity(),MyCamera.class);
                startActivity(intent);
            }
        });

                // return inflater.inflate(R.layout.fragment_diary, container, false);
        return rootView;
    }


    // DB에 날짜에 해당하는 data가 있는지 확인한다.
    // data가 있으면 recyclerview에 뿌리고 없으면 없다고 함
    public void getDiaryData(String date){
        Log.i("GET DIARY DATA","START");
        int d = Integer.parseInt(date);
        if (DbHelper == null){
            DbHelper = new DbHelper(getActivity(),"TEST",null,DbHelper.DB_VERSION);
        }

        ArrayList<Diary> diaryList = DbHelper.getDiaryDataByDate(d);
        if(diaryList.size()==0){
            Log.i("DIART DATA : ","EMPTY!");
            Toast.makeText(getContext(),"There is no data!",Toast.LENGTH_SHORT).show();
        }
        else {
            rvDiary.setAdapter(new DiaryListAdapter(diaryList));
            rvDiary.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvDiary.setVisibility(View.VISIBLE);
        }
        Log.i("GET DIARY DATA","END");

        /*

        //WriteDiary.java 에서 db에 넣은 date format은 yyyyMMdd
        mDbOpenHelper = new DbOpenHelper(getActivity());
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        // 추출된 col 갯수 확인
        int col = 0;

        // 테이블 모든 행 선택
        Cursor iCursor = mDbOpenHelper.selectColumns();

        while(iCursor.moveToNext()){
            String tempDate = iCursor.getString(iCursor.getColumnIndex("date"));

            String tempMeal = iCursor.getString(iCursor.getColumnIndex("meal"));
            String tempFood = iCursor.getString(iCursor.getColumnIndex("food"));
            String tempCal = iCursor.getString(iCursor.getColumnIndex("calorie"));
            String tempContent = iCursor.getString(iCursor.getColumnIndex("content"));

            if(tempDate.equals(date)){
                col++;
                //리사이클러뷰에 표시할 데이터 리스트 생성
                //디폴트로 오늘의 다이어리 데이터 가져오기
                ArrayList<String> list = new ArrayList<>();
                for(int i=0;i<3;i++) {
                    list.add(String.format("TEXT %d",i));
                }
                //리사이클러뷰에 linearlayoutmanager 객체 지정
                //해당 fragment를 관리하는 activity를 리턴하는 함수는 getActivity - this 역할
                recyclerView = rootView.findViewById(R.id.recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager((getActivity())));

                //리사이클러뷰에 simpleTextAdapter 객체 지정
                RecyclerviewItemAdapter adapter = new RecyclerviewItemAdapter(list);
                recyclerView.setAdapter((adapter));
            }
        }

        if(col==0){
        }
        else{
            String colnum = Integer.toString(col);
            Log.i("COL NUMBER: ",colnum);
        }
    */
    }


}

