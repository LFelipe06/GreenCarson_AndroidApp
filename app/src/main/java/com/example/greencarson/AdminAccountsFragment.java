package com.example.greencarson;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        return view;
    }

    @Override
    public void onClick(View v) {
        getParentFragmentManager().beginTransaction().replace(R.id.container, addAdminFragment).commit();
    }
}