package com.example.um.soundrecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class myAdapter extends ArrayAdapter<recorderItem> {
    private int resourceID;
    public myAdapter(Context context, int textViewResourceID, List<recorderItem> object){
        super(context,textViewResourceID,object);
        resourceID=textViewResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        recorderItem recorderItem = getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
        ImageView flagImg=(ImageView)view.findViewById(R.id.itemimg);
        TextView flagname=(TextView)view.findViewById(R.id.itemname);
        TextView flagtime=(TextView)view.findViewById(R.id.itemtime);
        flagname.setText("录音名称:"+recorderItem.getName());
        flagtime.setText("录音时长:"+recorderItem.getTime()+"S");
        flagImg.setImageResource(R.drawable.zzz);
        return view;
    }
}
