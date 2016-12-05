package com.liupeng.shuttleBusComing.utils;

import java.util.List;

/**
 * Created by LiuPeng on 2016/11/12.
 */

public class StationGson {


    /**
     * error : {"code":2000,"msg":"字段名称：[roleId] 是必须填写的"}
     */

    private ErrorBean error;
    private List<DataBean> data;

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class ErrorBean {
        /**
         * code : 2000
         * msg : 字段名称：[roleId] 是必须填写的
         */

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }


    public static class DataBean {
        /**
         * id : 1
         * stationId : 1
         * line : 6
         * stationName : 中山门
         * lat : 39.109597
         * lng : 117.254775
         * updateTime : 2016-12-02 11:13:49
         */

        private int id;
        private int stationId;
        private int line;
        private String stationName;
        private String lat;
        private String lng;
        private String updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStationId() {
            return stationId;
        }

        public void setStationId(int stationId) {
            this.stationId = stationId;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
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

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
