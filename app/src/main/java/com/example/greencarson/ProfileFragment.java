package com.example.greencarson;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    AdminAccountsFragment adminsAccountsFragment = new AdminAccountsFragment();
    View view;
    Button administrarCuentas;
    Button cerrarSesion;
    Button guardarProfile;
    EditText editEmail;
    EditText editPassword;
    EditText editNewPassword;
    EditText editNewPasswordConfirmation;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // If session expired
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent home = new Intent(getContext(), MainActivity.class);
            startActivity(home);
        }
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        administrarCuentas = view.findViewById(R.id.administrarCuentas);
        cerrarSesion = view.findViewById(R.id.cerrarSesion);
        guardarProfile = view.findViewById(R.id.guardarProfile);
        editEmail = view.findViewById(R.id.editCorreoProfile);
        editPassword = view.findViewById(R.id.editContrasenaProfile);
        editNewPassword = view.findViewById(R.id.editNuevaContrasenaProfile);
        editNewPasswordConfirmation = view.findViewById(R.id.editConfirmaContrasenaProfile);
        cerrarSesion.setOnClickListener(this);
        guardarProfile.setOnClickListener(this);
        administrarCuentas.setOnClickListener(this);
        editEmail.setText(user.getEmail());
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.administrarCuentas) {
            getParentFragmentManager().beginTransaction().replace(R.id.container, adminsAccountsFragment).commit();
        } else if (v.getId() == R.id.cerrarSesion) {
            signOut();
        } else if (v.getId() == R.id.guardarProfile) {
            String email = editEmail.getText().toString();
            String newPassword = editNewPassword.getText().toString();
            String newPasswordConfirmation = editNewPasswordConfirmation.getText().toString();
            if (validateData(email, newPassword, newPasswordConfirmation)){
                saveChanges(newPassword);
            }
        }
    }

    private boolean validateData(String email, String password, String passwordConfirmation){
        if (Objects.equals(email, "")){
            Snackbar.make(view, "El correo no puede estar vacío", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (Objects.equals(password, "")){
            Snackbar.make(view, "La contraseña nueva no puede estar vacía", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (password.length() < 5){
            Snackbar.make(view, "La contraseña nueva debe contener al menos 8 caracteres", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (!Objects.equals(password, passwordConfirmation)){
            Snackbar.make(view, "Las contraseñas nuevas no coinciden", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void saveChanges(String newPassword) {

        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(user.getEmail()), editPassword.getText().toString());
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "Password updated");
                                Snackbar.make(view, "Se actualizó la contraseña", Snackbar.LENGTH_SHORT)
                                        .show();
                            } else {
                                Log.d(TAG, "Error password not updated");
                            }
                        });
                    } else {
                        Log.d(TAG, "Error auth failed");
                        Snackbar.make(view, "La contraseña es incorrecta", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }


    private void signOut() {
        // TODO : add pop up for confirmation
        mAuth.signOut();
        Intent home = new Intent(getActivity(), MainActivity.class);
        startActivity(home);
    }
}