package com.benbaba.module.device.bean;

/**
 * 鼓返回得数据
 * Created by Administrator on 2018/1/23.
 */
public class DrumBean {

    private HeaderBean header;
    private BodyBean body;

    public HeaderBean getHeader() {
        return header;
    }

    public BodyBean getBody() {
        return body;
    }

    public static class HeaderBean {
        /**
         * app_id : aaaaa
         * action : rhythmPrompting
         * dev_id : 0000
         */

        private String app_id;
        private String action;
        private String dev_id;


        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDev_id() {
            return dev_id;
        }

        public void setDev_id(String dev_id) {
            this.dev_id = dev_id;
        }


    }

    public static class BodyBean {
        private String result_code;
        private String tone;
        private String hand;
        private String volume;//
        private String status;//标识得是玩具鼓状态

        public String getHand() {
            return hand;
        }

        public void setHand(String hand) {
            this.hand = hand;
        }

        public String getResult_code() {
            return result_code;
        }

        public void setResult_code(String result_code) {
            this.result_code = result_code;
        }

        public String getTone() {
            return tone;
        }

        public void setTone(String tone) {
            this.tone = tone;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
