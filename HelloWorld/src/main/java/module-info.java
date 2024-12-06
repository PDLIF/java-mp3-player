module com.example.helloworld {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports com.example.databaseapp;
    opens com.example.databaseapp to javafx.fxml;
}