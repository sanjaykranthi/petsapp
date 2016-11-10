package com.petsapp.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.petsapp.android.Model.PetBookData;
import com.petsapp.android.PetbookPagerActivity;
import com.petsapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by WEB DESIGNING on 20-05-2016.
 */
public class PetBookRecyclerAdapter extends RecyclerView.Adapter<PetBookRecyclerAdapter.ViewHolder> {

    private List<PetBookData> itemList;
    private Context context;

    public PetBookRecyclerAdapter(Context context, List<PetBookData> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(itemList.get(position).getphoto()).into(viewHolder.photo);
        Picasso.with(context).load(itemList.get(position).getphoto()).into(viewHolder.photo);
        viewHolder.name.setText(itemList.get(position).getName());
        viewHolder.details.setText(itemList.get(position).getDetails());
        viewHolder.id.setText(itemList.get(position).getId());
        viewHolder.imgText.setText(itemList.get(position).getphoto());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.petbook_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, details, id, imgText;

        ImageView photo;

        public ViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.pet_name);
            details = (TextView) itemView.findViewById(R.id.pet_details);
            id = (TextView) itemView.findViewById(R.id.pet_id);
            imgText = (TextView) itemView.findViewById(R.id.pet_img_text);
            photo = (ImageView) itemView.findViewById(R.id.pet_img);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent in = new Intent(v.getContext(), PetbookPagerActivity.class);
                    int pos = getAdapterPosition();
                    in.putExtra("pos", pos);
                    v.getContext().startActivity(in);

                }
            });

        }

    }

}
