package com.petsapp.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.petsapp.android.AdoptionPagerActivity;
import com.petsapp.android.Model.Data;
import com.petsapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by WEB DESIGNING on 20-03-2016.
 */
public class FindAdoptAdapter extends RecyclerView.Adapter<FindAdoptAdapter.ViewHolder> {

    private List<Data> itemList;
    private Context context;


    public FindAdoptAdapter(Context context, List<Data> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Picasso.with(context).load(itemList.get(position).getphoto()).into(viewHolder.photo);
        viewHolder.name.setText(itemList.get(position).getName());
        viewHolder.age.setText(itemList.get(position).getAge());
        viewHolder.breed.setText(itemList.get(position).getBreed());
        viewHolder.gender.setText(itemList.get(position).getGender());
        viewHolder.weigh.setText(itemList.get(position).getweight());
        viewHolder.heigh.setText(itemList.get(position).getheight());
        viewHolder.size.setText(itemList.get(position).getsize());
        viewHolder.imag.setText(itemList.get(position).getphoto());
        viewHolder.latt.setText(itemList.get(position).getlat());
        viewHolder.lonn.setText(itemList.get(position).getlon());
        viewHolder.phone.setText(itemList.get(position).getphone());
        viewHolder.date.setText(itemList.get(position).getDate());
        viewHolder.categ.setText(itemList.get(position).getcateg());
        viewHolder.dist.setText(itemList.get(position).getDist());
        viewHolder.pager.setText(itemList.get(position).getPager());

        String age1 = viewHolder.age.getText().toString();
        if (age1.equalsIgnoreCase("")) {
            viewHolder.yrs.setVisibility(View.GONE);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, gender, breed, weigh, heigh, size, imag, latt, lonn, phone, date, categ, dist, yrs, pager;
        Spinner breedsp, agesp, gendersp, distancesp;
        ImageView photo;

        public ViewHolder(final View itemView) {
            super(itemView);
            breed = (TextView) itemView.findViewById(R.id.breed);
            categ = (TextView) itemView.findViewById(R.id.categ);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            weigh = (TextView) itemView.findViewById(R.id.weig);
            heigh = (TextView) itemView.findViewById(R.id.heigh);
            size = (TextView) itemView.findViewById(R.id.size);
            imag = (TextView) itemView.findViewById(R.id.imag);
            latt = (TextView) itemView.findViewById(R.id.latt);
            lonn = (TextView) itemView.findViewById(R.id.lonn);
            gender = (TextView) itemView.findViewById(R.id.gender);
            photo = (ImageView) itemView.findViewById(R.id.picture);
            phone = (TextView) itemView.findViewById(R.id.phone);
            date = (TextView) itemView.findViewById(R.id.dateId);
            dist = (TextView) itemView.findViewById(R.id.dist);
            yrs = (TextView) itemView.findViewById(R.id.first);
            pager = (TextView) itemView.findViewById(R.id.pager);
            CardView card = (CardView) itemView.findViewById(R.id.card);
            itemView.setOnClickListener(new View.OnClickListener() {
                int pos = getAdapterPosition();

                @Override
                public void onClick(View v) {
                   /* int pos = getAdapterPosition();
                    Intent in = new Intent(v.getContext(), AdoptionPetProfile.class);
                    // Bundle extras = new Bundle();
                    in.putExtra("petname", name.getText().toString());
                    in.putExtra("age", age.getText().toString());
                    in.putExtra("gender", gender.getText().toString());
                    in.putExtra("breed", breed.getText().toString());
                    in.putExtra("weight",weigh.getText().toString());
                    in.putExtra("height",heigh.getText().toString());
                    in.putExtra("size",size.getText().toString());
                    in.putExtra("image",imag.getText().toString());
                    in.putExtra("lat",latt.getText().toString());
                    in.putExtra("lon",lonn.getText().toString());
                    in.putExtra("phone", phone.getText().toString());
                    in.putExtra("date", date.getText().toString());
                    v.getContext().startActivity(in);*/

                    Intent in = new Intent(v.getContext(), AdoptionPagerActivity.class);
                    int pos = getAdapterPosition();
                    in.putExtra("pos", pos);
                    in.putExtra("categ", categ.getText().toString());

                    in.putExtra("brd", breed.getText().toString());
                    in.putExtra("gnd", gender.getText().toString());
                    in.putExtra("ageS", age.getText().toString());
                    in.putExtra("lat", latt.getText().toString());
                    in.putExtra("lon", lonn.getText().toString());
                    in.putExtra("dist", dist.getText().toString());
                    in.putExtra("pager", pager.getText().toString());
                    v.getContext().startActivity(in);

                }
            });

        }

    }
}
