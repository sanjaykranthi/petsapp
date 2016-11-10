package com.petsapp.android.Model;

/**
 * Created by INDIA on 05-02-2016.
 */
public class Play_Date_Filter_Data {

    String name;
    String age;
    String gender, breed, height, weight, size, lat, lon, phone, textname;
    String invited_on_date;
    String byname;
    String chatIid;
    String status;
    String invited_at_time;
    String type;
    String toid;
    String pager;
    String countlength;
    private String photo;
    private String mThumbnailUrl;
    private String date, categ;
    private String lostorfound;
    private String color;
    private String street;
    private String area;
    private String id;
    private String about;
    private String colorList;
    private String dist;
    private String details;
    private String likes;
    private String userLiked;
    private String pId;

    public Play_Date_Filter_Data(String name, String age, String gender, String breed, String photo, String height, String weight, String size, String lat, String lon) {
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

    public Play_Date_Filter_Data() {

    }

    public String getcountlength() {
        return countlength;
    }

    public void setcountlength(String countlength) {
        this.countlength = countlength;
    }

    public String getPager() {
        return pager;
    }

    public void setPager(String pager) {
        this.pager = pager;
    }

    public String gettoid() {
        return toid;
    }

    public void settoid(String toid) {
        this.toid = toid;
    }

    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getUserLiked() {
        return userLiked;
    }

    public void setUserLiked(String userLiked) {
        this.userLiked = userLiked;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getColorList() {
        return colorList;
    }

    public void setColorList(String colorList) {
        this.colorList = colorList;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String gettextname() {
        return textname;
    }

    public void settextname(String textname) {
        this.textname = textname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLostorfound() {
        return lostorfound;
    }

    public void setLostorfound(String lostorfound) {
        this.lostorfound = lostorfound;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getcateg() {
        return categ;
    }

    public void setcateg(String categ) {
        this.categ = categ;
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
    //

    public String getinvited_on_date() {
        return invited_on_date;
    }

    public void setinvited_on_date(String invited_on_date) {
        this.invited_on_date = invited_on_date;
    }

    public String getbyname() {
        return byname;
    }

    public void setbyname(String byname) {
        this.byname = byname;
    }


    public String getinvited_at_time() {
        return invited_at_time;
    }

    public void setinvited_at_time(String invited_at_time) {
        this.invited_at_time = invited_at_time;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getChatIid() {
        return chatIid;
    }

    public void setChatIid(String chatIid) {
        this.chatIid = chatIid;
    }

}


