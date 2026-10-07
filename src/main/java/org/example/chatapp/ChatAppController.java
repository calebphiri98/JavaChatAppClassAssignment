package org.example.chatapp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatAppController {

    @FXML
    private VBox chatPane;

    @FXML
    private TextArea textArea;

    private String lastMessageTime = null;

    public ChatAppController() {

    }

    @FXML
    public void initialize() {
        startBackgroundChecker();
    }


    private void startBackgroundChecker() {
        Thread checker = new Thread(() -> {
            while (true) {
                try {
                    List<String> newMessages = fetchNewMessages();
                    if (!newMessages.isEmpty()) {
                        Platform.runLater(() -> showIncomingMessages(newMessages));
                    }
                } catch (IOException e) {
                    System.out.println("Could not reach the server: " + e.getMessage());
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        checker.setDaemon(true);
        checker.start();
    }

    private synchronized List<String> fetchNewMessages() throws IOException {
        List<String> texts = new ArrayList<>();
        Socket socket = new Socket("localhost", 3000);
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        if (lastMessageTime == null)
            writer.println("GET/ALL");
        else
            writer.println("GET/" + lastMessageTime);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("_END_")) break;

            String[] res = line.split("/", 2);
            lastMessageTime = res[0];
            texts.add(res[1]);
        }
        socket.close();
        return texts;
    }

    private void showIncomingMessages(List<String> texts) {
        for (String text : texts) {
            Label label1 = new Label(text);
            label1.setWrapText(true);
            label1.getStyleClass().add("incoming-message");
            HBox wrapper = new HBox(label1);
            wrapper.setAlignment(Pos.CENTER_LEFT);
            chatPane.getChildren().add(wrapper);
        }
    }

    @FXML
    public void sendButtonClick(ActionEvent actionEvent) {
        String text = textArea.getText().replace("\n", " ").trim();
        if (text.isEmpty()) return;

        try {
            synchronized (this) {
                Socket socket = new Socket("localhost", 3000);

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("POST/" + text);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("_END_")) break;

                    String[] res = line.split("/", 2);
                    lastMessageTime = res[0];

                    Label label1 = new Label(res[1]);
                    label1.getStyleClass().add("user-message");
                    label1.setWrapText(true);
                    HBox wrapper = new HBox(label1);
                    wrapper.setAlignment(Pos.CENTER_RIGHT);
                    chatPane.getChildren().add(wrapper);
                }
                textArea.clear();
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void checkMessageButtonClick(ActionEvent actionEvent) throws IOException {
        List<String> newMessages = fetchNewMessages();
        showIncomingMessages(newMessages);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(newMessages.size() + " new Messages");
        alert.showAndWait();
    }
}
