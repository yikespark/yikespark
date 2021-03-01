package com.yikes.park.menu.profile.data;

public class UserInformation {

    private String id;
    private String username;
    private String email;
    private String avatar;
    private int followers;

    public UserInformation() {
        // Default constructor required for calls to DataSnapshot.getValue(UserInformation.class)
    }

    public UserInformation(String id, String username, String email, String avatar, int followers) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.followers = followers;
    }

    public String getId() {
        return id;
    }

    public void setId(String username) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }


}
