package com.example.databaseapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Controller {
    public TextField FindId;
    public TextField FindName;
    public DatePicker FindBirthdate;
    public TextField FindEmail;
    public HBox SearchButtons;
    public HBox TableButtons;
    public HBox DataBaseActiveButtons;
    @FXML private TextField nameField;
    @FXML private DatePicker birthdatePicker;
    @FXML private TextField emailField;
    @FXML private TableView<Person> dataTable;
    @FXML private TableColumn<Person, Integer> idColumn;
    @FXML private TableColumn<Person, String> nameColumn;
    @FXML private TableColumn<Person, String> birthdateColumn;
    @FXML private TableColumn<Person, String> emailColumn;

    private FileDatabase database = new FileDatabase();
    private String backupFilePath;
    private final ObservableList<Person> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {


        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        birthdateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBirthdate()));
        emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        dataTable.setItems(data);

        updateUIState(false);
        //loadData();
    }

    private void loadData() {
        try {
            List<Person> people = database.readAll();
            data.setAll(people);
            dataTable.setItems(data); // Обновляем таблицу
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
                int id = generateUniqueId(); // Генерация ID
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
                dataTable.setItems(data); // Обновляем таблицу
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибок при добавлении
            }
        });
    }

    // Метод для генерации уникального ID
    private int generateUniqueId() {
        try {
            return database.getMaxId() + 1; // Генерируем новый ID
        } catch (IOException e) {
            e.printStackTrace();
            return 1; // Если произошла ошибка, возвращаем 1 как начальный ID
        }
    }

    @FXML
    private void onSelectDatabaseClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(dataTable.getScene().getWindow());
        if (selectedFile != null) {
            // Создаем дублирующий файл
            backupFilePath = selectedFile.getAbsolutePath().replace(".csv", "_backup.csv");
            File backupFile = new File(backupFilePath);
            try {
                // Копируем оригинальный файл в дублирующий, а затем грузим данные с основной базы
                Files.copy(selectedFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                database = new FileDatabase(selectedFile.getAbsolutePath());
                updateUIState(true); // Активируем элементы интерфейса
                loadData(); // Загрузить данные
                updateAppTitle();
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибок при копировании файла
            }
        }
    }
    @FXML
    private void onCreateDatabaseClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File newFile = fileChooser.showSaveDialog(dataTable.getScene().getWindow());
        if (newFile != null) {
            try {
                // Создаем пустой файл
                newFile.createNewFile();
                database = new FileDatabase(newFile.getAbsolutePath());
                backupFilePath = newFile.getAbsolutePath().replace(".csv", "_backup.csv"); // Устанавливаем путь к резервной копии
                updateUIState(true); // Активируем элементы интерфейса
                loadData(); // Загружаем данные (пока пустые)
                updateAppTitle();
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибок при создании файла
            }
        }
    }
    @FXML
    private void onResetButtonClick(ActionEvent event) {
        if (backupFilePath != null) {
            File backupFile = new File(backupFilePath);
            File mainDatabaseFile = new File(database.getFilePath());

            try {
                // Копируем содержимое резервного файла в основной файл базы данных
                Files.copy(backupFile.toPath(), mainDatabaseFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                loadData(); // Загружаем данные из основного файла базы данных
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Ошибка при сбросе данных: " + e.getMessage());
            }
        } else {
            showAlert("Резервная копия не найдена. Пожалуйста, создайте или выберите базу данных.");
        }
    }
    @FXML
    public void onClearButtonClick(ActionEvent actionEvent) throws IOException {
        database.clear();
        loadData();
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

//        // Обновляем базу данных
//        try {
//            database.overwrite(data); // Перезаписываем базу данных текущими данными
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Обработка ошибок при обновлении базы данных
//        }
    }
    @FXML
    private void onDeleteButtonClick() {
        // Получаем выбранного человека из интерфейса (например, из таблицы)
        Person selectedPerson = getSelectedPerson(); // Метод для получения выбранного человека
        if (selectedPerson != null) {
            try {
                database.deleteById(selectedPerson.getId()); // Удаляем запись из базы данных
                refreshTable(); // Обновляем таблицу, чтобы отобразить изменения
            } catch (IOException e) {
                showAlert("Ошибка при удалении записи: " + e.getMessage()); // Обработка ошибок
            }
        } else {
            showAlert("Пожалуйста, выберите запись для удаления."); // Сообщение, если ничего не выбрано
        }
    }

    @FXML
    private void onEditButtonClick(ActionEvent event) {
        Person selectedPerson = getSelectedPerson(); // Получаем выбранного человека из таблицы
        if (selectedPerson != null) {
            Dialog<Person> dialog = new Dialog<>();
            dialog.setTitle("Редактировать запись");

            // Создаем панель для ввода данных
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField nameField = new TextField(selectedPerson.getName());
            nameField.setPromptText("Имя");
            DatePicker birthdatePicker = new DatePicker(); // Создаем DatePicker

            try {
                // Пытаемся распарсить дату
                LocalDate date = LocalDate.parse(selectedPerson.getBirthdate());
                birthdatePicker.setValue(date); // Устанавливаем значение в DatePicker, если парсинг успешен
            } catch (DateTimeParseException e) {
                // Если парсинг не удался, можно оставить DatePicker пустым или установить значение по умолчанию
                birthdatePicker.setValue(null); // Устанавливаем значение null, если парсинг не удался
                // Вы можете также вывести сообщение об ошибке, если это необходимо
                System.out.println("Ошибка парсинга даты: " + e.getMessage());
            }


            birthdatePicker.setPromptText("Дата рождения");
            TextField emailField = new TextField(selectedPerson.getEmail());
            emailField.setPromptText("Email");

            grid.add(new Label("Имя:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Дата рождения:"), 0, 1);
            grid.add(birthdatePicker, 1, 1);
            grid.add(new Label("Email"), 0, 2);
            grid.add(emailField, 1, 2);

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setContent(grid);
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Обработка нажатия кнопки OK
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    // Создаем нового человека с обновленными данными
                    int id = selectedPerson.getId(); // Сохраняем ID, чтобы не менять его
                    String name = nameField.getText();
                    String birthdate = birthdatePicker.getValue() != null ? birthdatePicker.getValue().toString() : "";
                    String email  = emailField.getText();
                    return new Person(id, name, birthdate, email);
                }
                return null;
            });

            // Показать диалог и обработать результат
            dialog.showAndWait().ifPresent(updatedPerson -> {
                try {
                    database.deleteById(selectedPerson.getId()); // Удаляем старую запись
                    database.add(updatedPerson); // Добавляем обновленную запись
                    refreshTable(); // Обновляем таблицу
                } catch (IOException e) {
                    e.printStackTrace();
                    // Обработка ошибок при обновлении
                }
            });
        } else {
            showAlert("Пожалуйста, выберите запись для редактирования."); // Сообщение, если ничего не выбрано
        }
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

    private void refreshTable() throws IOException {
        List<Person> people = database.readAll(); // Читаем обновленный список людей
        data.setAll(people); // Обновляем данные в ObservableList
        dataTable.setItems(data); // Обновляем таблицу
    }
    
    private Person getSelectedPerson() {
        return dataTable.getSelectionModel().getSelectedItem(); // Предполагаем, что у вас есть TableView
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateUIState(boolean isDatabaseSelected) {
        // Деактивируем или активируем элементы интерфейса
        DataBaseActiveButtons.setDisable(!isDatabaseSelected);
        TableButtons.setDisable(!isDatabaseSelected);
        dataTable.setDisable(!isDatabaseSelected);
        // Добавьте другие элементы интерфейса, которые нужно активировать/деактивировать
    }

    private Stage primaryStage; // Поле для хранения ссылки на основное окно
    // Метод для установки основного окна
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        updateAppTitle(); // Обновляем заголовок при инициализации
    }

    private void updateAppTitle() {
        if (database != null && database.getFilePath() != null) {
            primaryStage.setTitle("Приложение для работы с БД: " + new File(database.getFilePath()).getName());
        } else {
            primaryStage.setTitle("Приложение для работы с БД: база не подключена");
        }
    }


}
