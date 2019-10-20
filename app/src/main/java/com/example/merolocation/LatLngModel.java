package com.example.merolocation;

import java.io.Serializable;

public class LatLngModel implements Serializable {

    private int id;
    private  double lat , lng;
    private String Name;
    private String vechileType;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getVechileType() {
        return vechileType;
    }

    public void setVechileType(String vechileType) {
        this.vechileType = vechileType;
    }
}
