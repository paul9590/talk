package com.ping.talk;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class MyArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    String id;

    public MyArrayAdapter(Context context, ArrayList<String> values, String id) {
        super(context, R.layout.listlay, values);
        this.context = context;
        this.values = values;
        this.id = id;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listlay, parent, false);
        TextView txtchat = (TextView) rowView.findViewById(R.id.txtchat);
        txtchat.setText(values.get(position));


        String s = values.get(position);

        if (s.startsWith("["+id+"]")) {
            txtchat.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }

        return rowView;
    }
}
