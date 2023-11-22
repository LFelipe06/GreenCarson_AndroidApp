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
    private String selectedCategory;
    //private FilterChangeInterface responder;
    public CategorySelectionAdapter(ArrayList<Item> data, String selectedCategory){
        this.data = data;
        this.selectedCategory = selectedCategory;
    }


    @NonNull
    @Override
    public CategorySelectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_category_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isSelectedCategory(position)){
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
                selectedCategory = data.get(holder.getAdapterPosition()).getName();

                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    private boolean isSelectedCategory(int position){
        return Objects.equals(this.data.get(position).getName(), selectedCategory);
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