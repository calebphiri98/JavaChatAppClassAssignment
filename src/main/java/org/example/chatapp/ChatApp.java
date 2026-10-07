package org.example.chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(ChatApp.class.getResource("chat.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(ChatApp.class.getResource("/styles.css").toExternalForm());
        stage.setTitle("Chat App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
