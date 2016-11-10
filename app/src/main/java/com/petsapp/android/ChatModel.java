package com.petsapp.android;

/**
 * Created by WEB DESIGNING on 13-06-2016.
 */
public class ChatModel {

    String msg;
    String msg_on;
    String status;
    String msgtype;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg_on() {
        return msg_on;
    }

    public void setMsg_on(String msg_on) {
        this.msg_on = msg_on;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
