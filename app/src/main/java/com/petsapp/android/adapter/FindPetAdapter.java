package com.petsapp.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.petsapp.android.Model.Data;
import com.petsapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by WEB DESIGNING on 23-03-2016.
 */
public class FindPetAdapter extends RecyclerView.Adapter<FindPetAdapter.ViewHolder> {

    private List<Data> itemList;
    private Context context;


    public FindPetAdapter(Context context, List<Data> itemList) {
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
        viewHolder.categ.setText(itemList.get(position).getcateg());
        // viewHolder.heigh.setText(itemList.get(position).getheight());
        // viewHolder.size.setText(itemList.get(position).getsize());
        viewHolder.imag.setText(itemList.get(position).getphoto());
        viewHolder.latt.setText(itemList.get(position).getlat());
        viewHolder.lonn.setText(itemList.get(position).getlon());
        viewHolder.phone.setText(itemList.get(position).getphone());
        viewHolder.date.setText(itemList.get(position).getDate());
        viewHolder.lnfStatus.setText(itemList.get(position).getLostorfound());


        String state1 = viewHolder.lnfStatus.getText().toString();

        if (state1.equalsIgnoreCase("Missing")) {

            viewHolder.lnfStatus.setBackgroundColor(Color.parseColor("#f8a04f"));

        } else {
            viewHolder.lnfStatus.setBackgroundColor(Color.parseColor("#ed6e27"));
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lnf_cntct_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, gender, breed, categ, heigh, size, imag, latt, lonn, phone, date, lnfStatus;
        ImageView photo;

        public ViewHolder(final View itemView) {
            super(itemView);
            breed = (TextView) itemView.findViewById(R.id.breed);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            categ = (TextView) itemView.findViewById(R.id.weig);
            heigh = (TextView) itemView.findViewById(R.id.heigh);
            size = (TextView) itemView.findViewById(R.id.size);
            imag = (TextView) itemView.findViewById(R.id.imag);
            latt = (TextView) itemView.findViewById(R.id.latt);
            lonn = (TextView) itemView.findViewById(R.id.lonn);
            gender = (TextView) itemView.findViewById(R.id.gender);
            photo = (ImageView) itemView.findViewById(R.id.picture);
            phone = (TextView) itemView.findViewById(R.id.phone);
            date = (TextView) itemView.findViewById(R.id.dateId);
            lnfStatus = (TextView) itemView.findViewById(R.id.status_text_id);

            //  CardView card = (CardView) itemView.findViewById(R.id.card);
            itemView.setOnClickListener(new View.OnClickListener() {
                int pos = getAdapterPosition();

                @Override
                public void onClick(View v) {
                   /* int pos = getAdapterPosition();
                    Intent in = new Intent(v.getContext(), LostFoundPetProfile.class);
                    // Bundle extras = new Bundle();
                    in.putExtra("petname", name.getText().toString());
                    in.putExtra("age", age.getText().toString());
                    in.putExtra("gender", gender.getText().toString());
                    in.putExtra("breed", breed.getText().toString());
                    in.putExtra("date",date.getText().toString());
                //    in.putExtra("height",heigh.getText().toString());
                //    in.putExtra("size",size.getText().toString());
                    in.putExtra("image",imag.getText().toString());
                    in.putExtra("lat",latt.getText().toString());
                    in.putExtra("lon",lonn.getText().toString());
                    in.putExtra("phone", phone.getText().toString());
                    in.putExtra("lnfStatus", lnfStatus.getText().toString());

                    v.getContext().startActivity(in);*/

                    int pos = getAdapterPosition();

                  /*  Intent i = new Intent(v.getContext(), PagerActivity.class);
                    i.putExtra("pos", pos);
                    i.putExtra("categ", categ.getText().toString());
                    v.getContext().startActivity(i);

                    Toast.makeText(v.getContext(), "pos: "+pos, Toast.LENGTH_SHORT).show();*/

                }
            });

        }

    }

}
