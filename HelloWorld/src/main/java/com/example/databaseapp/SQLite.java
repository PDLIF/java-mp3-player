package com.example.databaseapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLite {
    public static void main(String[] args) {
        // Указываем путь к базе данных
        String url = "jdbc:sqlite:E:/Education/DB/mydatabase.db"; // Для Windows

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Соединение с базой данных установлено!");

                // Пример создания таблицы
                String sql = """
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL,
                            birthdate DATA NOT NULL,
                            email TEXT UNIQUE NOT NULL
                        );
                        """;
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Таблица 'users' создана.");
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка подключения: " + e.getMessage());
        }
    }
}