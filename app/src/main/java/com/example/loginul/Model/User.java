package com.example.loginul.Model;

public class User {

    String FullName,email,search,profile_image,bio,uid;

    public  User(){

    }

    public User(String name, String email, String search, String profile_image, String bio, String uid) {
        this.FullName = name;
        this.email = email;
        this.search = search;
        this.profile_image = profile_image;
        this.bio = bio;
        this.uid = uid;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        this.FullName = fullName;
    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String image_profile) {
        this.profile_image = image_profile;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
