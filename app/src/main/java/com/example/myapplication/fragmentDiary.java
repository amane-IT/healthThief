package com.example.myapplication;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import static android.app.Activity.RESULT_OK;


// 날짜별 다이어리 리스트(간소화된 정보)를 보여준다.
public class fragmentDiary  extends Fragment {
    View rootView;
    ImageButton camBt;
    Button dateBt;


    private DbHelper DbHelper;
    String dbName;
    ArrayList<Diary> diaryList;
    RecyclerView recyclerView;
    DiaryListAdapter diaryListAdapter;
    RecyclerView.LayoutManager layoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        diaryListAdapter = new DiaryListAdapter(diaryList, getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // initialize
        rootView = inflater.inflate(R.layout.fragment_diary,container,false);
        camBt = rootView.findViewById(R.id.diarayCam);
        dateBt = rootView.findViewById(R.id.chooseDate);

        //디비 생성
        if (DbHelper == null){
            dbName = "foodiaryDB";
            DbHelper = new DbHelper(getActivity(),"TEST",null,DbHelper.DB_VERSION);
            DbHelper.testDB();
        }

        SimpleDateFormat format = new SimpleDateFormat ("yyyy년 MM월 dd일");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        //현재 날짜 가져옴
        Calendar time = Calendar.getInstance();
        //디폴트 - 오늘 날짜의 버튼
        String format_time = format.format(time.getTime());
        dateBt.setText(format_time);
        //디폴트 - 오늘 날짜의 다이어리 목록
        String format_time2 = format2.format(time.getTime());
        int ft2 = Integer.parseInt(format_time2);
        diaryList = DbHelper.getDiaryDataByDate(ft2);
        if(diaryList.size()==0){
            Log.i("DIART DATA : ","EMPTY!");
            Toast.makeText(getContext(),"There is no data!",Toast.LENGTH_SHORT).show();
        }
        else{
            Log.i("DIART DATA : ","NOT EMPTY!");
            Toast.makeText(getContext(),"data list set!",Toast.LENGTH_SHORT).show();
        }

        // RecyclerView
        // obtain handle, connect to layout manager, attach adapter for the data to be displayed
        recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        diaryListAdapter = new DiaryListAdapter(diaryList,getActivity());
        recyclerView.setAdapter(diaryListAdapter);
        diaryListAdapter.notifyDataSetChanged();

        // Button Click Event
        dateBt.setOnClickListener(new View.OnClickListener(){
            // fragment_diary 의 최상단 버튼을 클릭하면 날짜를 선택할 수 있다.
            @Override
            public void onClick(View v)
            {
                Log.i("Diary BUTTON","Date Picking");
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, 2019, 11, 01);
                dialog.show();
            }
        });

        camBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Diary Cam","Start Camera");
                Intent intent = new Intent(getActivity(),MyCamera.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        // return inflater.inflate(R.layout.fragment_diary, container, false);
        return rootView;
    }

    // 선택한 날짜로 버튼 이름이 바뀜
    // 그리고 날짜에 해당하는 getDiaryData 실행
    public DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            int month = monthOfYear+1;
            String y = String.valueOf(year);
            String m = String.valueOf(month);
            if(month<10){m= "0"+m;}
            String d = String.valueOf(dayOfMonth);
            if(dayOfMonth<10){d="0"+d;}
            String date = y+m+d;
            Log.i("CHOOSEN DATE : ",date);
            dateBt.setText(year+"년 "+month+"월 "+dayOfMonth+"일");
            getDiaryData(date);
        }
    };

    // DB에 날짜에 해당하는 data가 있는지 확인한다.
    // data가 있으면 recyclerview에 뿌리고 없으면 없다고 함
    public void getDiaryData(String date){
        Log.i("GET DIARY DATA","START");
        int d = Integer.parseInt(date);
        diaryList = DbHelper.getDiaryDataByDate(d);
        if(diaryList.size()==0){
            changeData(diaryList);
            Log.i("DIARY DATA : ","EMPTY!");
            Toast.makeText(getContext(),"There is no data!",Toast.LENGTH_SHORT).show();
        }
        else {
            changeData(diaryList);
        }
        Log.i("GET DIARY DATA","END");
    }

    // 데이터 바뀌었을 때 사용
    public void changeData(ArrayList<Diary> diaryList) {
        diaryListAdapter.clear();
        diaryListAdapter = new DiaryListAdapter(diaryList,getActivity());
        recyclerView.setAdapter(diaryListAdapter);
        diaryListAdapter.notifyDataSetChanged();
    }

}

