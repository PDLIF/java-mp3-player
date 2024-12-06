package com.example.databaseapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;


import java.sql.*;
import java.time.LocalDate;

public class Controller {
    @FXML
    private TableView<User> dataTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, LocalDate> birthdateColumn;
    @FXML
    private TableColumn<User, String> emailColumn;

    private final String databaseUrl = "jdbc:sqlite:E:/Education/DB/mydatabase.db";

    // Метод для инициализации колонок таблицы
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        birthdateColumn.setCellValueFactory(new PropertyValueFactory<>("birthdate"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        loadTableData(); // Загрузка данных в таблицу при инициализации
    }

    // Загрузка данных из базы в таблицу
    public void loadTableData() {
        ObservableList<User> userList = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(databaseUrl);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDate("birthdate").toLocalDate(),
                        rs.getString("email")
                ));
            }
            dataTable.setItems(userList);
            System.out.println("Вроде загрузились");

        } catch (Exception e) {
            System.out.println("Ошибка загрузки данных: " + e.getMessage());
        }
    }

    // Добавьте следующие поля для ввода данных
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker birthdatePicker;
    @FXML
    private TextField emailField;

    public void onAddButtonClick(ActionEvent actionEvent) {
        String name = nameField.getText();
        LocalDate birthdate = birthdatePicker.getValue();
        String email = emailField.getText();

        if (name.isEmpty() || birthdate == null || email.isEmpty()) {
            System.out.println("Все поля должны быть заполнены!");
            return;
        }

        String insertSQL = "INSERT INTO users (name, birthdate, email) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, name);
            pstmt.setDate(2, java.sql.Date.valueOf(birthdate));
            pstmt.setString(3, email);

            pstmt.executeUpdate();
            System.out.println("Запись успешно добавлена!");
            loadTableData(); // Обновляем таблицу после добавления

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении записи: " + e.getMessage());
        }
    }

    public void onUpdateButtonClick(ActionEvent actionEvent) {
    }

    public void onDeleteButtonClick(ActionEvent actionEvent) {
    }

    public void onRefreshButtonClick(ActionEvent actionEvent) {
    }
}
