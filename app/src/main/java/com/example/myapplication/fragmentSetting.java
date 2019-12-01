package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class fragmentSetting extends Fragment {

    View rootView;
    Button setBt;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_setting,container,false);
        setBt = rootView.findViewById(R.id.settingDone);

        //setProfile에서 data를 받아온다.
        Intent intent = getActivity().getIntent();
        TextView nameT = getActivity().findViewById(R.id.fname);
        TextView ageT = getActivity().findViewById(R.id.fage);
        TextView weightT = getActivity().findViewById(R.id.fweight);
        TextView sexT = getActivity().findViewById(R.id.fsex);

        String name = intent.getStringExtra("setName");
        if(name != null) nameT.setText(name);
        String age = intent.getStringExtra("setAge");
        if(age != null) ageT.setText(age);
        String weight = intent.getStringExtra("setWeight");
        if(weight != null) weightT.setText(weight);
        String sex = intent.getStringExtra("setSex");
        if(sex != null) sexT.setText(sex);


        // '설정 변경' 버튼을 누르면 프로필을 변경할 수 있다.

        setBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("설정 변경 버튼","클릭함");
                Intent intent = new Intent(getActivity(),setProfile.class);
                startActivity(intent);
            }

        });



        return rootView;

    }


}
