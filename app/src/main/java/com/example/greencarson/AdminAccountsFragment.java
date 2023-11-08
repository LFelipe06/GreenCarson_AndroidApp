package com.example.greencarson;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

public class AdminAccountsFragment extends Fragment implements View.OnClickListener {

    AddAdminFragment addAdminFragment = new AddAdminFragment();
    Button nuevoAdministrador;
    View view;

    public AdminAccountsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_accounts, container, false);
        nuevoAdministrador = view.findViewById(R.id.nuevoAdministrador);
        nuevoAdministrador.setOnClickListener(this);
        // Recycler view
        RecyclerView recyclerView = view.findViewById(R.id.cuentasAdminRecycler);
        CustomAdapter adapter = new CustomAdapter();
        List<String> data = Arrays.asList("text1", "text2", "text3");
        adapter.setData(data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    @Override
    public void onClick(View v) {
        getParentFragmentManager().beginTransaction().replace(R.id.container, addAdminFragment).commit();
    }
}