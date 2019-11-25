package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

//DateFragment 에서 날짜 정하면 fragment_diary의 버튼 내용이 바뀌어야 함
//근데 findViewById를 쓰려면 AppCompatActivity를 클래스에서 확장해ㅑ함
//근데 다른걸로 extend 되어서 확장 없이 activity를 써야함
//그럴려면 번들을 어쩌고저쩌고해서,,, << 이 단계임
//
public class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public Activity activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yyyy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yyyy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yyyy, int mm, int dd) {
        populateSetDate(yyyy, mm+1, dd);
    }
    public void populateSetDate(int year, int month, int day) {
        Button dateBtf = this.activity.findViewById(R.id.chooseDate);
        dateBtf.setText(year+"년 "+month+"월 "+day+"일");
    }
}
