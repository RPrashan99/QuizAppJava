package com.example.quizserver;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField tf_username;

    @FXML
    private PasswordField pf_password;

    @FXML
    private Button bt_login;

    @FXML
    private Button bt_cancel;

    @FXML
    private Label lb_login;

    private String username = "Rush";
    private String password = "quiz";

    public void loginButtonOnAction(ActionEvent event) throws IOException {
        if(!tf_username.getText().isBlank() && !pf_password.getText().isBlank()){
            if(loginValidation()){

                Data.username = tf_username.getText();
                Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Quiz APP");
                stage.setMinWidth(600);
                stage.setMinHeight(400);
                stage.setScene(scene);
                stage.show();

                bt_login.getScene().getWindow().hide();
            }
        }else{
            lb_login.setText("Enter username and password!");
        }
    }

    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) bt_cancel.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public boolean loginValidation(){
        if(Objects.equals(tf_username.getText(), username) && Objects.equals(pf_password.getText(), password)){
            lb_login.setText("Try to login");
            return true;
        }else{
            lb_login.setText("Invalid Login, Try again!");
            return false;
        }
    }
}