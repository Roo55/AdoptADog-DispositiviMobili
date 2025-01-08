package com.example.adoptadog.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "adoption_forms")
public class AdoptionForm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String comments;

    // Constructor
    public AdoptionForm(String fullName, String email, String phone, String address, String comments) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.comments = comments;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "AdoptionForm{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
