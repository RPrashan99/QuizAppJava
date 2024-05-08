package com.example.quizserver;

import java.util.Objects;

public class question {
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private Integer rightAnswer;

    public question(String question, String answer1, String answer2, String answer3, String answer4, Integer answer){
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.rightAnswer = answer;
    }

    public String createQuestionString(){
        String q = "question:" + question + ",";
        String a1 = "1:" + answer1 + ",";
        String a2 = "2:" + answer2 + ",";
        String a3 = "3:" + answer3 + ",";
        String a4 = "4:" + answer4;

        return q + a1 + a2 + a3 + a4;
    }

    public boolean checkRightAnswer(Integer givenAnswer){
        if(Objects.equals(rightAnswer, givenAnswer)){
            return true;
        }else{
            return false;
        }
    }

    public String getQuestion(){
        return question;
    }
    public String getAnswer1(){
        return answer1;
    }
    public String getAnswer2(){
        return answer2;
    }
    public String getAnswer3(){
        return answer3;
    }
    public String getAnswer4(){
        return answer4;
    }

    public Integer getRightAnswer(){
        return rightAnswer;
    }
}
