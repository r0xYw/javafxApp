package com.transport.models;

public class Driver {
    private int id;
    private String lastName;
    private String firstName;
    private String middleName;
    private int experience;

    public Driver() {}

    public Driver(int id, String lastName, String firstName,
                  String middleName, int experience) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.experience = experience;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
}