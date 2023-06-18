package com.example.mp3player;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.awt.*;
import java.awt.im.InputContext;
import java.io.File;
import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
class ControllerTest {
    Stage stage;
    Scene scene;
    Controller controller;
    /*
        Пояснения по тестам - Дмитрий Макаров:
        1) Для тестирование нужно использовать конфигурацию с такими настройками(файл есть в проекте):
        --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
        Подобрано через гугл, без данной настройки по неустановленной причине даже пустые юнит тесты не работали
        2) Для тестирования установлен модуль testfx, чтобы эмулировать действия пользователя
        3) Этот модуль к сожалению, не распологает функционалом для работы вне приложения, так что drag and drop,
        а также выбор файла в тесты включить не удалось (попытки предпринимались).
        4) Ввиду особенностей функционала testfx, в методе start ниже дублируется код из application, что по логике
        обеспечивает покрытие данного класса.
        5) Тесты не покрывают геттеры, которые были созданы для теста ввиду приватности значений класса controller


     */

    @Start
    private void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        scene = new Scene(fxmlLoader.load(), Color.AQUA);
        controller = fxmlLoader.getController();

        stage.setTitle("MP3 Player");
        stage.setScene(scene);
        stage.show();
        stage.setAlwaysOnTop(true);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @AfterEach
    void setScene(){
        controller.getMediaPlayer().stop();
    }

    @Test
    void testClickOnButtonPlay(FxRobot robot) {

        robot.clickOn("#playButton");
        Assertions.assertEquals("PLAYING", controller.getMediaPlayer().getStatus().toString());



    }

    @Test
    void testClickOnPauseMediaAfterPlay(FxRobot robot) {
        robot.clickOn("#playButton");
        robot.clickOn("#pauseButton");
        Assertions.assertEquals("PAUSED", controller.getMediaPlayer().getStatus().toString());
    }

    @Test
    void testClickOnResetMediaAfterPlay(FxRobot robot) {
        robot.clickOn("#playButton");
        robot.clickOn("#resetButton");
        //Меньше секунды (погрешность)
        Assertions.assertTrue( controller.getMediaPlayer().getCurrentTime().lessThanOrEqualTo(Duration.seconds(1)) );
        // Слайдер на 0
        Assertions.assertEquals(0, robot.lookup("#songProgressBar").queryAs(Slider.class).getValue() );
    }

    @Test
    void testClickOnPreviousMediaAfterPlayCorner(FxRobot robot) {
        robot.clickOn("#playButton");
        String previousSongName = robot.lookup("#songName").toString();
        robot.clickOn("#previousButton");
        Assertions.assertNotEquals(previousSongName,robot.lookup("#songName").toString());


    }
    @Test
    void testClickOnPreviousMediaAfter(FxRobot robot) {
        robot.clickOn("#playButton");
        robot.clickOn("#nextButton");
        String previousSongName = robot.lookup("#songName").toString();
        robot.clickOn("#previousButton");
        Assertions.assertNotEquals(previousSongName,robot.lookup("#songName").toString());


    }

    @Test
    void testClickOnNextMediaAfterPlayCorner(FxRobot robot) {
        robot.clickOn("#playButton");
        String previousSongName = robot.lookup("#songName").toString();
        robot.clickOn("#nextButton");
        Assertions.assertNotEquals(previousSongName,robot.lookup("#songName").toString());
    }
    @Test
    void testClickOnNextMediaAfterPlay(FxRobot robot) {
        robot.clickOn("#playButton");
        String previousSongName = robot.lookup("#songName").toString();
        for ( File ignored :controller.getSongs() )
        {
            robot.clickOn("#nextButton");
        }
        robot.clickOn("#nextButton");
        Assertions.assertNotEquals(previousSongName,robot.lookup("#songName").toString());
    }

    @Test
    void testChangeSpeedTo25Percent(FxRobot robot) {
        robot.clickOn("#playButton");

        robot.clickOn("#speedBox");
        robot.clickOn(robot.lookup("#speedBox").queryComboBox().getItems().get(0).toString());

        robot.sleep(1000);
        //Гуи переключилось
        Assertions.assertEquals("25%",robot.lookup("#speedBox").queryComboBox().getValue() );

    }

    @Test
    void testBeginTimer(FxRobot robot) {
        controller.beginTimer();
        double current = controller.getMediaPlayer().getCurrentTime().toSeconds();
        double end = controller.getMedia().getDuration().toSeconds();
        double progress = (current / end);
        //Статус работает
        robot.sleep(500);
        Assertions.assertTrue(controller.isRunning());
        //Провека слайдера
        Assertions.assertEquals(robot.lookup("#songProgressBar").queryAs(Slider.class).getValue(),progress);
    }
    @Test
    void testBeginTimerCaseEnding(FxRobot robot) {
        controller.beginTimer();
        controller.getMediaPlayer().seek(controller.getMedia().getDuration().multiply(100));
        robot.sleep(600);
        //Статус не работает
        Assertions.assertFalse(controller.isRunning());

    }

    @Test
    void testCancelTimer(FxRobot robot) {
        controller.beginTimer();
        controller.cancelTimer();
        robot.sleep(1000);
        //Статус не работает
        Assertions.assertFalse(controller.isRunning());
    }

    @Test
    void testChangeVolume(FxRobot robot){
        double volume1 = controller.getMediaPlayer().getVolume();
        robot.moveTo("#volumeSlider");
        robot.drag();
        robot.moveBy(25,0);
        robot.drag();
        //Громкость поменялась
        Assertions.assertNotEquals(volume1,controller.getMediaPlayer().getVolume());

    }
    @Test
    void testFindMusic(FxRobot robot){
        robot.clickOn("#musicSearchField");
        robot.write("Test Song2.mp3");
        //Должен остаться 1 элемент
        Assertions.assertEquals(1,robot.lookup("#musicList").queryListView().getItems().size());
    }
    @Test
    void testChangePositionAfterPlay(FxRobot robot){

        robot.moveTo("#songProgressBar");
        robot.clickOn();
        double position = robot.lookup("#songProgressBar").queryAs(Slider.class).getValue();

        //Гуи переключилось
        Assertions.assertTrue(position>48);
        //Само медиа переключилось
        Assertions.assertTrue(controller.getMediaPlayer().getCurrentTime().greaterThan(controller.getMedia().getDuration().multiply(0.4))) ;
    }



    @Test
    void testClickOnButtonDelete(FxRobot robot) {
        robot.moveTo("#musicList");
        robot.clickOn();
        robot.clickOn("#deleteButton");
        robot.sleep(100);
        Assertions.assertFalse(controller.getSongs().contains(new File ("test song 2.mp3")));
    }

    //Отключенныне тесты, в которых была попытка хотя бы захардкорить действия пользователя
    //Ввиду частых ошибок отключены
    @Disabled
    @Test
    void testClickOnButtonAdd(FxRobot robot) {
        robot.clickOn("#addButton");
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.ENTER);
        robot.push(KeyCode.BACK_SPACE);


        String p = controller.getDirectory().getAbsolutePath();

        p=p.substring(0,p.length()-5);

        //Смена раскладки
        if ( !InputContext.getInstance().getLocale().toString().equals("en_US")){
            robot.push(KeyCode.SHIFT,KeyCode.ALT);
        }
        // Костыль так как write в либе походу сломан

        for (String s: (p+"src/test/TestMusic").toUpperCase().split(""))
        {   //System.out.println(s);
            switch (s) {

                case "\\", "/" -> robot.push(KeyCode.SLASH);
                case "." -> robot.push(KeyCode.PERIOD);
                case " " -> robot.push(KeyCode.SPACE);
                case ":" -> robot.push(KeyCode.SHIFT,KeyCode.SEMICOLON);
                case "-" -> robot.push(KeyCode.MINUS);
                default -> {
                    KeyCode k = KeyCode.getKeyCode(s);
                    robot.push(k);
                }
            }
        }
        robot.push(KeyCode.ENTER);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        for (String s: "Test Song3.mp3".toUpperCase().split(""))
        {
            switch (s) {

                case "\\", "/" -> robot.push(KeyCode.SLASH);
                case "." -> robot.push(KeyCode.PERIOD);
                case " " -> robot.push(KeyCode.SPACE);
                case ":" -> robot.push(KeyCode.SHIFT,KeyCode.SEMICOLON);
                case "-" -> robot.push(KeyCode.MINUS);
                default -> {
                    KeyCode k = KeyCode.getKeyCode(s);
                    robot.push(k);
                }
            }
        }
        robot.push(KeyCode.ENTER);
        robot.sleep(100);
        Assertions.assertTrue(controller.getSongs().contains(new File("music/Test Song3.mp3")));

    }


    @Disabled
    @Test
    void testHandleDragOver(FxRobot robot) throws IOException {
        File file = new File("src/test/TestMusic");
        Desktop.getDesktop().open(file);

        //Очень ненадежный хардкодный код
        robot.moveTo(stage.getX(),stage.getY());
        robot.moveBy(-100,300);
        robot.drag();
        robot.moveTo(stage.getX()+25,stage.getY()+25);

    }
    @Disabled
    @Test
    void testHandleDragExited(FxRobot robot) throws IOException {
        Desktop.getDesktop().open(new File("src/test/TestMusic"));

        //Очень ненадежный хардкодный код
        robot.moveTo(stage.getX(),stage.getY());
        robot.moveBy(-100,300);
        robot.drag();
        robot.moveTo(stage.getX()+25,stage.getY()+25);

        System.out.println(robot.lookup("#pane").queryText());

    }
    @Disabled
    @Test
    void testHandleDragDropped(FxRobot robot) throws IOException {
        Desktop.getDesktop().open(new File("src/test/TestMusic"));

        //Очень ненадежный хардкодный код
        robot.moveTo(stage.getX(),stage.getY());
        robot.moveBy(-100,300);
        robot.drag();
        robot.moveTo(stage.getX()+25,stage.getY()+25);

        robot.sleep(600);
        Assertions.assertTrue(controller.getSongs().contains(new File("music/Test Song3.mp3")));
    }

}