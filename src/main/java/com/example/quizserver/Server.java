package com.example.quizserver;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends Thread {
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Socket client;

    private Quiz quiz;
    private final int clientId;

    Server(Socket client, int id, Quiz qz) throws IOException {

        this.client = client;
        this.clientId = id;
        this.quiz = qz;

        this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    }

    @Override
    public void run(){
        try {

            while(client.isConnected()){
                String messageFromClient = bufferedReader.readLine();
                if (messageFromClient != null) {
                    messageProcess(messageFromClient);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error receiving answers from user");
            closeEverything(client,bufferedReader, bufferedWriter);
        } finally {
            String endMessage = "User " + clientId + " disconnected";
            System.out.println(endMessage);
            closeEverything(client, bufferedReader, bufferedWriter);
        }
    }

    public int getID(){
        return clientId;
    }

    public void messageProcess(String message){
        String patternUserName = "User(\\d+):Registered";
        String patternAnswers = "User:(\\d+),(\\d+):(\\d+)";

        System.out.println(message);

        if(message.matches(patternUserName)){
            Pattern regex = Pattern.compile(patternUserName);
            Matcher matcher = regex.matcher(message);
            //Matcher matcher = Pattern.compile(patternUserName).matcher(message);
            if(matcher.find()){
                String userName = matcher.group(1);
                HomeController.userRegister(userName, clientId);
                sendQuest(quiz.createQuizString());
            }

        } else if (message.matches(patternAnswers)) {
            Pattern regex = Pattern.compile(patternAnswers);
            Matcher matcher = regex.matcher(message);
            //Matcher matcher = Pattern.compile(patternUserName).matcher(message);

            if(matcher.find()){
                String userName = matcher.group(1);
                String qNum = matcher.group(2);
                String aNum = matcher.group(3);
                HomeController.recordMarks(userName, qNum, aNum);
            }
        } else{
            System.out.println("Unknown message: " + message);
        }
    }

    public void initialClientNumber(int clientId){
        try {
            bufferedWriter.write("Client"+clientId+ ":username");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending initial message to client");
        }
    }

    public void sendQuest(String quiz){
        try {
            bufferedWriter.write(quiz);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending quiz to user");
        }
    }

    public void sendMarks(String marks){
        try {
            bufferedWriter.write("Quiz:"+marks);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending marks to user");
        }
    }

    public void closeEverything(Socket client, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(client != null){
                client.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
