package com.petsapp.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.petsapp.android.Model.Allpetlist;
import com.petsapp.android.Petprofilereg;
import com.petsapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Android on 04-03-2016.
 */


public class PetRecyclerview extends RecyclerView.Adapter<PetRecyclerview.ViewHolder> {
    private List<Allpetlist> itemList;
    private Context context;


    public PetRecyclerview(Context context, List<Allpetlist> itemList) {
        this.itemList = itemList;
        this.context = context;
        System.out.println("it------" + itemList.size());
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Picasso.with(context).load(itemList.get(position).getPhoto()).into(viewHolder.icons);
        viewHolder.name.setText(itemList.get(position).getName());
        viewHolder.age.setText(itemList.get(position).getage());
        viewHolder.breed.setText(itemList.get(position).getbreed());
        viewHolder.gender.setText(itemList.get(position).getgender());
        viewHolder.wei.setText(itemList.get(position).getwieght());
        viewHolder.heig.setText(itemList.get(position).getheight());
        viewHolder.siz.setText(itemList.get(position).getsize());
        viewHolder.img.setText(itemList.get(position).getPhoto());
        viewHolder.llat.setText(itemList.get(position).getlat());
        viewHolder.llon.setText(itemList.get(position).getlon());
        viewHolder.petmateId.setText(itemList.get(position).getPetmetId());
        viewHolder.about.setText(itemList.get(position).getAbout());
        viewHolder.category.setText(itemList.get(position).getCategory());
        viewHolder.vacciText.setText(itemList.get(position).getVacci());
        viewHolder.crossbreedText.setText(itemList.get(position).getCrossbreed());
        //viewHolder.photo.setImageResource(Integer.parseInt(itemList.get(position).getPhoto()));

        String age1 = viewHolder.age.getText().toString();
        if (age1.equalsIgnoreCase("")) {
            viewHolder.yrs.setVisibility(View.GONE);
        } else {
            viewHolder.yrs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.allpetslist, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, gender, breed, wei, heig, siz, llat, llon, img, petmateId, about, category, vacciText, crossbreedText, yrs;
        Spinner breedsp, agesp, gendersp, distancesp;
        ImageView photo;
        ImageView icons;

        public ViewHolder(final View itemView) {
            super(itemView);
            breed = (TextView) itemView.findViewById(R.id.breed);
            icons = (ImageView) itemView.findViewById(R.id.picture);
            wei = (TextView) itemView.findViewById(R.id.wei);
            heig = (TextView) itemView.findViewById(R.id.heig);
            siz = (TextView) itemView.findViewById(R.id.siz);
            llat = (TextView) itemView.findViewById(R.id.llat);
            llon = (TextView) itemView.findViewById(R.id.llon);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            img = (TextView) itemView.findViewById(R.id.img);
            gender = (TextView) itemView.findViewById(R.id.gender);
            petmateId = (TextView) itemView.findViewById(R.id.petmateId);
            about = (TextView) itemView.findViewById(R.id.aboutId);
            category = (TextView) itemView.findViewById(R.id.categoryId);
            vacciText = (TextView) itemView.findViewById(R.id.vacciId);
            yrs = (TextView) itemView.findViewById(R.id.first);
            crossbreedText = (TextView) itemView.findViewById(R.id.crossbreedId);
            // photo=(ImageView)itemView.findViewById(R.id.picture);
            //CardView card = (CardView) itemView.findViewById(R.id.cardlist);
            itemView.setOnClickListener(new View.OnClickListener() {
                int pos = getAdapterPosition();

                @Override
                public void onClick(View v) {
                    int pos1 = getAdapterPosition();
                    Intent in = new Intent(v.getContext(), Petprofilereg.class);
                    //   Intent in = new Intent(v.getContext(), MyPetPagerActivity.class);
                    int pos = getAdapterPosition();
                    in.putExtra("pos", pos);
                    in.putExtra("petname", name.getText().toString());
                    in.putExtra("age", age.getText().toString());
                    in.putExtra("gender", gender.getText().toString());
                    in.putExtra("breed", breed.getText().toString());
                    in.putExtra("weight", wei.getText().toString());
                    in.putExtra("height", heig.getText().toString());
                    in.putExtra("size", siz.getText().toString());
                    in.putExtra("lat", llat.getText().toString());
                    in.putExtra("lon", llon.getText().toString());
                    in.putExtra("image", img.getText().toString());
                    in.putExtra("petmateId", petmateId.getText().toString());
                    in.putExtra("about", about.getText().toString());
                    in.putExtra("category", category.getText().toString());
                    in.putExtra("vacci", vacciText.getText().toString());
                    in.putExtra("crossbreed", crossbreedText.getText().toString());
                    // System.out.println("panme" + ptname.getText().toString());
                    v.getContext().startActivity(in);
//IMPORTANT
                    /*Intent in = new Intent(v.getContext(), MyPetPagerActivity.class);
                    int pos = getAdapterPosition();
                    in.putExtra("pos", pos);
                    v.getContext().startActivity(in);*/

                }
            });

        }
    }
}