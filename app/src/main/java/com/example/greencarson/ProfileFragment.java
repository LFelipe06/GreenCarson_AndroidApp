package com.example.greencarson;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    AdminAccountsFragment adminsAccountsFragment = new AdminAccountsFragment();
    View view;
    Button administrarCuentas;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        administrarCuentas = view.findViewById(R.id.administrarCuentas);
        administrarCuentas.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        getParentFragmentManager().beginTransaction().replace(R.id.container, adminsAccountsFragment).commit();
    }
}