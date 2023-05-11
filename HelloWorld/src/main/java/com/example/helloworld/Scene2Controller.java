package helloworld;

import javafx.fxml.FXML;

public class Scene2Controller {

    @FXML
    Label nameLabel;

    public void displayName(String username) {
        nameLabel.setText("Hello, " + username);
    }
}