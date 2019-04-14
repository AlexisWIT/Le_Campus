package com.uol.yt120.lecampus.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;

@Entity(tableName = "footprint_table")
public class Footprint {
    // if any variable name changed, change query in DAO as well
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String title;
    private String description;
    private String nodeList;  // String of JSONArray [ArrayList<HashMap<String, Object>>]
    private String createTime;

//    private String creator;       Author username
//    private String length;        in metre
//    private Integer totalTime;    in millisec
//    private Integer privacy;      0-private, 1-open to public, 2-open to friends only
    public Footprint() { }

    @Ignore
    public Footprint(String title, String description, String nodeList, String createTime) {
        this.title = title;
        this.description = description;
        this.nodeList = nodeList;
        this.createTime = createTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getNodeList() {
        return nodeList;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Footprint{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
