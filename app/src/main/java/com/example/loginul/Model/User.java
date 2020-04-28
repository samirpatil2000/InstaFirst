package com.example.loginul.Model;

public class User {

    String name,email,search,image_profile,bio,uid;

    public  User(){

    }

    public User(String name, String email, String search, String image_profile, String bio, String uid) {
        this.name = name;
        this.email = email;
        this.search = search;
        this.image_profile = image_profile;
        this.bio = bio;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImage_profile() {
        return image_profile;
    }

    public void setImage_profile(String image_profile) {
        this.image_profile = image_profile;
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
