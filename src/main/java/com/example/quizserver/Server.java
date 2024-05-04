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
    private final int clientId;

    Server(Socket client, int id) throws IOException {

        this.client = client;
        this.clientId = id;

        this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    }

    @Override
    public void run(){
        while(client.isConnected()){
            try {
                String messageFromClient = bufferedReader.readLine();
                messageProcess(messageFromClient);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error receiving answers from user");
                closeEverything(client,bufferedReader, bufferedWriter);
                break;
            } finally {
                String endMessage = "User " + clientId + " disconnected";
                System.out.println(endMessage);
                closeEverything(client, bufferedReader, bufferedWriter);
            }
        }
    }

    public void messageProcess(String message){
        String patternUserName = "User(\\d+):Registered";
        String patternAnswers = "User:(\\d+),(\\d+):(\\d+)";

        if(message.matches(patternUserName)){
            Matcher matcher = Pattern.compile(patternUserName).matcher(message);
            String userName = matcher.group(1);
            HomeController.userRegister(userName);
        } else if (message.matches(patternAnswers)) {
            Matcher matcher = Pattern.compile(patternUserName).matcher(message);
            String userName = matcher.group(1);
            String qNum = matcher.group(2);
            String aNum = matcher.group(3);
            HomeController.recordMarks(userName, qNum, aNum);
        } else{
            System.out.println("Unknown message: " + message);
        }
    }

    public void initialClientNumber(int clientId){
        try {
            bufferedWriter.write("Client"+clientId+ ":Registered");
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

//    public void receiveMessageFromClient(VBox vBox){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(client.isConnected()){
//                    try {
//                        String messageFromClient = bufferedReader.readLine();
//                        ServerController.addLabel(messageFromClient, vBox);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        System.out.println("Error receiving answers from client");
//                        closeEverything(client,bufferedReader, bufferedWriter);
//                        break;
//                    }
//                }
//            }
//        }).start();
//    }

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
