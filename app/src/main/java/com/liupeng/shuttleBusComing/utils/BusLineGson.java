package com.liupeng.shuttleBusComing.utils;

import java.util.List;

/**
 * Created by LiuPeng on 2016/11/12.
 */

public class BusLineGson {


    /**
     * error : {"code":2000,"msg":"字段名称：[roleId] 是必须填写的"}
     */

    private ErrorBean error;
    private List<Integer> data;

    /**
     * {"data": [1,6]}
     */

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
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

}
