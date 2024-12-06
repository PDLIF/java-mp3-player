package com.example.databaseapp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileDatabase {
    private final String filePath;

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
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String birthdate = parts[2];
                String email = parts[3];
                people.add(new Person(id, name, birthdate, email));
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

    // Перезапись всех записей (например, после обновления или удаления)
    public void overwrite(List<Person> people) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Person person : people) {
                writer.write(person.getId() + "," + person.getName() + "," + person.getBirthdate() + "," + person.getEmail());
                writer.newLine();
            }
        }
    }
}
