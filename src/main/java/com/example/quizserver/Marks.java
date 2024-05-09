package com.example.quizserver;

public class Marks {
    private int number;
    private String userName;
    private int marks;
    private int place;

    public Marks(int num, String userName, int marks){
        this.number=num;
        this.userName=userName;
        this.marks=marks;
    }

    public int getNumber(){
        return number;
    }
    public String getUserName(){
        return userName;
    }
    public int getMarks(){
        return marks;
    }
    public int getPlace(){
        return place;
    }

    public void setPlace(int place){
        this.place=place;
    }
}
