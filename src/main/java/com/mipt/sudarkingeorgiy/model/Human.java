package com.mipt.sudarkingeorgiy.model;

public class Human {
    private String firstName;
    private String lastName;
    private int age;
    private boolean isWorking;

    public Human(String firstName, String lastName, int age, boolean isWorking) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.isWorking = isWorking;
    }
}
