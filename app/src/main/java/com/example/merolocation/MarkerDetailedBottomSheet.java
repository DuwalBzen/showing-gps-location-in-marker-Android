package com.example.merolocation;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MarkerDetailedBottomSheet extends BottomSheetDialogFragment {
    private String location;


    public MarkerDetailedBottomSheet(String text) {
        this.location = text;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_detail_layout, container, false);
        Button direction= (Button) v.findViewById(R.id.direction);

        TextView name= (TextView) v.findViewById(R.id.name);
        name.setText(location);

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapsActivity)getActivity()).getDirection();
                dismiss();
            }
        });
        return v;
    }
}
