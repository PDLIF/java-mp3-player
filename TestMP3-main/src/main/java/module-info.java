module com.example.mp3player {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;


    opens com.example.mp3player to javafx.fxml;
    exports com.example.mp3player;
}