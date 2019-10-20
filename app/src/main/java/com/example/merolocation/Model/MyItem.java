package com.example.merolocation.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {

    private int id;
    private  double lat , lng;
    private String Name;
    private String vechileType;

    private final LatLng mPosition;
     String mTitle;
     String mSnippet;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MyItem(double lat, double lng, String Name,String vechileType) {
        mPosition = new LatLng(lat, lng);
        mTitle = Name;
        mSnippet=vechileType;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}