package com.example.myapplication;

// 기본 정보 관리
public class Diary {

    //PK
    private int _id;

    private String date;
    private String meal;
    private String food;
    private String cal;
    private String diary;

    public int get_id(){ return _id; }
    public String getDate(){ return date;}
    public String getMeal(){ return meal;}
    public String getFood(){ return food;}
    public String getCal(){ return cal;}
    public String getDiary(){ return diary;}

    public void set_id(int _id){ this._id = _id;}
    public void setDate(String date){ this.date = date;}
    public void setMeal(String meal){this.meal = meal;}
    public void setFood(String food){this.food = food;}
    public void setCal(String cal){this.cal = cal;}
    public void setDiary(String diary){this.diary = diary;}

}
