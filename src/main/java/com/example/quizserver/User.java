package com.example.quizserver;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userName;
    private Integer marks;
    private List<Integer>answers;
    private List<Integer>wrongAnswers;

    public User(String userName){
        this.userName = userName;
        this.marks = 0;
        this.answers = new ArrayList<Integer>();
        this.wrongAnswers = new ArrayList<Integer>();
    }

    public String getUserName(){
        return userName;
    }

    public void addMarks(Integer mark){
        marks = mark;
    }

    public Integer getMarks(){
        return marks;
    }

    public List<Integer> getAnswers(){
        return answers;
    }

    public void addAnswers(Integer answer){
        answers.add(answer);
    }

    public void addWrongAnswers(Integer wrongAnswer){
        wrongAnswers.add(wrongAnswer);
    }

}
