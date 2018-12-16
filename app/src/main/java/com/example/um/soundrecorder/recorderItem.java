package com.example.um.soundrecorder;

public class recorderItem {
    private String time,name,path;
    private int ImgID;
    public recorderItem(String name,String time,String path){
        this.name= name;
        this.time=time;
        this.ImgID=R.drawable.zzz;
        this.path=path;
    }
    public String getTime(){return time;}
    public String getName(){return name;}
    public String getPath(){return path;}
    public int getImgID(){return ImgID;}
}
