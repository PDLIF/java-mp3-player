package com.example.sampleproject;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EntranceWindowController {

    @FXML
    private Button exitButton;

    @FXML
    private Button startButton;

    @FXML
    void startGame(ActionEvent event) throws IOException {
    	System.out.println("Game started!");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GameWindow.fxml"));
        try
        {
            loader.load();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    void exitGame(ActionEvent event) {
    	System.exit(0);
    }

}
