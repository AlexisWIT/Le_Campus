package com.uol.yt120.lecampus.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String studentNumber;
    private String nickname;
    private String ucasNumber;
    private String realname;
    private String preferedName;
    private String dateofbirth;
    private String uolEmail;

//    private Integer tag_weight_art;
//    private Integer tag_weight_sport;
//    private Integer tag_weight_spiritual;

    public User() { }

    @Ignore
    public User(Integer id, String studentNumber, String nickname, String ucasNumber, String realname, String preferedName, String dateofbirth, String uolEmail) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.nickname = nickname;
        this.ucasNumber = ucasNumber;
        this.realname = realname;
        this.preferedName = preferedName;
        this.dateofbirth = dateofbirth;
        this.uolEmail = uolEmail;
    }

    @Ignore
    public User(Integer id, String studentNumber, String ucasNumber, String realname, String preferedName, String dateofbirth, String uolEmail) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.ucasNumber = ucasNumber;
        this.realname = realname;
        this.preferedName = preferedName;
        this.dateofbirth = dateofbirth;
        this.uolEmail = uolEmail;
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
}
