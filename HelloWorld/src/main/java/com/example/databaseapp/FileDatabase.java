package com.example.databaseapp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FileDatabase {
    private String filePath;
    private Map<Integer, Person> peopleMap; // Хранение данных в HashMap

    public FileDatabase( ) throws IOException {
    }

    public FileDatabase(String filePath) throws IOException {
        this.filePath = filePath;
        this.peopleMap = new HashMap<>();
        loadData(); // Загружаем данные при инициализации
    }

    // Метод для загрузки данных из файла в HashMap
    private void loadData() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts.length > 1 ? parts[1] : "";
                    String birthdate = parts.length > 2 ? parts[2] : "";
                    String email = parts.length > 3 ? parts[3] : "";
                    peopleMap.put(id, new Person(id, name, birthdate, email)); // Добавляем в HashMap
                }
            }
        }
    }

    // Метод для чтения всех записей
    public List<Person> readAll() {
        return new ArrayList<>(peopleMap.values()); // Возвращаем список всех людей
    }
    // Добавление записи
    public void add(Person person) throws IOException {
        long startTime = System.nanoTime(); // Начало замера времени
        peopleMap.put(person.getId(), person); // Добавляем или обновляем запись
        saveData(); // Сохраняем данные в файл
        long endTime = System.nanoTime(); // Конец замера времени
        long duration = endTime - startTime;
        System.out.println("Время добавления записи:  " + duration + " нс (" + (duration / 1_000_000.0) + " с)");
    }

    // Удаление записи
    public void deleteById(int id) throws IOException {
        long startTime = System.nanoTime(); // Начало замера времени
        peopleMap.remove(id); // Удаляем запись по ID
        saveData(); // Сохраняем данные в файл
        long endTime = System.nanoTime(); // Конец замера времени
        long duration = endTime - startTime;
        System.out.println("Время удаления записи: " + duration + " нс (" + (duration / 1_000_000.0) + " с)");
    }

    // Сохранение данных из HashMap в файл
    private void saveData() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Person person : peopleMap.values()) {
                writer.write(person.getId() + "," + person.getName() + "," + person.getBirthdate() + "," + person.getEmail());
                writer.newLine();
            }
        }
    }

    // Поиск по ID
    public Person searchById(int id) {
        long startTime = System.nanoTime(); // Начало замера времени
        Person person = peopleMap.get(id); // Поиск по ID за O(1)
        long endTime = System.nanoTime(); // Конец замера времени
        long duration = endTime - startTime;
        System.out.println("Время поиска по ID: " + duration + " нс (" + (duration / 1_000_000.0) + " с)");
        return person;
    }

    // Поиск по другим параметрам
    public List<Person> search(String name, String birthdate, String email) {
        long startTime = System.nanoTime(); // Начало замера времени
        List<Person> result = new ArrayList<>();
        for (Person person : peopleMap.values()) {
            if ((name.isEmpty() || person.getName().toLowerCase().contains(name.toLowerCase())) &&
                    (birthdate.isEmpty() || person.getBirthdate().equals(birthdate)) &&
                    (email.isEmpty() || person.getEmail().toLowerCase().contains(email.toLowerCase()))) {
                result.add(person);
            }
        }
        long endTime = System.nanoTime(); // Конец замера времени
        long duration = endTime - startTime;
        System.out.println("Время поиска по параметрам: " + duration + " нс (" + (duration / 1_000_000.0) + " с)");
        return result;
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
