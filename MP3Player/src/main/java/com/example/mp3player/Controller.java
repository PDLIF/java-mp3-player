package com.example.mp3player;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Pane pane;
    @FXML
    private Label songName;
    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void playMedia(){}

    public void pauseMedia(){}

    public void resetMedia(){}

    public void previousMedia(){}

    public void nextMedia(){}

    public void changeSpeed(ActionEvent event){}

    public void Timer(){}

    public void cancelTimer(){}
}