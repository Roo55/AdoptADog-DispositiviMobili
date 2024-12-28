package com.example.adoptadog.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "dogs")
public class Dog {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("nombre")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("tipo")
    @ColumnInfo(name = "type")
    private String type;

    @SerializedName("color")
    @ColumnInfo(name = "color")
    private String color;

    @SerializedName("edad")
    @ColumnInfo(name = "age")
    private String age;

    @SerializedName("estado")
    @ColumnInfo(name = "status")
    private String status; // E.g., "adopcion"

    @SerializedName("genero")
    @ColumnInfo(name = "gender")
    private String gender; // "macho" or "hembra"

    @SerializedName("desc_fisica")
    @ColumnInfo(name = "physical_description")
    private String physicalDescription;

    @SerializedName("desc_personalidad")
    @ColumnInfo(name = "personality_description")
    private String personalityDescription;

    @SerializedName("imagen")
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @SerializedName("region")
    @ColumnInfo(name = "region")
    private String region;

    @SerializedName("comuna")
    @ColumnInfo(name = "comuna")
    private String comuna;

    @SerializedName("url")
    @ColumnInfo(name = "details_url")
    private String detailsUrl;

    @ColumnInfo(name = "isFavorite")
    private boolean isFavorite;

    // Constructor
    public Dog(int id, String name, String type, String color, String age, String status,
               String gender, String physicalDescription, String personalityDescription,
               String imageUrl, String region, String comuna, String detailsUrl, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
        this.age = age;
        this.status = status;
        this.gender = gender;
        this.physicalDescription = physicalDescription;
        this.personalityDescription = personalityDescription;
        this.imageUrl = imageUrl;
        this.region = region;
        this.comuna = comuna;
        this.detailsUrl = detailsUrl;
        this.isFavorite = isFavorite;
    }

    // Getters and Setters
    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhysicalDescription() {
        return physicalDescription;
    }

    public void setPhysicalDescription(String physicalDescription) {
        this.physicalDescription = physicalDescription;
    }

    public String getPersonalityDescription() {
        return personalityDescription;
    }

    public void setPersonalityDescription(String personalityDescription) {
        this.personalityDescription = personalityDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
