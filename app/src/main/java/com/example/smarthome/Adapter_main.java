package com.example.smarthome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adapter_main extends ArrayAdapter<MyNewsdata> {
    private int resourceId;
    private Context context;
    public Adapter_main(@NonNull Context context, int resource, @NonNull List<MyNewsdata> objects) {
        super(context, resource, objects);
        resourceId=resource;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final MyNewsdata myNewsdata=getItem(position);
        View view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView biaoti=view.findViewById(R.id.tv_biaoti);
        ImageView img=view.findViewById(R.id.iv_news_img);
        TextView neirong=view.findViewById(R.id.tv_news_neirong);
        biaoti.setText(myNewsdata.getName());
        img.setImageResource(myNewsdata.getImageId());
        neirong.setText(myNewsdata.getNeirong());


        return view;
    }
}
