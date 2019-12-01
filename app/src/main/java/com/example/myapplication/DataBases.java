package com.example.myapplication;
import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        // 날짜 / 아점저 / 음식 / 칼로리 / 탄 / 단 / 지
        public static final String DATE = "date";
        public static final String MEAL = "meal";
        public static final String FOOD = "food";
        public static final String CAL = "calorie";
        public static final String CARBON = "carbon";
        public static final String PROTEIN = "protein";
        public static final String FAT = "fat";
        public static final String DIARY = "diary";
        public static final String _TABLENAME0 = "usertable";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+
                "("+_ID+" integer primary key autoincrement, "
                +DATE+ " text not null ,"
                +MEAL+ " text not null, "
                +FOOD+ " text not null, "
                +CAL+ " integer null, "
                +CARBON+ " integer null, "
                +PROTEIN+ " integer null, "
                +FAT+ " integer null, "
                +DIARY+ " text not null); ";
    }

}
