package com.liupeng.shuttleBusComing.bean;

import java.sql.Time;

/**
 * Created by liupeng on 2016/12/7.
 * E-mail: liupeng826@hotmail.com
 */

public class Station {
    /**
     * id : 6
     * uuid : 03002B56-8578-4B70-9582-E6BB5B6B7378
     * lat : 39.1499449327257
     * lng : 117.40011311849
     * createdTime : 2016-11-01 13:06:45
     * updateTime : 2016-11-25 13:05:13
     */

    private long id;
    private Integer stationId;
    private Integer line;
    private String stationName;
    private String lat;
    private String lng;
    private Time reachTime;
    private String busNo;
    private String driverName;
    private String driverTel;
    private String comments;
    private String createdTime;
    private String updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public Integer getline() {
        return line;
    }

    public void setline(Integer line) {
        this.line = line;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Time getReachTime() {
        return reachTime;
    }

    public void setReachTime(Time reachTime) {
        this.reachTime = reachTime;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverTel() {
        return driverTel;
    }

    public void setDriverTel(String driverTel) {
        this.driverTel = driverTel;
    }

    public String getCreatedTime() {

        return createdTime;
    }

    public void setCreatedTime(String createdTime) {

        this.createdTime = createdTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {

        this.updateTime = updateTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
