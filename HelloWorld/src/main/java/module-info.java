module com.example.helloworld {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.ooxml;


    exports com.example.databaseapp;
    opens com.example.databaseapp to javafx.fxml;
}