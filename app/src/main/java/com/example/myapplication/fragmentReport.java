package com.example.myapplication;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class fragmentReport extends Fragment implements MainActivity.OnBackPressedListener{

    View rootView;
    Button monthBt;
    Button trendBt;

    fragmentDiary mainFragment = new fragmentDiary();


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //initialize
        rootView = inflater.inflate(R.layout.fragment_report,container,false);
        monthBt = rootView.findViewById(R.id.getMonthly);
        trendBt = rootView.findViewById(R.id.getTrend);



        /*
        //각 버튼 클릭시 해당 레포트 결과 화면 띄움
        graphBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Report","Start Graph");
                Intent intent = new Intent(getActivity(),getTrend.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
         */


        monthBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Report","Start Monthly");
                Intent intent = new Intent(getActivity(),getGraph.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        trendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Report","Start Trend");
                Intent intent = new Intent(getActivity(),getMonthly.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return rootView;

    }


    @Override
    public void onBack() {
        Log.e("Other", "onBack()");
        // 리스너를 설정하기 위해 Activity 를 받아옵니다.
        MainActivity activity = (MainActivity)getActivity();
        // 한번 뒤로가기 버튼을 눌렀다면 Listener 를 null 로 해제해줍니다.
        activity.setOnBackPressedListener(null);
        // MainFragment 로 교체
        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, mainFragment).commit();
        // Activity 에서도 뭔가 처리하고 싶은 내용이 있다면 하단 문장처럼 호출해주면 됩니다.
        // activity.onBackPressed();
    }

    // Fragment 호출 시 반드시 호출되는 오버라이드 메소드입니다.
    @Override
    //혹시 Context 로 안되시는분은 Activity 로 바꿔보시기 바랍니다.
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("Other", "onAttach()");
        ((MainActivity)context).setOnBackPressedListener(this);
    }
}