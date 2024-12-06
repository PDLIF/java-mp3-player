package com.example.databaseapp;

public class Person {
    private int id;
    private String name;
    private String birthdate;
    private String email;

    public Person(int id, String name, String birthdate, String email) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
        this.email = email;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

