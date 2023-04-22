module com.example.helloworld {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.helloworld to javafx.fxml;
    exports com.example.helloworld;
}