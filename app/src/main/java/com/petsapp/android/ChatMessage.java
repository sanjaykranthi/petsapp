package com.petsapp.android;

/**
 * Created by Zeta Apponomics 3 on 24-11-2014.
 */
public class ChatMessage {
    public boolean left;
    public String message;
    String msg;
    String msg_on;
    //test end
    String status;
    String msgtype = "jk";
    String Blockstatus;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }

    //test
    public ChatMessage(String message) {
        //  super();
        this.message = message;
    }

    public ChatMessage() {

    }

    public String getBlockstatus() {
        return Blockstatus;
    }

    public void setBlockstatus(String Blockstatus) {
        this.Blockstatus = Blockstatus;
    }


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
