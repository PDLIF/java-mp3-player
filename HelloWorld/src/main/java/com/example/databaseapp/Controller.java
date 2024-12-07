package com.example.databaseapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Controller {
    public TextField FindId;
    public TextField FindName;
    public DatePicker FindBirthdate;
    public TextField FindEmail;
    @FXML private TextField nameField;
    @FXML private DatePicker birthdatePicker;
    @FXML private TextField emailField;
    @FXML private TableView<Person> dataTable;
    @FXML private TableColumn<Person, Integer> idColumn;
    @FXML private TableColumn<Person, String> nameColumn;
    @FXML private TableColumn<Person, String> birthdateColumn;
    @FXML private TableColumn<Person, String> emailColumn;

    private FileDatabase database = new FileDatabase();
    private final ObservableList<Person> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        birthdateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBirthdate()));
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        dataTable.setItems(data);
        //loadData();
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
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Добавить нового человека");

        // Создаем панель для ввода данных
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Имя");
        DatePicker birthdatePicker = new DatePicker();
        birthdatePicker.setPromptText("Дата рождения");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        grid.add(new Label("Имя:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Дата рождения:"), 0, 1);
        grid.add(birthdatePicker, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Обработка нажатия кнопки OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                int id = data.size() + 1; // Генерация ID
                String name = nameField.getText();
                String birthdate = birthdatePicker.getValue() != null ? birthdatePicker.getValue().toString() : "";
                String email = emailField.getText();
                return new Person(id, name, birthdate, email);
            }
            return null;
        });

        // Показать диалог и обработать результат
        dialog.showAndWait().ifPresent(person -> {
            try {
                database.add(person); // Добавление в базу данных
                data.add(person); // Добавление в таблицу
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибок при добавлении
            }
        });
    }
    @FXML
    private void onSelectDatabaseClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(dataTable.getScene().getWindow());
        if (selectedFile != null) {
            // Создаем дублирующий файл
            File backupFile = new File(selectedFile.getAbsolutePath().replace(".csv", "_backup.csv"));
            try {
                // Копируем оригинальный файл в дублирующий
                Files.copy(selectedFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                database = new FileDatabase(backupFile.getAbsolutePath());
                loadData(); // Загрузить данные из дублирующего файла
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибок при копировании файла
            }
        }
    }

    @FXML
    private void onResetButtonClick(ActionEvent event) {
        // Загрузить данные из оригинального файла
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(dataTable.getScene().getWindow());
        if (selectedFile != null) {
            database = new FileDatabase(selectedFile .getAbsolutePath());
            loadData(); // Загрузить данные из оригинального файла
        }
    }

    @FXML
    private void onUpdateButtonClick(ActionEvent actionEvent) {
        // Создаем резервную копию, если она уже существует
        File backupFile = new File(database.getFilePath().replace(".csv", "_backup.csv"));
        if (backupFile.exists()) {
            backupFile.delete(); // Удаляем старую резервную копию
        }

        // Создаем новую резервную копию
        try {
            Files.copy(new File(database.getFilePath()).toPath(), backupFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибок при создании резервной копии
        }

        // Обновляем базу данных
        try {
            database.overwrite(data); // Перезаписываем базу данных текущими данными
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибок при обновлении базы данных
        }
    }
    public void onDeleteButtonClick(ActionEvent actionEvent) {
    }

    public void onRefreshButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    private void onFindButtonClick(ActionEvent event) {
        String name = FindName.getText();
        String birthdate = FindBirthdate.getValue() != null ? FindBirthdate.getValue().toString() : "";
        String email = FindEmail.getText();
        String idText = FindId.getText(); // Получаем ID из текстового поля

        try {
            List<Person> foundPeople;

            if (!idText.isEmpty()) {
                // Если ID указан, ищем только по ID
                int id = Integer.parseInt(idText);
                foundPeople = database.searchById(id); // Метод поиска по ID
            } else {
                // Если ID не указан, ищем по другим параметрам
                foundPeople = database.search(name, birthdate, email);
            }

            data.setAll(foundPeople); // Обновляем таблицу с найденными записями
            if (foundPeople.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Поиск завершен");
                alert.setHeaderText(null);
                alert.setContentText("Записи не найдены.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, введите корректный ID.");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка поиска");
            alert.setHeaderText(null);
            alert.setContentText("Произошла ошибка при поиске данных.");
            alert.showAndWait();
        }
    }

    // Реализация обновления и удаления аналогична
}
