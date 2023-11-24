package com.example.greencarson;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class CategorySelectionAdapter extends RecyclerView.Adapter<CategorySelectionAdapter.ViewHolder> {
    private final ArrayList<Item> data;
    private int selectedCategory;
    public CategorySelectionAdapter(ArrayList<Item> data){
        this.data = data;
        this.selectedCategory = 0;
    }


    @NonNull
    @Override
    public CategorySelectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_category_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == selectedCategory){
            holder.imageView.setBackgroundResource(R.color.lightGreen);
        }
        else{
            holder.imageView.setBackgroundResource(R.color.white);
        }
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(this.data.get(position).getImageUrl()).into(holder.imageView);
        holder.textView.setText(this.data.get(holder.getAdapterPosition()).getName());
        holder.itemView.setOnClickListener(view -> {
            int lastSelected = selectedCategory;
            selectedCategory = holder.getAdapterPosition();
            notifyItemChanged(selectedCategory);
            notifyItemChanged(lastSelected);
        });
    }

    public void setSelectedCategory(String categoryName){
        int lastSelected = selectedCategory;
        for(int i = 0; i < data.size(); i++){
            if(Objects.equals(data.get(i).getName(), categoryName)){
                selectedCategory = i;
                notifyItemChanged(i);
                notifyItemChanged(lastSelected);
                break;
            }
        }

    }

    public String getSelectedCategory(){
        return this.data.get(selectedCategory).getName();
    }

    public void clearSelection(){
        int lastSelected = selectedCategory;
        selectedCategory = 0;
        notifyItemChanged(selectedCategory);
        notifyItemChanged(lastSelected);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.itemImage);
            this.textView = view.findViewById(R.id.itemName);
        }
    }
}