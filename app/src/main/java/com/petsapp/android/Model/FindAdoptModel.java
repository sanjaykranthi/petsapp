package com.petsapp.android.Model;

/**
 * Created by WEB DESIGNING on 20-03-2016.
 */
public class FindAdoptModel {

    String name;
    String age;
    String gender, breed, height, weight, size, lat, lon, phone;
    private String photo;

    private String mThumbnailUrl;

    public FindAdoptModel(String name, String age, String gender, String breed, String photo, String height, String weight, String size, String lat, String lon) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.breed = breed;
        this.photo = photo;
        this.height = height;
        this.weight = weight;
        this.size = size;
        this.lat = lat;
        this.lon = lon;
        this.phone = phone;

    }

    public FindAdoptModel() {

    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.mThumbnailUrl = thumbnailUrl;
    }

    public String getweight() {
        return weight;
    }

    public void setweight(String weight) {
        this.weight = weight;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public String getheight() {
        return height;
    }

    public void setheight(String height) {
        this.height = height;
    }

    public String getsize() {
        return size;
    }

    public void setsize(String size) {
        this.size = size;
    }

    public String getlat() {
        return lat;
    }

    public void setlat(String lat) {
        this.lat = lat;
    }

    public String getlon() {
        return lon;
    }

    public void setlon(String lon) {
        this.lon = lon;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getphoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
