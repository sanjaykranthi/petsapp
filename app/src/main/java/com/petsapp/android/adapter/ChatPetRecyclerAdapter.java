package com.petsapp.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.petsapp.android.ChatsPetPagerActivity;
import com.petsapp.android.Model.ItemObject1;
import com.petsapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Android on 04-03-2016.
 */

public class ChatPetRecyclerAdapter extends RecyclerView.Adapter<ChatPetRecyclerAdapter.ViewHolder> {
    private List<ItemObject1> itemList;
    private Context context;

    public ChatPetRecyclerAdapter(Context context, List<ItemObject1> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(itemList.get(position).getPhoto()).into(viewHolder.icons);
        viewHolder.name.setText(itemList.get(position).getName());
        viewHolder.age.setText(itemList.get(position).getage());
        viewHolder.breed.setText(itemList.get(position).getbreed());
        viewHolder.gender.setText(itemList.get(position).getgender());
        viewHolder.img.setText(itemList.get(position).getPhoto());
        String age1 = viewHolder.age.getText().toString();
        if (age1.equalsIgnoreCase("")) {
            viewHolder.yrs.setVisibility(View.GONE);
        } else {
            viewHolder.yrs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_petlist, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, gender, breed, img, yrs;
        ImageView icons;

        public ViewHolder(final View itemView) {
            super(itemView);
            breed = (TextView) itemView.findViewById(R.id.breed);
            icons = (ImageView) itemView.findViewById(R.id.picture);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            img = (TextView) itemView.findViewById(R.id.img);
            gender = (TextView) itemView.findViewById(R.id.gender);
            yrs = (TextView) itemView.findViewById(R.id.first);

            itemView.setOnClickListener(new View.OnClickListener() {
                int pos = getAdapterPosition();

                @Override
                public void onClick(View v) {
                    Intent in = new Intent(v.getContext(), ChatsPetPagerActivity.class);
                    int pos = getAdapterPosition();
                    in.putExtra("pos", pos);
                    v.getContext().startActivity(in);
                }
            });
        }
    }
}