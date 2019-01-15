package com.benbaba.dadpat.host.bean.drum;

/**
 * socket接收的对象
 * Created by Administrator on 2018/5/7.
 */
public class DrumBean {
    private Header header;
    private Body body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public class Header {
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

    public class Body {
        private String result_code;

        public String getResult_code() {
            return result_code;
        }

        public void setResult_code(String result_code) {
            this.result_code = result_code;
        }
    }
}
