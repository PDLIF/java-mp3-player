package com.example.mp3player;

import eu.hansolo.tilesfx.addons.Switch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.function.Try;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxRobotContext;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.testfx.framework.junit5.Stop;
import org.testfx.service.finder.WindowFinder;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.Objects;

@ExtendWith(ApplicationExtension.class)
class ControllerTest {
    Stage stage;
    Scene scene;
    Controller controller;
    //Desktop desktop = null;
    @Start
    private void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        scene = new Scene(fxmlLoader.load(), Color.AQUA);
        controller = fxmlLoader.getController();

        stage.setTitle("MP3 Player");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });
    }



    //Не работает
    @Test
    void clickOnButtonAdd(FxRobot robot) {
        robot.clickOn("#addButton");
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        robot.push(KeyCode.ENTER);
        robot.push(KeyCode.BACK_SPACE);


        String p = controller.getDirectory().getAbsolutePath();

        p=p.substring(0,p.length()-5);



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
        };
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
        };
        robot.push(KeyCode.ENTER);

        Assertions.assertTrue(controller.getSongs().contains(new File("music/Test Song3.mp3")));

    }

    @Test
    void clickOnButtonDelete() {
    }

    @Test
    void handleDragOver(FxRobot robot) throws IOException {


        //desktop.open(new File("src/test/java/com/example/mp3player"));

        //JFileChooser jFileChooser = new JFileChooser("src/test/java/com/example/mp3player");
        //jFileChooser.
        //System.out.println(jFileChooser.getSelectedFile().getName() );

        //robot.sleep(5000);

        //JFileChooser jFileChooser = new JFileChooser();
        //System.out.println(jFileChooser.getSelectedFile().getName());

    }

    @Test
    void handleDragExited(FxRobot robot) {
    }

    @Test
    void handleDragDropped(FxRobot robot) {
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
    void testClickOnPreviousMediaAfterPlay(FxRobot robot) {
        robot.clickOn("#playButton");
        String previousSongName = robot.lookup("#songName").toString();
        robot.clickOn("#previousButton");
        Assertions.assertNotEquals(previousSongName,robot.lookup("#songName").toString());


    }

    @Test
    void testClickOnNextMediaAfterPlay(FxRobot robot) {
        robot.clickOn("#playButton");
        String previousSongName = robot.lookup("#songName").toString();
        robot.clickOn("#nextButton");
        Assertions.assertNotEquals(previousSongName,robot.lookup("#songName").toString());
    }

    @Test
    void testChangeSpeedTo25Percent(FxRobot robot) {

        robot.clickOn("#speedBox");
        robot.moveTo(robot.offset("#speedBox",0,25));
        robot.clickOn();

        Assertions.assertEquals("25%",robot.lookup("#speedBox").queryComboBox().getValue() );

    }

    @Test
    void testBeginTimer(FxRobot robot) {

    }

    @Test
    void testCancelTimer(FxRobot robot) {
    }


}