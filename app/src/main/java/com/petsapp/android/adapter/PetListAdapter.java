package com.petsapp.android.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.petsapp.android.Model.PetListModel;
import com.petsapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by ANDIROID on 06-03-2016.
 */
public class PetListAdapter extends RecyclerView.Adapter<PetListAdapter.ViewHolder> {

    private List<PetListModel> itemList;
    private Context context;
    private Context mContext;


    public PetListAdapter(Context context, List<PetListModel> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Picasso.with(context).load(itemList.get(position).getThumbnailUrl()).into(viewHolder.photo);
        viewHolder.name.setText(itemList.get(position).getName());
        viewHolder.age.setText(itemList.get(position).getAge());
        viewHolder.breed.setText(itemList.get(position).getBreed());
        viewHolder.gender.setText(itemList.get(position).getGender());

        String ageYrsStr = viewHolder.age.getText().toString();
        if (ageYrsStr.equalsIgnoreCase("")) {
            viewHolder.yrs.setVisibility(View.GONE);
        } else {
            viewHolder.yrs.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_petlist_cardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, gender, breed, yrs;
        Spinner breedsp, agesp, gendersp, distancesp;
        ImageView photo;

        public ViewHolder(final View itemView) {
            super(itemView);
            breed = (TextView) itemView.findViewById(R.id.breed);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            gender = (TextView) itemView.findViewById(R.id.gender);
            photo = (ImageView) itemView.findViewById(R.id.picture);
            yrs = (TextView) itemView.findViewById(R.id.first);
            CardView card = (CardView) itemView.findViewById(R.id.card);
            itemView.setOnClickListener(new View.OnClickListener() {
                int pos = getAdapterPosition();

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                   /* Intent in = new Intent(v.getContext(), PetProfile.class);
                    v.getContext().startActivity(in);*/

                }
            });

        }

    }
}
