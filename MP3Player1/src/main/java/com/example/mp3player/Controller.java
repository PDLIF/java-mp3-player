package com.example.mp3player;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;



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
    private Slider songProgressBar;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs;

    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};

    private Timer timer;
    private TimerTask task;
    private boolean running;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        songs = new ArrayList<File>();

        directory = new File("music");

        files = directory.listFiles();

        if (files != null) {

            for (File file : files) {
                songs.add(file);
            }
        }

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songName.setText(songs.get(songNumber).getName());

        for (int i = 0; i < speeds.length; i++) {

            speedBox.getItems().add(Integer.toString(speeds[i]) + "%");
        }

        speedBox.setOnAction(this::changeSpeed);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);

            }
        });

        songProgressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double progress = songProgressBar.getValue() / 100;
                mediaPlayer.seek(media.getDuration().multiply(progress));
                if (running) {
                    cancelTimer();
                    beginTimer();
                }
            }
        });

//        mediaPlayer.statusProperty().addListener((observable, oldStatus, newStatus) -> {
//            if (newStatus == MediaPlayer.Status.PLAYING) {
//                cancelTimer();
//                beginTimer();
//                mediaPlayer.play();
//            }
//        });
    }

    public void playMedia() {

        beginTimer();

        changeSpeed(null);

        mediaPlayer.play();

//        mediaPlayer.setOnEndOfMedia(() -> {
//
//            songProgressBar.setValue(0);
//            nextMedia();
//        });
    }


    public void pauseMedia(){

        cancelTimer();
        mediaPlayer.pause();
    }

    public void resetMedia(){

        cancelTimer();
        songProgressBar.setValue(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void previousMedia(){

        cancelTimer();

        songProgressBar.setValue(0);

        if (songNumber > 0) {

            songNumber--;

            mediaPlayer.stop();

            //cancelTimer();

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songName.setText(songs.get(songNumber).getName());

            //mediaPlayer.play();
        }
        else {

            songNumber = songs.size() - 1;

            mediaPlayer.stop();

//            if (running) {
//                cancelTimer();
//            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songName.setText(songs.get(songNumber).getName());

            //mediaPlayer.play();
        }
    }

    public void nextMedia(){

        cancelTimer();

        songProgressBar.setValue(0);

        if (songNumber < songs.size() - 1) {

            songNumber++;

            mediaPlayer.stop();

//            if (running) {
//                cancelTimer();
//            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songName.setText(songs.get(songNumber).getName());

            //mediaPlayer.play();
        }
        else {

            songNumber = 0;

            mediaPlayer.stop();

//            if (running) {
//                cancelTimer();
//            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songName.setText(songs.get(songNumber).getName());

            //mediaPlayer.play();
        }
    }

    public void changeSpeed(ActionEvent event){

        if (speedBox.getValue() == null) {
            mediaPlayer.setRate(1);
        }
        else {
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        }
    }

    public void beginTimer(){

        timer = new Timer();

        task = new TimerTask() {
            @Override
            public void run() {

                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                double progress = (current / end);
                System.out.println(progress);
                songProgressBar.setValue(progress * 100);


                if (current / end == 1.0) {

                    cancelTimer();
                }
            };
        };

        timer.scheduleAtFixedRate(task, 0, 500);
    }

    public void cancelTimer(){

        running = false;
        timer.cancel();
    }
}