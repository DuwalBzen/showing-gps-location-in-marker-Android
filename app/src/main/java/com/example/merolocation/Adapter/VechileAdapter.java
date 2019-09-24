package com.example.merolocation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.merolocation.Model.vechicelSpinner_listModel;
import com.example.merolocation.R;

import java.util.ArrayList;

public class VechileAdapter extends  ArrayAdapter<vechicelSpinner_listModel> {

    public VechileAdapter(Context context, ArrayList<vechicelSpinner_listModel> vechileList) {
        super(context, 0, vechileList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.vechile_spinner_row, parent, false
            );
        }

        ImageView imageVechicle= convertView.findViewById(R.id.vechile_image_id);
        TextView textViewName = convertView.findViewById(R.id.vechile_name_id);

        vechicelSpinner_listModel currentItem = getItem(position);

        if (currentItem != null) {
            imageVechicle.setImageResource(currentItem.getmVehcileImage());
            textViewName.setText(currentItem.getmVehcileName());
        }

        return convertView;
    }
}
