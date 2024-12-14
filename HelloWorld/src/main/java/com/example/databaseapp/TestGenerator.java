package com.example.databaseapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TestGenerator {
    public static void main(String[] args) {
        String filePath = "E:/Education/DB/generated_data.csv"; // Путь к файлу, который будет создан
        int numberOfRecords = 1000; // Количество записей для генерации
        generateData(filePath, numberOfRecords);
    }

    private static void generateData(String filePath, int numberOfRecords) {
        Random random = new Random();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[] names = {"Иванов Иван", "Петров Петр", "Сидоров Сидор", "Николаев Николай", "Васильев Василий", "Смирнов Сергей", "Кузнецов Алексей", "Попов Андрей", "Соловьев Артем", "Морозов Дмитрий"};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 1; i <= numberOfRecords; i++) {
                String name = names[random.nextInt(names.length)];
                LocalDate date = LocalDate.now().minusDays(random.nextInt(365)); // Генерируем случайную дату за последний год
                String email = generateEmail(name);
                writer.write(i + "," + name + "," + date.format(dateFormatter) + "," + email);
                writer.newLine();
            }
            System.out.println("Данные успешно сгенерированы в " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateEmail(String name) {
        String[] domains = {"example.com", "mail.ru", "gmail.com", "yahoo.com", "hotmail.com"};
        String[] nameParts = name.split(" ");
        String email = nameParts[0].toLowerCase() + "." + nameParts[1].toLowerCase() + "@" + domains[new Random().nextInt(domains.length)];
        return email;
    }
}
