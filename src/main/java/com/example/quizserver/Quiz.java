package com.example.quizserver;

import java.io.Serializable;
import java.util.List;

public class Quiz implements Serializable {

    private String quizName;
    private List<question> questions;

    public Quiz(String quizName, List<question>questions){
        this.quizName = quizName;
        this.questions = questions;
    }

    public String createQuizString(){
        String qName = "QuizName:" + quizName + ";";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(qName);
        for (question q : questions){
            String text = q.createQuestionString();
            stringBuilder.append(text).append(";");
        }

        return stringBuilder.toString().trim();
    }

    public String getQuizName(){
        return quizName;
    }

    public Integer calculateMarks(List<Integer>answers){
        int marks = 0;
        if(answers.size() == questions.size()){
            int arraySize = answers.size();
            for(int i = 0; i <arraySize; i++) {
                if (questions.get(i).checkRightAnswer(answers.get(i))) {
                    marks += 5;
                }
            }
        }else{
            System.out.println("Insufficient answers");
        }

        return marks;
    }
}
