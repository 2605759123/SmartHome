package com.example.smarthome;

public class MyNewsdata {
    private String name;
    private int imageId;
    private String neirong;
    public MyNewsdata(String name,int imageId,String neirong){
        this.name=name;
        this.imageId=imageId;
        this.neirong=neirong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getNeirong() {
        return neirong;
    }

    public void setNeirong(String neirong) {
        this.neirong = neirong;
    }
}
