package com.example.quizserver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class cardQuizController implements Initializable {

    @FXML
    private AnchorPane card_Quiz;

    @FXML
    private Label lb_cardQuizName;

    @FXML
    private Label lb_cardQuizNo;

    private Quiz quizData;

    public void setData(Quiz quiz){
        this.quizData = quiz;

        lb_cardQuizName.setText(quiz.getQuizName());
        lb_cardQuizNo.setText(quiz.numOfQuestions());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        bt_select.setOnMouseClicked(event->{
//            HomeController.quizSelect(quizData);
//        });
    }
}
