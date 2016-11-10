package com.petsapp.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.petsapp.android.Model.Data;
import com.petsapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by WEB DESIGNING on 20-03-2016.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<Data> itemList;
    private Context context;


    public ChatListAdapter(Context context, List<Data> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(itemList.get(position).getphoto()).into(viewHolder.photo);

        //   viewHolder.name.setText(itemList.get(position).getName());


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView photo;
        Button acceptBtn, declineBtn;

        public ViewHolder(final View itemView) {
            super(itemView);

            //    name = (TextView) itemView.findViewById(R.id.name);
            //   photo = (ImageView) itemView.findViewById(R.id.picture);
            //   acceptBtn = (Button) itemView.findViewById(R.id.accept_id);
            //   declineBtn = (Button) itemView.findViewById(R.id.accept_id);


        }


    }
}
