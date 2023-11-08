package com.example.greencarson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomGridAdapter extends BaseAdapter {
    private Context context;
    private String[] imageFileNames;

    public CustomGridAdapter(Context context, String[] imageFileNames) {
        this.context = context;
        this.imageFileNames = imageFileNames;
    }

    @Override
    public int getCount() {
        return imageFileNames.length;
    }

    @Override
    public Object getItem(int position) {
        return imageFileNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.material_list_container, parent, false);
        }

        String fileName = imageFileNames[position];

        ImageView imageView = convertView.findViewById(R.id.material_imageView);
        TextView textView = convertView.findViewById(R.id.material_textView);

        // Carga la imagen local desde el archivo y muestra el nombre del archivo
        int imageResourceId = context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
        imageView.setImageResource(imageResourceId);
        textView.setText(fileName);

        return convertView;
    }
}


