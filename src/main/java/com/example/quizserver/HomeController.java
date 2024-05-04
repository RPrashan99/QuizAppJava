package com.example.quizserver;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {

    @FXML
    private Button bt_create;

    @FXML
    private Button bt_createQ;

    @FXML
    private Button bt_endQ;

    @FXML
    private Button bt_exit;

    @FXML
    private Button bt_finishQ;

    @FXML
    private Button bt_home;

    @FXML
    private Button bt_startQ;

    @FXML
    private ComboBox<?> cb_rightAnswer;

    @FXML
    private Button bt_summary;

    @FXML
    private Label lb_totalQuiz;

    @FXML
    private Label lb_totalUsers;

    @FXML
    private Label lb_username;

    @FXML
    private AnchorPane main_page;

    @FXML
    private AnchorPane pane_create;

    @FXML
    private AnchorPane pane_home;

    @FXML
    private AnchorPane pane_summary;

    @FXML
    private AnchorPane pane_quizFunction;

    @FXML
    private TextField tf_answer1;

    @FXML
    private TextField tf_answer2;

    @FXML
    private TextField tf_answer3;

    @FXML
    private TextField tf_answer4;

    @FXML
    private TextField tf_question;

    @FXML
    private TextField tf_quizName;

    private Alert alert;

    private List<Quiz>quizzes = new ArrayList<>();

    private List<question>questions = new ArrayList<>();

    private StringProperty observableTotalQuiz = new SimpleStringProperty();

    private StringProperty observableTotalUsers = new SimpleStringProperty();

    private Integer[] answerList = {1,2,3,4};

    public static ServerSocket serverSocket;

    private static List<User>users;

    public static Quiz selectedQuiz;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        displayUserName();
        answerListNumbers();

        //observe total quiz
        lb_totalQuiz.textProperty().bind(observableTotalQuiz);
        String totalQzs = String.valueOf(quizzes.size());
        observableTotalQuiz.set(totalQzs);

        //observe total quiz
        lb_totalUsers.textProperty().bind(observableTotalUsers);
        String totalUsers = String.valueOf(users.size());
        observableTotalUsers.set(totalUsers);
    }

    public void switchMenus(ActionEvent event){

        if(event.getSource() == bt_home){
            if(!pane_home.isVisible()){
                clearQuestion();
                tf_quizName.clear();
                pane_create.setVisible(false);
                pane_summary.setVisible(false);
                pane_home.setVisible(true);
            }
        } else if (event.getSource() == bt_create) {
            if(!pane_create.isVisible()){
                pane_home.setVisible(false);
                pane_summary.setVisible(false);
                pane_create.setVisible(true);
            }
        } else if (event.getSource() == bt_summary) {
            if(!pane_summary.isVisible()){
                pane_home.setVisible(false);
                pane_create.setVisible(false);
                pane_summary.setVisible(true);
            }
        } else{
            System.out.println("Need to setup!");
        }
    }

    public void createQuestion(ActionEvent event){

        if(!tf_question.getText().isBlank() && !tf_answer1.getText().isBlank() && !tf_answer2.getText().isBlank()
                && !tf_answer3.getText().isBlank() && !tf_answer4.getText().isBlank() && cb_rightAnswer.getSelectionModel().getSelectedItem() != null){

            question quest = new question(tf_question.getText(),tf_answer1.getText(),tf_answer2.getText(),
                    tf_answer3.getText(), tf_answer4.getText(), (Integer) cb_rightAnswer.getSelectionModel().getSelectedItem());
            questions.add(quest);
            clearQuestion();
        }else{
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Question Create Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the fields !");
            Optional<ButtonType> option = alert.showAndWait();

            if(option.get().equals(ButtonType.OK)){
                alert.close();
            }
        }
    }

    public void createQuiz(){
        if(!tf_quizName.getText().isBlank()){
            boolean isQuizNameTaken = false;
            for(Quiz quiz: quizzes){
                if(quiz.getQuizName().equals(tf_quizName.getText())){
                    isQuizNameTaken = true;
                    break;
                }
            }

            if(isQuizNameTaken){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Quiz Create Error");
                alert.setHeaderText(null);
                alert.setContentText("Quiz name already exists. Please give another name for the quiz !");
                Optional<ButtonType> option = alert.showAndWait();

                if(option.get().equals(ButtonType.OK)){
                    alert.close();
                }
            }else{
                Quiz quiz = new Quiz(tf_quizName.getText(),questions);
                quizzes.add(quiz);

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Quiz Create");
                alert.setHeaderText(null);
                alert.setContentText(" Quiz successfully created!");
                Optional<ButtonType> option = alert.showAndWait();

                if(option.get().equals(ButtonType.OK)){
                    updateTotalQuizValue();
                    tf_quizName.clear();
                    clearQuestion();
                    alert.close();
                }
            }
        }else{
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Quiz Create Error");
            alert.setHeaderText(null);
            alert.setContentText("Please give name for the quiz !");
            Optional<ButtonType> option = alert.showAndWait();

            if(option.get().equals(ButtonType.OK)){
                alert.close();
            }
        }
    }

    public void updateTotalQuizValue(){
        String totalQzs = String.valueOf(quizzes.size());
        observableTotalQuiz.set(totalQzs);
    }

    public void updateTotalUserValue(){
        String totalUsers = String.valueOf(users.size());
        observableTotalUsers.set(totalUsers);
    }

    public void clearQuestion(){
        tf_question.clear();
        tf_answer1.clear();
        tf_answer2.clear();
        tf_answer3.clear();
        tf_answer4.clear();
        cb_rightAnswer.setValue(null);
    }

    public void displayUserName(){
        String user = Data.username;
        String text = "Welcome " + user + " !";
        lb_username.setText(text);
    }

    public void startQuizValid(){
        if(selectedQuiz != null){
            startQuiz();
        }else{
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Quiz Start Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a quiz to start !");
            Optional<ButtonType> option = alert.showAndWait();

            if(option.get().equals(ButtonType.OK)){
                alert.close();
            }
        }
    }

    public void startQuiz(){

        this.users = new ArrayList<User>();
        new Thread(new Runnable() {

            public List<Server> clients = new ArrayList<Server>();
            @Override
            public void run() {
                try{
                    serverSocket = new ServerSocket(1234);
                    int current_clients = 1;

                    while(true){
                        Socket client = serverSocket.accept();

                        Server server = new Server(client, current_clients);
                        server.initialClientNumber(current_clients);
                        server.start();

                        String entryMessage = "User " + current_clients + " Connected";
                        System.out.println(entryMessage);

                        current_clients++;
                        clients.add(server);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error creating server socket");
                } finally {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopQuiz(){
        try{
            calculateMarks();
            serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Server close failed!");
        }
    }

    public static void userRegister(String username){
        User newUser = new User(username);
        users.add(newUser);
        System.out.println("User: " + username + "added!");
    }

    public static void recordMarks(String username, String question, String answer){
        for(User user: users){
            if(user.getUserName().equals(username)){
                user.addAnswers(Integer.parseInt(answer));
                break;
            }
        }
    }

    public void calculateMarks(){
        for(User user: users){
            if(!user.getAnswers().isEmpty()){
                Integer totalMarks = selectedQuiz.calculateMarks(user.getAnswers());
                user.addMarks(totalMarks);
            }
        }
    }

    public void answerListNumbers(){
        List<Integer> answerL = new ArrayList<Integer>();

        answerL.addAll(Arrays.asList(answerList));

        ObservableList listAnswers = FXCollections.observableArrayList(answerL);
        cb_rightAnswer.setItems(listAnswers);
    }

    public void ExitButtonOnAction(ActionEvent event){

        try{
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit App");
            alert.setHeaderText(null);
            alert.setContentText("Are you want to exit ?");
            Optional<ButtonType> option = alert.showAndWait();

            if(option.get().equals(ButtonType.OK)){

                bt_exit.getScene().getWindow().hide();

                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                stage.setTitle("Quiz App");

                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) bt_exit.getScene().getWindow();
        stage.close();
    }
}
