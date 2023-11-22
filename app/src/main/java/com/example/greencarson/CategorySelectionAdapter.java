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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                int lastSelected = selectedCategory;
                selectedCategory = holder.getAdapterPosition();
                notifyItemChanged(selectedCategory);
                notifyItemChanged(lastSelected);
            }
        });
    }

    public String getSelectedCategory(){
        return this.data.get(selectedCategory).getName();
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