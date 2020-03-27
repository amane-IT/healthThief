package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


public class ChangeProfileDialog extends DialogFragment {

    private Fragment fragment;
    String change;
    RadioGroup rd;
    String changing;
    setProfile sp;
    DbHelper DBHelper;
    SQLiteDatabase db;
    int p;
    String sexChange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_change_dialog, container, false);
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
        sp = new setProfile();

        DBHelper = new DbHelper(getActivity(),"TEST",null,DbHelper.DB_VERSION);
        db = DBHelper.getReadableDatabase();

        Spinner spinner = (Spinner) view.findViewById(R.id.profileSpinner);
        Button changeBtn = (Button) view.findViewById(R.id.changeProfileBtn);
        Button cancleBtn = (Button) view.findViewById(R.id.cancleChangeBtn);
        EditText changeEdit = (EditText) view.findViewById(R.id.profileEdit);
        rd = (RadioGroup) view.findViewById(R.id.changeRadio);

        String[] str = getResources().getStringArray(R.array.profile_arr);
        // 아래 코드 layout 수정. 해당 레이아웃은 spinner 내부 레이아웃 작성으로 textview 하나만 들어간 xml이어야함
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getContext(),R.layout.change_spinner,str);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: change = "NAME"; rd.setVisibility(View.GONE); changeEdit.setVisibility(View.VISIBLE); break;
                    case 1: change = "AGE"; rd.setVisibility(View.GONE); changeEdit.setVisibility(View.VISIBLE); p =1; break;
                    case 2 : change = "WEIGHT"; rd.setVisibility(View.GONE); changeEdit.setVisibility(View.VISIBLE); p=2; break;
                    case 3 : change = "HEIGHT";rd.setVisibility(View.GONE); changeEdit.setVisibility(View.VISIBLE); p=3; break;
                    case 4 : change = "SEX"; rd.setVisibility(View.VISIBLE); changeEdit.setVisibility(View.GONE); p = 4; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity().getApplicationContext(),"nothing changed...",Toast.LENGTH_SHORT).show();
            }
        });


        rd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.male){
                    sexChange = "1";
                    Toast.makeText(getActivity().getApplicationContext(),changing,Toast.LENGTH_SHORT).show();
                }
                else if(i == R.id.female){
                    sexChange = "2";
                    Toast.makeText(getActivity().getApplicationContext(),changing,Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO : changeBtn 클릭시 DB 업데이트가 이루어짐
        changeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                changing = changeEdit.getText().toString();
                if (change=="SEX"){
                    changing = sexChange;
                }
                // TODO : 이름, 권칼을 제외한 나머지가 바뀌었을때 변경된 권칼을 계산하고 함께 업데이트
                db.execSQL(" UPDATE INFODB SET "+change+" = '"+ changing+ "' WHERE _ID=1");
                if (change != "NAME") changeScal(p,changing);
                Cursor check = db.rawQuery("SELECT * FROM INFODB",null);
                check.moveToFirst();
                for (int i=1; i<7; i++){ Log.i("checkProfile:",check.getString(i));}

                Toast.makeText(getActivity().getApplicationContext(),"update profile!",Toast.LENGTH_SHORT).show();
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismiss();
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (fragment != null) {
                    DialogFragment dialogFragment = (DialogFragment) fragment;
                    dialogFragment.dismiss();
                }
            }
        });

        return view;
    }

    public void changeScal(int p, String ch){
        String position;
        int num = Integer.parseInt(ch);
        int changedScal=1234;

        Cursor c = db.rawQuery("SELECT AGE,WEIGHT,HEIGHT,SEX,SCAL FROM INFODB",null);
        c.moveToFirst();

        int age = Integer.parseInt(c.getString(0));
        int weight = Integer.parseInt(c.getString(1));
        int height = Integer.parseInt(c.getString(2));
        int sex = Integer.parseInt(c.getString(3));

        switch (p){
            case 1:
                changedScal = sp.suggestCal(num,weight,height,sex);
            break;
            case 2:
                changedScal = sp.suggestCal(age,num,height,sex);
                break;
            case 3:
                changedScal = sp.suggestCal(age,weight,num,sex);
                break;
            case 4:
                changedScal = sp.suggestCal(age,weight,height,num);
                break;
        }

        position = String.valueOf(changedScal);
        db.execSQL(" UPDATE INFODB SET SCAL = '"+ position + "' WHERE _ID=1");
        c.close();
    }

}
