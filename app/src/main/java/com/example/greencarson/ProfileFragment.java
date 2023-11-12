package com.example.greencarson;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    AddAdminFragment addAdminFragment = new AddAdminFragment();
    View view;
    Button crearNuevoAdmin;
    Button cerrarSesion;
    Button guardarProfile;
    EditText editNombre;
    EditText editApellidos;
    EditText editEmail;
    EditText editPassword;
    EditText editNewPassword;
    EditText editNewPasswordConfirmation;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        crearNuevoAdmin = view.findViewById(R.id.crearNuevoAdmin);
        cerrarSesion = view.findViewById(R.id.cerrarSesion);
        guardarProfile = view.findViewById(R.id.guardarProfile);
        editNombre = view.findViewById(R.id.editNombreProfile);
        editApellidos = view.findViewById(R.id.editApellidosProfile);
        editEmail = view.findViewById(R.id.editCorreoProfile);
        editPassword = view.findViewById(R.id.editContrasenaProfile);
        editNewPassword = view.findViewById(R.id.editNuevaContrasenaProfile);
        editNewPasswordConfirmation = view.findViewById(R.id.editConfirmaContrasenaProfile);
        cerrarSesion.setOnClickListener(this);
        guardarProfile.setOnClickListener(this);
        crearNuevoAdmin.setOnClickListener(this);
        // Check if session expired
        user = FirebaseAuth.getInstance().getCurrentUser();
        checkSession();
        setData(user);
        return view;
    }

    private void checkSession() {
        if (user == null) {
            Intent home = new Intent(getContext(), MainActivity.class);
            startActivity(home);
        } else {
            String uid = user.getUid();
            //[START OF QUERY]
            DocumentReference docRef = db.collection("administradores").document(uid);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                    boolean isSuperAdmin = Boolean.parseBoolean(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("superAdmin")).toString());
                    // Hide button if not superAdmin
                    if (isSuperAdmin) {
                        crearNuevoAdmin.setVisibility(View.VISIBLE);
                    } else {
                        crearNuevoAdmin.setVisibility(View.GONE);
                    }
                } else {
                    Log.d(ContentValues.TAG, "get failed with ", task.getException());
                }
            });
            //[END OF QUERY]
        }
    }

    private void setData(FirebaseUser user) {
        // Set data in the editText fields
        editEmail.setText(user.getEmail());
        String uid = user.getUid();
        //[START OF QUERY]
        DocumentReference docRef = db.collection("administradores").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
                String nombre = Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("nombre")).toString();
                String apellidos = Objects.requireNonNull(document.getData().get("apellidos")).toString();
                editNombre.setText(nombre);
                editApellidos.setText(apellidos);
            } else {
                Log.d(ContentValues.TAG, "get failed with ", task.getException());
            }
        });
        //[END OF QUERY]

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.crearNuevoAdmin) {
            getParentFragmentManager().beginTransaction().replace(R.id.container, addAdminFragment).addToBackStack(null).commit();
        } else if (v.getId() == R.id.cerrarSesion) {
            ConfirmationPopUp confirmationDialog = new ConfirmationPopUp(
                    requireActivity(),
                    "¿Estás seguro de que deseas cerrar sesión?", () -> {
                mAuth.signOut();
                Intent home = new Intent(getActivity(), MainActivity.class);
                startActivity(home);

            });
            confirmationDialog.show();
            Objects.requireNonNull(confirmationDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } else if (v.getId() == R.id.guardarProfile) {
            String nombre = editNombre.getText().toString();
            String apellidos = editApellidos.getText().toString();
            String email = editEmail.getText().toString();
            String newPassword = editNewPassword.getText().toString();
            String newPasswordConfirmation = editNewPasswordConfirmation.getText().toString();
            if (validateData(nombre, apellidos, email, newPassword, newPasswordConfirmation)) {
                saveChanges(newPassword, nombre, apellidos);
            }
        }
    }

    private boolean validateData(String nombre, String apellidos, String email, String password, String passwordConfirmation) {
        if (Objects.equals(nombre, "")) {
            Snackbar.make(view, "El nombre no puede estar vacío", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (Objects.equals(apellidos, "")) {
            Snackbar.make(view, "Los apellidos no pueden estar vacíos", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (Objects.equals(email, "")) {
            Snackbar.make(view, "El correo no puede estar vacío", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (Objects.equals(password, "")) {
            Snackbar.make(view, "La contraseña no puede estar vacía", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (password.length() < 5) {
            Snackbar.make(view, "La contraseña debe contener al menos 8 caracteres", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (!Objects.equals(password, passwordConfirmation)) {
            Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void saveChanges(String newPassword, String nombre, String apellidos) {
        String uid = user.getUid();
        Map<String, Object> docData = new HashMap<>();
        docData.put("nombre", nombre);
        docData.put("apellidos", apellidos);
        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(user.getEmail()), editPassword.getText().toString());
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "Password updated");
                            } else {
                                Log.d(TAG, "Error password not updated");
                            }
                        });
                        //[START OF QUERY]
                        db.collection("administradores").document(uid)
                                .update(docData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    Snackbar.make(view, "Se actualizaron los datos", Snackbar.LENGTH_SHORT)
                                            .show();
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                        //[END OF QUERY]
                    } else {
                        Log.d(TAG, "Error auth failed");
                        Snackbar.make(view, "La contraseña es incorrecta", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }


}