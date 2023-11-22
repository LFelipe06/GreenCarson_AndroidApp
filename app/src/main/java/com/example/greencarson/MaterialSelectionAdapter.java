package com.example.greencarson;

import android.content.Context;
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
import java.util.Objects;
import java.util.Set;

public class MaterialSelectionAdapter extends RecyclerView.Adapter<MaterialSelectionAdapter.ViewHolder> {
    private final ArrayList<Item> data;
    private final Set<String> activeMaterials;
    //private FilterChangeInterface responder;
    public MaterialSelectionAdapter(ArrayList<Item> data, Set<String> activeMaterials){
        this.data = data;
        this.activeMaterials = activeMaterials;
    }


    @NonNull
    @Override
    public MaterialSelectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_category_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isActive(position)){
            holder.imageView.setBackgroundResource(R.color.lightGreen);
            holder.imageView.setVisibility(View.INVISIBLE);
        }
        else{
            holder.imageView.setBackgroundResource(R.color.white);
            holder.imageView.setVisibility(View.VISIBLE);
        }
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(this.data.get(position).getImageUrl()).into(holder.imageView);
        holder.textView.setText(this.data.get(holder.getAdapterPosition()).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (isActive(holder.getAdapterPosition())) {
                    activeMaterials.remove(data.get(holder.getAdapterPosition()).getName());
                }
                else{
                    activeMaterials.add(data.get(holder.getAdapterPosition()).getName());
                }
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    private boolean isActive(int position){
        for (String material : activeMaterials){
            if (Objects.equals(this.data.get(position).getName(), material)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.itemImage);
            this.textView = view.findViewById(R.id.itemName);
        }
    }
}