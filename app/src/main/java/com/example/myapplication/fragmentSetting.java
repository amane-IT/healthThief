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
        TextView heightT = rootView.findViewById(R.id.fheight);
        TextView sexT = rootView.findViewById(R.id.fsex);
        TextView scalT = rootView.findViewById(R.id.fcalorie);

        DbHelper DBHelper = new DbHelper(getActivity(),"TEST",null,DbHelper.DB_VERSION);
        DBHelper.getInfoData();
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(" SELECT NAME, AGE, WEIGHT, HEIGHT, SEX, SCAL FROM INFODB WHERE _ID = 1",null);

        Log.d("Count",String.valueOf(cursor.getCount()));
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            //String s="0";
            nameT.setText(cursor.getString(0));
            ageT.setText(cursor.getString(1));
            weightT.setText(cursor.getString(2));
            heightT.setText(cursor.getString(3));
            String s = cursor.getString(4);
            if (s.equals("1")) {sexT.setText("남");} else if(s.equals("2")) {sexT.setText("여");} else{sexT.setText("미정");}
            scalT.setText(cursor.getString(5));
        }

        cursor.close();

        // framgent에서 dialog 사용하는 fragmentDialong
        /*
        https://taehyun71.tistory.com/4
        http://youknow-yoonho.blogspot.com/2016/02/android-fragment-dialog.html

        기타 : https://fluorite94.tistory.com/26

         */

        // '설정 변경' 버튼을 누르면 프로필을 변경할 수 있다.
        //다이얼로그 창이 떠 무엇을 변경할지 물어보고,
        // 변경할 정보를 원래 정보와 비교하며 EditText가 있는 새 다이얼로그 창을 띄운다.
        // 변경에 성공할 경우 db의 프로필 정보가 업데이트되며 원래 창으로 돌아온다. (정보바뀌었는지 확인하는 코드...? 암튼 있던거 같은데 그것도 함 써보고)
        setBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("설정 변경 버튼","클릭함");

                ChangeProfileDialog changeProfileDialog = new ChangeProfileDialog();
                changeProfileDialog.show(getActivity().getSupportFragmentManager(),"tag");
            }

        });


        return rootView;

    }


}
