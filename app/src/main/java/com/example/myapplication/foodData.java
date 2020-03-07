package com.example.myapplication;

public class foodData {

    public int _id;

    public String name;
    public int kcal;
    public float carbo;
    public float protein;
    public float fat;
    public int serving;


    public int get_id() { return _id; }

    public String getName() { return name; }
    public int getKcal() { return kcal; }
    public float getCarbo() { return carbo; }
    public float getProtein() { return protein; }
    public float getFat() { return fat; }
    public int getServing() { return serving; }


    public void set_id(int _id) { this._id = _id; }
    public void setName(String name) { this.name = name; }
    public void setKcal(int kcal) { this.kcal = kcal; }
    public void setCarbo(float carbo) { this.carbo = carbo; }
    public void setProtein(float protein) { this.protein = protein; }
    public void setFat(float fat) { this.fat = fat; }
    public void setServing(int serving) { this.serving = serving; }
}
