package com.pierre.biojoux.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import GarbageBin.GarbageBin;

public class GarbageAdapter extends BaseAdapter {

    private Context context = null;
    private final int layout;
    private ArrayList<GarbageBin> data = null;
    private LayoutInflater inflater = null;

    public GarbageAdapter(Context context, int custom_layout, ArrayList<GarbageBin> data) {
        this.context = context;
        this.layout = custom_layout;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size(); }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    public Object getArray() { return data; }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
        }
        TextView statusView = (TextView) convertView.findViewById(R.id.garbageStatus);
        ImageView statusImg = (ImageView) convertView.findViewById(R.id.imageGarbage);
        String status = data.get(position).getStatus();
        statusView.setText(status);
        if (status.equals("Full")) {
            statusImg.setImageResource(R.drawable.fullbin);
        }
        else if (status.equals("Medium")) {
            statusImg.setImageResource(R.drawable.medbin);
        }
        else
        {
            statusImg.setImageResource(R.drawable.emptybin);
        }
        return convertView;
    }
}
