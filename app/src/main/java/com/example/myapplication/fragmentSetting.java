package com.example.myapplication;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

        //db에서 데이터를 받아와 뿌린다
        TextView nameT = rootView.findViewById(R.id.fname);
        TextView ageT = rootView.findViewById(R.id.fage);
        TextView weightT = rootView.findViewById(R.id.fweight);
        TextView sexT = rootView.findViewById(R.id.fsex);
        TextView scalT = rootView.findViewById(R.id.fcalorie);

        DbHelper DBHelper = new DbHelper(getActivity(),"TEST",null,DbHelper.DB_VERSION);
        DBHelper.getInfoData();
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(" SELECT NAME, AGE, WEIGHT, SEX, SCAL FROM INFODB WHERE _ID = 1",null);

        Log.d("Count",String.valueOf(cursor.getCount()));
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            String s="0";
            nameT.setText(cursor.getString(0));
            ageT.setText(cursor.getString(1));
            weightT.setText(cursor.getString(2));
            s = cursor.getString(3);
            if (s=="1") {sexT.setText("남");} else {sexT.setText("여");}
            scalT.setText(cursor.getString(4));
        }

        cursor.close();

        // '설정 변경' 버튼을 누르면 프로필을 변경할 수 있다.
        setBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("설정 변경 버튼","클릭함");
                Intent intent = new Intent(getActivity(),changeProfile.class);
                startActivity(intent);
            }

        });


        return rootView;

    }


}
