package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MonthlyDialog extends Dialog {

    getMonthly gm;
    MonthlyDialog mdialog;


    public MonthlyDialog(Context context){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.monthly_dialog);

        mdialog = this;

        TextView carbonT = (TextView) this.findViewById(R.id.mdialog_carbonT);
        TextView carbon = (TextView) this.findViewById(R.id.mdialog_carbon);
        TextView proteinT = (TextView) this.findViewById(R.id.mdialog_proteinT);
        TextView protein = (TextView) this.findViewById(R.id.mdialog_protein);
        TextView fatT = (TextView) this.findViewById(R.id.mdialog_fatT);
        TextView fat = (TextView) this.findViewById(R.id.mdialog_fat);
        TextView breakfastT = (TextView) this.findViewById(R.id.mdialog_breakfastT);
        TextView breakfast = (TextView) this.findViewById(R.id.mdialog_breakfast);
        TextView lunchT = (TextView) this.findViewById(R.id.mdialog_lunchT);
        TextView lunch = (TextView) this.findViewById(R.id.mdialog_lunch);
        TextView dinnerT = (TextView) this.findViewById(R.id.mdialog_dinnerT);
        TextView dinner = (TextView) this.findViewById(R.id.mdialog_dinner);


        carbonT.setText("탄수화물");
        proteinT.setText("단백질");
        fatT.setText("지방");
        breakfastT.setText("아침");
        lunchT.setText("점심");
        dinnerT.setText("저녁");

        /**
         * 데이터 넘겨받기 좃대슴ㅠ
         */


        // getMonthly 에서 intent.엑스트롸 어쩌구로 넘겨받은 데이터 표시하기
        Intent intent = getIntent();
        ArrayList<Diary> list = (ArrayList<Diary>) intent.getSerializableExtra("diaries");


        carbon.setText("탄"+"g");
        protein.setText("단"+"g");
        fat.setText("지"+"g");
        breakfast.setText("념");
        lunch.setText("냠");
        dinner.setText("뇸");


        Button oBtn = (Button)this.findViewById(R.id.mdialog_btn);
        oBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickBtn(v);
            }
        });

    }

    public void onClickBtn(View _oView)
    {
        this.dismiss();
    }


}
