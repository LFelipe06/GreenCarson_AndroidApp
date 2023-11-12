package com.example.greencarson;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button login;
    EditText email;
    EditText password;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextPassword);
        login=findViewById(R.id.login);
        db= FirebaseFirestore.getInstance();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            checkPermission(currentUser);
        }
    }
    private void signIn(View view, String email, String password) {
        //Handle empty strings
        if (Objects.equals(email, "") || Objects.equals(password, "")){
            Log.w(TAG, "signInWithEmail:failure");
            Snackbar.make(view, "Correo o contraseña incorrectos", Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        //Authenticate
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        checkPermission(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Snackbar.make(view, "Correo o contraseña incorrectos", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
    }
    private void checkPermission(FirebaseUser user) {
        //Check if user is admin
        String uid = user.getUid();
        //[START OF QUERY]
        DocumentReference docRef = db.collection("administradores").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Inicio de sesión exitoso");
                    Intent home = new Intent(MainActivity.this, content.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                    MainActivity.this.finish();
                } else {
                    Log.d(TAG, "El usuario no tiene permisos de administrador");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
        //[END OF QUERY]
    }

    public void login(View v){
        //Button on click
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        this.signIn(v, emailText, passwordText);
    }
}