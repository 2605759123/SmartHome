package com.example.smarthome;

public class MySettingdata {
    private String name;
    private int imageId;
    private String switch1;//开关（闹钟）
    public MySettingdata(String name,String switch1,int imageId){
        this.name=name;
        this.imageId=imageId;
        this.switch1=switch1;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getSwitch1() {
        return switch1;
    }
}
