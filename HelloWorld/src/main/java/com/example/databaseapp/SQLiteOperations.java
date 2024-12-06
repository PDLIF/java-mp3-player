package com.example.databaseapp;

import java.io.File;
import java.sql.*;

public class SQLiteOperations {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:E:/Education/DB/mydatabase.db"; // Для Windows

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Соединение установлено!");
                System.out.println("Путь к базе данных: " + new     File("mydatabase.db").getAbsolutePath());
                // Добавление данных
                String insertSQL = "INSERT INTO users (name,birthdate,email) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                    pstmt.setString(1, "Дмитрий");
                    pstmt.setString(2, "20221212");
                    pstmt.setString(3, "dmitry@example.com");
                    pstmt.executeUpdate();
                    System.out.println("Данные добавлены.");
                }

                // Выборка данных
                String selectSQL = "SELECT * FROM users";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(selectSQL)) {
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + "\t" +
                                rs.getString("name") + "\t" +
                                rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

}
