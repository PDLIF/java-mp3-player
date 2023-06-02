module com.example.sampleproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens com.example.sampleproject to javafx.fxml;
    exports com.example.sampleproject;
}