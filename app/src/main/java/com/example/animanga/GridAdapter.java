package com.example.animanga;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.animanga.data.Item;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private ArrayList<Item> items;

    GridAdapter(ArrayList<Item> items) {
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watched,null);
        TextView textView = convertView.findViewById(R.id.name);
        ImageView imageView = convertView.findViewById(R.id.image_poster);
        Glide.with(parent.getContext())
                .load(items.get(position).getPic())
                .placeholder(R.drawable.ic_android_black_24dp)
                .into(imageView);
        textView.setText(items.get(position).getName());
        return convertView;
    }
}
