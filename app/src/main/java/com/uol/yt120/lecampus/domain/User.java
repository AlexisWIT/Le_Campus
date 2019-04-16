package com.uol.yt120.lecampus.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer serverId;
    private String password;

    private String studentNumber;
    private String nickname;
    private String ucasNumber;
    private String realname;
    private String preferedName;
    private String dateofbirth;
    private String uolEmail;
    private String friendList; // JSONArray: [{"serverId", 15},{"serverId", 17}]

//    private Integer tag_weight_art;
//    private Integer tag_weight_sport;
//    private Integer tag_weight_spiritual;

    public User() { }

    @Ignore
    public User(Integer serverId, String password, String studentNumber, String nickname, String ucasNumber, String realname, String preferedName, String dateofbirth, String uolEmail, String friendList) {
        //this.id = id;
        this.serverId = serverId;
        this.password = password;
        this.studentNumber = studentNumber;
        this.nickname = nickname;
        this.ucasNumber = ucasNumber;
        this.realname = realname;
        this.preferedName = preferedName;
        this.dateofbirth = dateofbirth;
        this.uolEmail = uolEmail;
        this.friendList = friendList;
    }

    @Ignore
    public User(String studentNumber, String ucasNumber, String realname, String preferedName, String dateofbirth, String uolEmail) {
        //this.id = id;
        this.studentNumber = studentNumber;
        this.ucasNumber = ucasNumber;
        this.realname = realname;
        this.preferedName = preferedName;
        this.dateofbirth = dateofbirth;
        this.uolEmail = uolEmail;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUcasNumber() {
        return ucasNumber;
    }

    public void setUcasNumber(String ucasNumber) {
        this.ucasNumber = ucasNumber;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPreferedName() {
        return preferedName;
    }

    public void setPreferedName(String preferedName) {
        this.preferedName = preferedName;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getUolEmail() {
        return uolEmail;
    }

    public void setUolEmail(String uolEmail) {
        this.uolEmail = uolEmail;
    }

    public String getFriendList() {
        return friendList;
    }

    public void setFriendList(String friendList) {
        this.friendList = friendList;
    }
}
