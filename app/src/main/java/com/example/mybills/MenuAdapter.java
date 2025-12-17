package com.example.mybills;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<String> {

    Activity context;
    String[] title, subtitle;
    Integer[] image;

    public MenuAdapter(Activity context, String[] title, String[] subtitle, Integer[] image) {
        super(context, R.layout.custom_menu_layout, title);
        this.context = context;
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
    }

    public View getView(int position, View view, ViewGroup parent) {
        View row = LayoutInflater.from(context)
                .inflate(R.layout.custom_menu_layout, null, true);

        ((TextView) row.findViewById(R.id.txtTitle)).setText(title[position]);
        ((TextView) row.findViewById(R.id.txtSub)).setText(subtitle[position]);
        ((ImageView) row.findViewById(R.id.imgIcon))
                .setImageResource(image[position]);

        return row;
    }
}
