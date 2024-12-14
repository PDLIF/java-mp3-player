package com.example.databaseapp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileDatabase {
    private String filePath;

    public FileDatabase() {

    }
    public FileDatabase(String filePath) {
        this.filePath = filePath;
    }

    // Чтение всех записей
    public List<Person> readAll() throws IOException {
        List<Person> people = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Проверяем, что строка содержит хотя бы один элемент (идентификатор)
                if (parts.length >= 1) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts.length > 1 ? parts[1] : ""; // Если нет имени, устанавливаем пустую строку
                    String birthdate = parts.length > 2 ? parts[2] : ""; // Если нет даты рождения, устанавливаем пустую строку
                    String email = parts.length > 3 ? parts[3] : ""; // Если нет email, устанавливаем пустую строку
                    people.add(new Person(id, name, birthdate, email));
                } else {
                    System.out.println("Пропускаем пустую строку: " + line);
                }
            }
        }
        return people;
    }

    // Добавление записи
    public void add(Person person) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(person.getId() + "," + person.getName() + "," + person.getBirthdate() + "," + person.getEmail());
            writer.newLine();
        }
    }
    public void deleteById(int id) throws IOException {
        List<Person> people = readAll(); // Читаем все записи
        people.removeIf(person -> person.getId() == id); // Удаляем запись с заданным ID
        overwrite(people); // Перезаписываем файл с обновленным списком
    }
    // Перезапись всех записей (например, после обновления или удаления)
    public void overwrite(List<Person> people) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Person person : people) {
                writer.write(person.getId() + "," + person.getName() + "," + person.getBirthdate() + "," + person.getEmail());
                writer.newLine();
            }
        }
    }
    public List<Person> searchById(int id) throws IOException {
        long startTime = System.currentTimeMillis();

        List<Person> foundPeople = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int personId = Integer.parseInt(parts[0]);
                String name = parts[1];
                String birthdate = parts[2];
                String email = parts[3];

                if (personId == id) {
                    foundPeople.add(new Person(personId, name, birthdate, email));
                    break; // Если ID уникален, можно выйти из цикла
                }
            }
        }

        // блок кода, время выполнения которого нужно измерить
        long endTime = System.currentTimeMillis();
        System.out.println("endTime - startTime: " + (endTime - startTime));

        return foundPeople;
    }
    public List<Person> search(String name, String birthdate, String email) throws IOException {
        List<Person> foundPeople = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String personName = parts[1];
                String personBirthdate = parts[2];
                String personEmail = parts[3];

                // Проверка на совпадение по критериям
                boolean matches = true;
                if (!name.isEmpty() && !personName.toLowerCase().contains(name.toLowerCase())) {
                    matches = false;
                }
                if (!birthdate.isEmpty() && !personBirthdate.equals(birthdate)) {
                    matches = false;
                }
                if (!email.isEmpty() && !personEmail.toLowerCase().contains(email.toLowerCase())) {
                    matches = false;
                }

                if (matches) {
                    foundPeople.add(new Person(id, personName, personBirthdate, personEmail));
                }
            }
        }
        return foundPeople;
    }
    public void clear() throws IOException {
        // Очищаем файл базы данных
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Просто не записываем ничего, тем самым очищаем файл
        }
    }
    public String getFilePath() {
        return filePath;
    }

    public int getMaxId() throws IOException {
        List<Person> people = readAll(); // Предполагается, что этот метод возвращает список всех людей
        return people.stream()
                .mapToInt(Person::getId)
                .max()
                .orElse(0); // Если список пуст, возвращаем 0
    }
}
