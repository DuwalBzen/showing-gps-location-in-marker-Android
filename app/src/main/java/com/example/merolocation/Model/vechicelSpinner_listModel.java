package com.example.merolocation.Model;

public class vechicelSpinner_listModel {
    private String mVehcileName;
    private int mVehcileImage;

    public vechicelSpinner_listModel(String mVehcileName, int mVehcileImage) {
        this.mVehcileName = mVehcileName;
        this.mVehcileImage = mVehcileImage;
    }

    public String getmVehcileName() {
        return mVehcileName;
    }

    public void setmVehcileName(String mVehcileName) {
        this.mVehcileName = mVehcileName;
    }

    public int getmVehcileImage() {
        return mVehcileImage;
    }

    public void setmVehcileImage(int mVehcileImage) {
        this.mVehcileImage = mVehcileImage;
    }
}
