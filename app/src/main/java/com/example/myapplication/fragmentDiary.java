package com.example.myapplication;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class fragmentDiary  extends Fragment {
    View rootView;
    Button dateBt;

    public fragmentDiary(){
        //Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_diary,container,false);
        dateBt = rootView.findViewById(R.id.chooseDate);

        // default 로 버튼엔 오늘 날짜가 적힌다.
        SimpleDateFormat format = new SimpleDateFormat ("yyyy년 MM월 dd일");
        Calendar time = Calendar.getInstance();
        String format_time = format.format(time.getTime());
        dateBt.setText(format_time);

        dateBt.setOnClickListener(new View.OnClickListener(){
            // fragment_diary 의 최상단 버튼을 클릭하면 날짜를 선택할 수 있다.
            @Override
            public void onClick(View v)
            {
                Log.i("Diary BUTTON","Date Picking");
                DialogFragment newFragment = new DateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        //리사이클러뷰에 표시할 데이터 리스트 생성
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<4; i++)
        {
            list.add(String.format("TEXT %d",i));
        }
        //리사이클러뷰에 linearlayoutmanager 객체 지정
        RecyclerView recyclerView = rootView.findViewById((R.id.recycler));
        //해당 fragment를 관리하는 activity를 리턴하는 함수는 getActivity - this 역할
        recyclerView.setLayoutManager(new LinearLayoutManager((getActivity())));

        //리사이클러뷰에 simpleTextAdapter 객체 지정
        RecyclerviewItemAdapter adapter = new RecyclerviewItemAdapter(list);
        recyclerView.setAdapter((adapter));

        // return inflater.inflate(R.layout.fragment_diary, container, false);
        return rootView;
    }


}

