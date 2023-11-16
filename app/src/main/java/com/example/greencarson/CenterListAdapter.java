package com.example.greencarson;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CenterListAdapter extends RecyclerView.Adapter<CenterListAdapter.ViewHolder> {
    private final ArrayList<CenterItem> data;
    public CenterListAdapter (ArrayList<CenterItem> data){
        this.data = data;
    }

    @NonNull
    @Override
    public CenterListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.center_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(this.data.get(position).getImagen()).into(holder.imageView);
        holder.textView.setText(this.data.get(position).getNombre());
        holder.categoryTextView.setText(holder.textView.getContext().getResources().getString(R.string.categoria_centro,this.data.get(position).getCategoria()));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;
        private final ImageView imageView;
        private final TextView categoryTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.imageView = view.findViewById(R.id.centerImage);
            this.textView = view.findViewById(R.id.nombteCentroItem);
            this.categoryTextView = view.findViewById(R.id.categoriaCentroItem);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position : " + getLayoutPosition() + " text : " + this.textView.getText(), Toast.LENGTH_SHORT).show();
        }

    }
}