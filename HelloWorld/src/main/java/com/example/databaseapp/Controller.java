package com.example.databaseapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.List;

public class Controller {
    @FXML private TextField nameField;
    @FXML private DatePicker birthdatePicker;
    @FXML private TextField emailField;
    @FXML private TableView<Person> dataTable;
    @FXML private TableColumn<Person, Integer> idColumn;
    @FXML private TableColumn<Person, String> nameColumn;
    @FXML private TableColumn<Person, String> birthdateColumn;
    @FXML private TableColumn<Person, String> emailColumn;

    private final FileDatabase database = new FileDatabase("data.csv");
    private final ObservableList<Person> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        birthdateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBirthdate()));
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        dataTable.setItems(data);
        loadData();
    }

    private void loadData() {
        try {
            List<Person> people = database.readAll();
            data.setAll(people);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddButtonClick(ActionEvent event) {
        try {
            int id = data.size() + 1;
            String name = nameField.getText();
            String birthdate = birthdatePicker.getValue().toString();
            String email = emailField.getText();
            Person newPerson = new Person(id, name, birthdate, email);
            database.add(newPerson);
            data.add(newPerson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUpdateButtonClick(ActionEvent actionEvent) {
    }

    public void onDeleteButtonClick(ActionEvent actionEvent) {
    }

    public void onRefreshButtonClick(ActionEvent actionEvent) {
    }

    // Реализация обновления и удаления аналогична
}
