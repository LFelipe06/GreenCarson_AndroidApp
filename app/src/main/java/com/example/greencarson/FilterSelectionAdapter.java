package com.example.greencarson;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FilterSelectionAdapter extends RecyclerView.Adapter<FilterSelectionAdapter.ViewHolder> {
    private final ArrayList<Item> data;
    private final Set<String> activeFilters;
    private FilterChangeInterface responder;
    public FilterSelectionAdapter(ArrayList<Item> data, Set<String> activeFilters, FilterChangeInterface responder){
        this.data = data;
        this.activeFilters = activeFilters;
        this.responder = responder;
    }


    @NonNull
    @Override
    public FilterSelectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_category_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isActive(position)){
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
                if (isActive(holder.getAdapterPosition())) {
                    activeFilters.remove(data.get(holder.getAdapterPosition()).getName());
                }
                else{
                    activeFilters.add(data.get(holder.getAdapterPosition()).getName());
                }
                notifyItemChanged(holder.getAdapterPosition());
                responder.filterCenters();
            }
        });
    }

    private boolean isActive(int position){
        for (String filter : activeFilters){
            if (Objects.equals(this.data.get(position).getName(), filter)) {
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