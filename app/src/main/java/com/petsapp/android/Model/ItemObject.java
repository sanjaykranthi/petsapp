package com.petsapp.android.Model;

/**
 * Created by WEB DESIGNING on 04-03-2016.
 */
public class ItemObject {

    public String name, age, gender, breed, category, weight, height, size, vac, crsbrdreqd, about, pstate, lat, lon, phone;
    public String photo, petmateid, textname;
    public String mThumbnailUrl;
    private String userid;

    public String getuserid() {
        return userid;
    }

    public void setuserid(String userid) {
        this.userid = userid;
    }

    /* public ItemObject(String name, int photo,String age,String gender,String breed) {
         this.name = name;
         this.photo = photo;
         this.age=age;
         this.gender=gender;
         this.breed=breed;

     }*/
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.mThumbnailUrl = thumbnailUrl;
    }

    public String gettextname() {
        return textname;
    }

    public void settextname(String textname) {
        this.textname = textname;
    }

    public String getpetmateid() {
        return petmateid;
    }

    public void setpetmateid(String petmateid) {
        this.petmateid = petmateid;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        this.category = category;
    }

    public String getweight() {
        return weight;
    }

    public void setweight(String weight) {
        this.weight = weight;
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

    public String getcrsbrdreqd() {
        return crsbrdreqd;
    }

    public void setcrsbrdreqd(String crsbrdreqd) {
        this.crsbrdreqd = crsbrdreqd;
    }

    public String getvac() {
        return vac;
    }

    public void setvac(String vac) {
        this.vac = vac;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public String getabout() {
        return about;
    }

    public void setabout(String about) {
        this.about = about;
    }

    public String getpstate() {
        return pstate;
    }

    public void setpstate(String pstate) {
        this.pstate = pstate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getage() {
        return age;
    }

    public void setage(String age) {
        this.age = age;
    }

    public String getgender() {
        return gender;
    }

    public void setgender(String gender) {
        this.gender = gender;
    }

    public String getbreed() {
        return breed;
    }

    public void setbreed(String breed) {
        this.breed = breed;
    }


}
