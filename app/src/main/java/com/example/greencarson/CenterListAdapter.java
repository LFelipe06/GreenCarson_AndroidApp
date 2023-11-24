package com.example.greencarson;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

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
        if (!Objects.equals(this.data.get(position).getImagen(), "")){
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(this.data.get(position).getImagen()).into(holder.imageView);
        }
        holder.textView.setText(this.data.get(position).getNombre());
        holder.categoryTextView.setText(holder.textView.getContext().getResources().getString(R.string.categoria_centro,this.data.get(position).getCategoria()));
        holder.id = this.data.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;
        private final ImageView imageView;
        private final TextView categoryTextView;
        public String id ;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.imageView = view.findViewById(R.id.centerImage);
            this.textView = view.findViewById(R.id.nombteCentroItem);
            this.categoryTextView = view.findViewById(R.id.categoriaCentroItem);
            // this.id = this.;
        }

        @Override
        public void onClick(View view) {
            FragmentManager fragmentManager = ((FragmentActivity) itemView.getContext()).getSupportFragmentManager();
            //Toast.makeText(view.getContext(), "position : " + getLayoutPosition() + " text : " + this.textView.getText(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(view.getContext(), "ID : " + this.id, Toast.LENGTH_SHORT).show();
            sendDataToSecondFragment(this.id, fragmentManager);

        }
        private Bundle createFloatBundle(String id) {
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            return bundle;
        }

        private void sendDataToSecondFragment(String id, FragmentManager fragmentManager) {
            Bundle dataBundle = createFloatBundle(id);

            EditCenterFragment secondFragment = new EditCenterFragment();
            secondFragment.setArguments(dataBundle);

            // Use FragmentManager to replace or add the second fragment
            //assert getFragmentManager() != null;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, secondFragment, "EditCenterFragment");
            transaction.addToBackStack(null); // Optional, if you want to navigate back
            transaction.commit();
        }
    }
}