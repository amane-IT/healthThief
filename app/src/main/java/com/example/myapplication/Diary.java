package com.example.myapplication;

// 다이어리 기본 정보 관리
public class Diary {

    //PK
    private int _id;

    private String date;
    private String meal;
    private String food;
    private String cal;
    private String carbo;
    private String protein;
    private String fat;
    private String diary;
    private String image;

    public int get_id(){ return _id; }
    public String getDate(){ return date;}
    public String getMeal(){ return meal;}
    public String getFood(){ return food;}
    public String getCal(){ return cal;}
    public String getCarbo(){ return carbo; }
    public String getProtein() { return protein; }
    public String getFat() { return fat; }
    public String getDiary(){ return diary;}
    public String getImage() { return image;}

    public void set_id(int _id){ this._id = _id;}
    public void setDate(String date){ this.date = date;}
    public void setMeal(String meal){this.meal = meal;}
    public void setFood(String food){this.food = food;}
    public void setCal(String cal){this.cal = cal;}
    public void setCarbo(String carbo) { this.carbo = carbo; }
    public void setProtein(String protein) { this.protein = protein; }
    public void setFat(String fat) { this.fat = fat; }
    public void setDiary(String diary){this.diary = diary;}
    public void setImage(String image){this.image = image;}


}
