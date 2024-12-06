package com.example.databaseapp;

import java.time.LocalDate;

public class User {
    private int id;
    private String name;
    private LocalDate birthdate;
    private String email;

    public User(int id, String name, LocalDate birthdate, String email) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getEmail() {
        return email;
    }
}