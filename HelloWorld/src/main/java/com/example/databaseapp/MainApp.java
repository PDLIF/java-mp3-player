package com.example.databaseapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/databaseapp/layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Database App");
        stage.setScene(scene);
        stage.show();

        Controller controller = fxmlLoader.getController();
        // Устанавливаем primaryStage в контроллер
        controller.setPrimaryStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
