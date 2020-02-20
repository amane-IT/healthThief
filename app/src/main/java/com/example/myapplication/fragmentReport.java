package com.example.myapplication;
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

public class fragmentReport extends Fragment{

    View rootView;
    Button graphBt;
    Button monthBt;
    Button trendBt;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //initialize
        rootView = inflater.inflate(R.layout.fragment_report,container,false);
        graphBt = rootView.findViewById(R.id.getGraph);
        monthBt = rootView.findViewById(R.id.getMonthly);
        trendBt = rootView.findViewById(R.id.getTrend);

        //각 버튼 클릭시 해당 레포트 결과 화면 띄움
        graphBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Report","Start Graph");
                Intent intent = new Intent(getActivity(),getGraph.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        monthBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Report","Start Monthly");
                Intent intent = new Intent(getActivity(),getMonthly.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        trendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Report","Start Trend");
                Intent intent = new Intent(getActivity(),getTrend.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return rootView;

    }
}
