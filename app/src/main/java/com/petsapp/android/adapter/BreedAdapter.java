package com.petsapp.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.petsapp.android.Model.BreedModel;
import com.petsapp.android.R;

import java.util.List;

/**
 * Created by WEB DESIGNING on 04-03-2016.
 */
public class BreedAdapter extends ArrayAdapter<BreedModel> {

    //
    String brd;
    private List<BreedModel> serviceModelList;
    private int resource;
    private LayoutInflater inflater;

    /* public BreedAdapter(Context context, int resource, String objects) {
       super(context,resource, Integer.parseInt(objects));

         brd = objects;
       this.resource = resource;

       inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

   }
*/
    public BreedAdapter(Context context, int resource, List<BreedModel> objects) {
        super(context, resource, objects);

        serviceModelList = objects;
        this.resource = resource;

        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.spinner_row, null);
        }

        TextView spinnerText;

        spinnerText = (TextView) convertView.findViewById(R.id.spinner_text);
        spinnerText.setText(serviceModelList.get(position).getName());


        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.spinner_dropdown_item, null);
        }

        TextView spinnerText;

        spinnerText = (TextView) convertView.findViewById(R.id.spinner_dropdown_text);
        // spinnerText.setText(serviceModelList.get(position).getName());

        BreedModel item = getItem(position);

        if (item instanceof CharSequence) {
            spinnerText.setText((CharSequence) item);
        } else {
            spinnerText.setText(item.toString());
        }

        return convertView;
    }
}