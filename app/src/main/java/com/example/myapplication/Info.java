package com.example.myapplication;

//내 정보 기본 관리
public class Info {

    //PK
    private String name;
    private int age;
    private int weight;
    private int height;
    private int sex;
    private int scal;

    public String getMyName(){ return name; }
    public int getAge(){ return age; }
    public int getWeight(){return weight;}
    public int getHeight(){return height;}
    public int getSex(){return sex;}
    public int getScal(){return scal;}

    public void setMyName(String name){this.name = name;}
    public void setAge(int age){this.age = age;}
    public void setWeight(int weight){this.weight=weight;}
    public void setHeight(int height){this.height=height;}
    public void setSex(int sex){this.sex=sex;}
    public void setScal(int scal){this.scal=scal;}

}
