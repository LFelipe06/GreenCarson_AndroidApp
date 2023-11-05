package com.example.greencarson;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddAdminFragment extends Fragment implements View.OnClickListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    private FirebaseAuth newAuth;
    Button guardarAddAdminBtn;
    EditText editNombre;
    EditText editApellidos;
    EditText editEmail;
    EditText editPassword;
    EditText editPasswordConfirmation;

    public AddAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_admin, container, false);
        guardarAddAdminBtn = view.findViewById(R.id.guardarAddAdmin);
        guardarAddAdminBtn.setOnClickListener(this);
        editNombre = view.findViewById(R.id.editNombre);
        editApellidos = view.findViewById(R.id.editApellidos);
        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);
        editPasswordConfirmation = view.findViewById(R.id.editPasswordConfirmation);
        return view;
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.guardarAddAdmin) {
            String nombre = editNombre.getText().toString();
            String apellidos = editApellidos.getText().toString();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            String passwordConfirmation = editPasswordConfirmation.getText().toString();
            if (validateData(nombre, apellidos, email, password, passwordConfirmation)){
                register(nombre, apellidos, email,  password);
            }
        }
    }

    private void register(String nombre, String apellidos, String email, String password){
        FirebaseApp thisApp = FirebaseApp.getInstance();
        FirebaseApp newApp = FirebaseApp.initializeApp(this.requireContext(), thisApp.getOptions(), thisApp.getName()+"_newUser");
        newAuth = FirebaseAuth.getInstance(newApp);
        newAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = newAuth.getCurrentUser();
                        assert user != null;
                        addUserData(user, nombre, apellidos);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Snackbar.make(view, "Error en el registro. El email podría estar en uso en otra cuenta", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private boolean validateData(String nombre, String apellidos, String email, String password, String passwordConfirmation){
        if (Objects.equals(nombre, "")){
            Snackbar.make(view, "El nombre no puede estar vacío", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (Objects.equals(apellidos, "")){
            Snackbar.make(view, "Los apellidos no pueden estar vacíos", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (Objects.equals(email, "")){
            Snackbar.make(view, "El correo no puede estar vacío", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (Objects.equals(password, "")){
            Snackbar.make(view, "La contraseña no puede estar vacía", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (password.length() < 5){
            Snackbar.make(view, "La contraseña debe contener al menos 8 caracteres", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (!Objects.equals(password, passwordConfirmation)){
            Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void addUserData(FirebaseUser user, String nombre, String apellidos) {
        //Check if user is admin
        String uid = user.getUid();
        Map<String, Object> docData = new HashMap<>();
        docData.put("nombre", nombre);
        docData.put("apellidos", apellidos);
        docData.put("superAdmin", false);

        //[START OF QUERY]
        db.collection("administradores").document(uid)
                .set(docData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        //[END OF QUERY]
        Snackbar.make(view, "Usuario registrado con éxito", Snackbar.LENGTH_SHORT)
                .show();
        newAuth.signOut();
    }
}