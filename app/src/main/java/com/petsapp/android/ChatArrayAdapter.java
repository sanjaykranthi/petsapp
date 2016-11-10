package com.petsapp.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    String messageType = "jk", tmpv = "";
    private TextView chatText, chatText1;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private Activity activity;
    private LayoutInflater inflater;
    // private List<ChatModel> movieItems = new ArrayList<>();;

    public ChatArrayAdapter(Context context, int resource, List<ChatMessage> chatMessageList) {
        super(context, resource, chatMessageList);
        // super(context,  chatMessageList);
        this.context = context;
        this.chatMessageList = chatMessageList;

    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    @Override
    public void add(ChatMessage object) {
        tmpv = object.getMsg();
        // chatMessageList.add(object);
        super.add(object);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        messageType = chatMessageList.get(position).getMsgtype().toString();


        if (messageType.equalsIgnoreCase("From")) {
            row = inflater.inflate(R.layout.left, parent, false);

        } else if (messageType.equalsIgnoreCase("To")) {
            row = inflater.inflate(R.layout.right, parent, false);


        } else if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.right, parent, false);


        } else {


        }
        chatText = (TextView) row.findViewById(R.id.msgr);

        ChatMessage m = chatMessageList.get(position);
        chatText.setText(m.getMsg());
        //if(m.message!=null)
        if (!tmpv.equals("")) {
            chatText.setText(tmpv);
            tmpv = "";
        }

           /* if(value){
                value=false;

                chatText.setText(chatMessageObj.message);
                return row;
            }
                    else
            {
                ChatMessage m = chatMessageList.get(position);
                chatText.setText(m.getMsg());
            }*/

        return row;
    }


}