package com.example.quizserver;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
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
    private Button bt_viewQ;

    @FXML
    private Button bt_createSelect;

    @FXML
    private Button bt_edit;

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
    private Label lb_createNewQuestion;

    @FXML
    private Label lb_questionTitle;

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
    private TableColumn<Marks, String> marks_col_marks;

    @FXML
    private TableColumn<Marks, String> marks_col_number;

    @FXML
    private TableColumn<Marks, String> marks_col_place;

    @FXML
    private TableColumn<Marks, String> marks_col_userName;

    @FXML
    private TableView<Marks> marks_tableView;

    @FXML
    private GridPane menu_gridPane;

    @FXML
    private GridPane pane_gridQus;

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
    private List<Quiz>quizzes = new ArrayList<Quiz>();
    private List<question>questions = new ArrayList<>();
    private StringProperty observableTotalQuiz = new SimpleStringProperty();
    private StringProperty observableTotalUsers = new SimpleStringProperty();
    private Integer[] answerList = {1,2,3,4};
    public static ServerSocket serverSocket;
    public static List<Server> clients;
    private static List<User>users = new ArrayList<User>();
    public static Quiz selectedQuiz;
    public int selectedQuestion;
    public Label selectedQLabel = null;
    public String filePath = "src/main/java/com/example/quizserver/quizData.ser";
    private ObservableList<Quiz> cardQuizzes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        displayUserName();
        answerListNumbers();
        getFile();
        quizDisplayCard();

        //observe total quiz
        lb_totalQuiz.textProperty().bind(observableTotalQuiz);
        String totalQzs = String.valueOf(quizzes.size());
        observableTotalQuiz.set(totalQzs);

        //observe total quiz
        lb_totalUsers.textProperty().bind(observableTotalUsers);
        String totalUsers = String.valueOf(users.size());
        observableTotalUsers.set(totalUsers);

        bt_startQ.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startQuizValid();
            }
        });
    }

    public void switchMenus(ActionEvent event){

        if(bt_startQ.isVisible() && selectedQuizPane != null){
            bt_startQ.setVisible(false);
            bt_viewQ.setVisible(false);
            bt_endQ.setVisible(false);
            selectedQuiz = null;
            selectedQuizPane.setStyle("-fx-background-color: #F9987A; -fx-border-color: black;");
            selectedQuizPane = null;
        }

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
            createQuestionPane();
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

    public void createQuestionPane(){
        int sizeInt = questions.size();
        String size = String.valueOf(sizeInt);
        Label newQ = customLabelQs(size);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);

        newQ.setEffect(dropShadow);

        newQ.setOnMouseClicked(event ->{
            questionSelect(sizeInt-1);
            toggleCreateEditQuestionDetails(true);
            newQ.setStyle("-fx-border-color: black; -fx-background-color: lightblue;");
            if(selectedQLabel != null) selectedQLabel.setStyle("-fx-border-color: black; -fx-background-color: lightgrey;");
            selectedQLabel = newQ;
        });

        pane_gridQus.setHalignment(newQ, HPos.CENTER);
        pane_gridQus.setPadding(new Insets(10));
        pane_gridQus.setHgap(3);
        pane_gridQus.setVgap(3);
        pane_gridQus.add(newQ, (sizeInt-1)%3, (sizeInt-1)/3);

        System.out.println("Question added to the pane!" + (sizeInt-1)/3 + " " + (sizeInt-1)%3);
    }

    public void questionSelect(int id){
        clearQuestion();
        selectedQuestion = id;
        question selectedQ = questions.get(id);
        tf_question.setText(selectedQ.getQuestion());
        tf_answer1.setText(selectedQ.getAnswer1());
        tf_answer2.setText(selectedQ.getAnswer2());
        tf_answer3.setText(selectedQ.getAnswer3());
        tf_answer4.setText(selectedQ.getAnswer4());
        cb_rightAnswer.getSelectionModel().select(selectedQ.getRightAnswer()-1);
    }

    public Label customLabelQs(String text){
        Label newLabel = new Label(text);
        newLabel.setFont(new Font("Arial", 20));

//        newLabel.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
//
//        // Set curved border radius
//        newLabel.setBorder(new javafx.scene.layout.Border(new javafx.scene.layout.BorderStroke(Color.BLACK,
//                javafx.scene.layout.BorderStrokeStyle.SOLID, CornerRadii.EMPTY, javafx.scene.layout.BorderWidths.DEFAULT)));

        newLabel.setStyle("-fx-border-color: black; -fx-background-color: lightgrey;");

        // Set text color
        newLabel.setTextFill(Color.WHITE);

        // Set padding
        newLabel.setPadding(new Insets(10));

        return newLabel;
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
                cardQuizzes.add(quiz);
                writeFile();
                pane_gridQus.getChildren().clear();

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

    public void writeFile(){
        try{
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(quizzes);
            System.out.println("File write Successful!");
        } catch (IOException e) {
            System.out.println("File write failed!");
        }
    }

    public void getFile() {
        try{
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            quizzes = (List<Quiz>) obj;

            System.out.println("Object retrieved successfully");
        }catch (IOException | ClassNotFoundException e){
            System.out.println("File get failed!");
        }
    }

    private AnchorPane selectedQuizPane;
    public void quizDisplayCard(){

        cardQuizzes = FXCollections.observableArrayList(quizzes);

        int row = 0;
        int column = 0;

        menu_gridPane.getRowConstraints().clear();
        menu_gridPane.getColumnConstraints().clear();

        for(int i = 0; i < cardQuizzes.size(); i++){

            try{
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("cardQuiz.fxml"));
                AnchorPane pane = load.load();
                cardQuizController cardC = load.getController();
                cardC.setData(cardQuizzes.get(i));

                int finalI = i;
                pane.setOnMouseClicked(event->{
                    quizSelect(cardQuizzes.get(finalI));
                    pane.setStyle("-fx-background-color: #C56018; -fx-border-color: #803704;");
                    if(selectedQuizPane != null){selectedQuizPane.setStyle("-fx-background-color: #F9987A; -fx-border-color: black;");}
                    selectedQuizPane = pane;
                });

                if(column == 2){
                    column = 0;
                    row += 1;
                }

                menu_gridPane.add(pane,column++, row);
                GridPane.setMargin(pane, new Insets(10));
            }catch (IOException e){
                e.printStackTrace();
                System.out.println("Quiz Display failed!");
            }
        }

    }

    public void quizSelect(Quiz quiz){
        selectedQuiz = quiz;
        if(!bt_startQ.isVisible() && !bt_endQ.isVisible()){
            bt_startQ.setVisible(true);
            bt_viewQ.setVisible(true);
            bt_endQ.setVisible(true);
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
        if(selectedQuiz != null && !isQuizStarted){
            startQuiz();
        }else if(isQuizStarted){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Quiz Start Error");
            alert.setHeaderText(null);
            alert.setContentText("Another Quiz in progress!");
            Optional<ButtonType> option = alert.showAndWait();

            if(option.get().equals(ButtonType.OK)){
                alert.close();
            }
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

    private static boolean isQuizStarted = false;
    private Quiz startedQuiz;
    public void startQuiz(){

        users = new ArrayList<User>();
        clients = new ArrayList<Server>();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    serverSocket = new ServerSocket(1234);
                    int current_clients = 1;
                    startedQuiz = selectedQuiz;
                    isQuizStarted = true;
                    bt_startQ.setDisable(true);
                    System.out.println("Server Started!");

                    while(true){
                        Socket client = serverSocket.accept();

                        Server server = new Server(client, current_clients, selectedQuiz);
                        server.start();
                        server.initialClientNumber(current_clients);

                        //send started quiz
                        //server.sendQuest(startedQuiz.createQuizString());

                        String entryMessage = "User " + current_clients + " Connected";
                        System.out.println(entryMessage);

                        current_clients++;
                        clients.add(server);
                    }
                }catch (IOException e) {
                    System.out.println("Error creating server socket");
                } finally {
                    try {
                        serverSocket.close();
                        System.out.println("Server socket close!");
                    } catch (IOException e) {
                        System.out.println("Server socket error!");
                    }
                }
            }
        }).start();
    }

    public void stopQuiz(){
        try{
            isQuizStarted = false;
            bt_startQ.setDisable(false);
            bt_endQ.setVisible(false);
            calculateMarks();
            sendMarksToUsers();
            prepareMarksForTable();
            serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Server close failed!");
        }
    }

    public static void userRegister(String username, int id){
        User newUser = new User(username, id);
        users.add(newUser);
        System.out.println("User: " + username + " added!");
    }

    public static void recordMarks(String username, String question, String answer){
        for(User user: users){
            if(user.getUserName().equals(username)){
                user.addAnswers(Integer.parseInt(answer));
                System.out.println("User: "+username+" marks added");
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

    //think about use userMarks list than users list
    public void sendMarksToUsers(){
        for(User user: users){
            if(clients.get(user.getUserID()-1) != null){
                Server s = clients.get(user.getUserID()-1);
                s.sendMarks(user.getMarks().toString());
            }
        }
    }

    public void answerListNumbers(){
        List<Integer> answerL = new ArrayList<Integer>();

        answerL.addAll(Arrays.asList(answerList));

        ObservableList listAnswers = FXCollections.observableArrayList(answerL);
        cb_rightAnswer.setItems(listAnswers);
    }

    public void toggleCreateEditQuestionDetails(Boolean needEdit){
        if(selectedQLabel == null && needEdit){
            bt_createQ.setVisible(false);
            bt_edit.setVisible(true);
            bt_createSelect.setVisible(true);
            lb_createNewQuestion.setVisible(true);
            lb_questionTitle.setText("Edit Question");
        } else if (selectedQLabel != null && !needEdit) {
            bt_createQ.setVisible(true);
            bt_edit.setVisible(false);
            bt_createSelect.setVisible(false);
            lb_createNewQuestion.setVisible(false);
            lb_questionTitle.setText("Create Question");
            clearQuestion();
        }
    }

    public void changeTheViewToCreate(){
        toggleCreateEditQuestionDetails(false);
        if(selectedQLabel != null) selectedQLabel.setStyle("-fx-border-color: black; -fx-background-color: lightgrey;");
        selectedQLabel = null;
    }

    //tested
    private ObservableList<Marks> observableUsers;
    public void prepareMarksForTable(){
        List<Marks> userMarks = new ArrayList<>();
        var count = 1;
        for(User user: users){
            Marks newMark = new Marks(count, user.getUserName(), user.getMarks());
            userMarks.add(newMark);
        }

        Collections.sort(userMarks, new QuizScoreComparator());

        var place = 1;
        for (Marks mark: userMarks){
            mark.setPlace(place);
            place++;
        }

        observableUsers = FXCollections.observableArrayList(userMarks);

        marksTableShow();
    }
    public void marksTableShow(){

        marks_col_number.setCellValueFactory(new PropertyValueFactory<>("number"));
        marks_col_userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        marks_col_marks.setCellValueFactory(new PropertyValueFactory<>("marks"));
        marks_col_place.setCellValueFactory(new PropertyValueFactory<>("place"));

        marks_tableView.setItems(observableUsers);

        System.out.println("Marks table show passed");
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
