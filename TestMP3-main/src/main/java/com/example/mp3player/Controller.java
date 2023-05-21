package com.example.mp3player;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.stage.FileChooser;
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

    @FXML
    private Label dragText;
    @FXML
    ListView<String> musicList;
    @FXML
    TextField musicSearchField;
    @FXML
    Button addButton;

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
    private static ArrayList<String> songsNames = new ArrayList<String>();

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


        //Создание списка песен
        for (File file : files) {
            songsNames.add(file.getName());
        }

        musicList.getItems().addAll(songsNames);
        //Работа со списком песен
        musicList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                mediaPlayer.pause();
                //Поиск выбранного трека
                int counter = -1;
                for (int i = 0; i < songsNames.size(); i++) {
                    if (Objects.equals(songsNames.get(i), t1)) {
                        counter = i;
                        break;
                    }
                }
                //Воспросизведение выбранного трека
                musicList.getSelectionModel(); // Я не помню зачем это написал
                if (timer != null) {
                    cancelTimer();
                }
                songNumber = counter;
                songProgressBar.setValue(0);
                media = new Media(songs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                songName.setText(songs.get(songNumber).getName());
                beginTimer();
                changeSpeed(null);
                mediaPlayer.play();

            }

        });

        //Поиск файла в списке
        musicSearchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                ArrayList<String> res = textSearch(t1);

                musicList.getItems().clear();
                for (String st : res) {
                    musicList.getItems().add(st);
                }
            }
        });
    }

    //Функция поиска
    private static ArrayList<String> textSearch(String text) {
        ArrayList<String> res = new ArrayList<>();

        for (String s: songsNames){
            if (s.startsWith(text)){
                res.add(s);
            }

        }

        return res;
    }

    //Добавить файл
    public void clickOnButtonAdd(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Music File");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            File saveDir = new File("music");
            File newFile = new File(saveDir,file.getName());
            try {
                Files.copy(file.toPath(),newFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
                songsNames.add(newFile.getName());
                musicList.getItems().add(newFile.getName());
                songs.add(newFile);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //Drag and drop
    public void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
            dragText.setStyle("-fx-text-fill: gray");

        }
    }

    public void handleDragExited(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
        dragText.setStyle("-fx-text-fill: white");
        System.out.println("EXIT");
    }

    public void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            dragText.setStyle("-fx-text-fill: white");
            System.out.println("EXIT");
            success = true;
            for (File file : db.getFiles()) {
                songs.add(file);
                songsNames.add(file.getName());
                musicList.getItems().add(file.getName());

                File saveDir = new File("music");
                File newFile = new File(saveDir,file.getName());
                try {
                    Files.copy(file.toPath(),newFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            mediaPlayer.stop();
            songNumber = songs.size() - 1;
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songName.setText(songs.get(songNumber).getName());

            playMedia();
        }
        event.setDropCompleted(success);
        event.consume();

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


    public void pauseMedia() {

        cancelTimer();
        changeSpeed(null);
        mediaPlayer.pause();
    }

    public void resetMedia() {

        cancelTimer();
        songProgressBar.setValue(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void previousMedia() {

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
        } else {

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
        playMedia();
    }

    public void nextMedia() {

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
        } else {

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

        playMedia();
    }

    public void changeSpeed(ActionEvent event) {

        if (speedBox.getValue() == null) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        }
    }

    public void beginTimer() {

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
            }

            ;
        };

        timer.scheduleAtFixedRate(task, 0, 500);
    }

    public void cancelTimer() {

        running = false;
        timer.cancel();
    }
}