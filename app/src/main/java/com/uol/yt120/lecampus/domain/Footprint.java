package com.uol.yt120.lecampus.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "footprint_table")
public class Footprint {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private ArrayList nodeList;
    private String createTime;

    public Footprint(String title, String description, ArrayList nodeList, String createTime) {
        this.title = title;
        this.description = description;
        this.nodeList = nodeList;
        this.createTime = createTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList getNodeList() {
        return nodeList;
    }

    public String getCreateTime() {
        return createTime;
    }
}
