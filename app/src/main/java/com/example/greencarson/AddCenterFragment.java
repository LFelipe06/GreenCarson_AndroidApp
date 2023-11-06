package com.example.greencarson;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddCenterFragment extends Fragment {
    // Variables para la hora y minuto de apertura/cierre
    public int horaApertura, horaCierre, minutoApertura, minutoCierre, hora, minuto;
    public boolean hourChecker = false; // false para hora de apertura, true para hora de cierre
    private List<String> diasSelecionados;

    private static final String TAG = "AddCenterFragment";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_DIRECCION = "direccion";
    private static final String KEY_LATITUD = "latitud";
    private static final String KEY_LONGITUG = "longitug";
    private static final String KEY_ESTADO = "estado";
    private static final String KEY_DIAS = "dias";
    private static final String KEY_HORA_APERTURA = "hora_apertura";
    private static final String KEY_HORA_CIERRE = "hora_cierre";
    private static final String KEY_TELEFONO = "num_telefonico";

    private EditText editTextNombre;
    private EditText editTextDireccion;
    private EditText editTextLatitud;
    private EditText editTextLongitud;
    private Button btnDiasCentro;
    private Button btnHoraApertura;
    private Button btnHoraCierre;
    private Button btnRegistrarCentro;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_center, container, false);

        btnHoraApertura = view.findViewById(R.id.btnHoraApertura);
        btnHoraCierre = view.findViewById(R.id.btnHoraCierre);
        btnDiasCentro = view.findViewById(R.id.btnDiasCentro);
        btnRegistrarCentro = view.findViewById(R.id.btn_guardar);

        editTextNombre = view.findViewById(R.id.editTextNombreCentro);
        editTextDireccion = view.findViewById(R.id.editTextDireccionCentro);
        editTextLatitud = view.findViewById(R.id.editTextLatitudCentro);
        editTextLongitud = view.findViewById(R.id.editTextLongitudCentro);

        // Guarda la hora y minuto de apertura desde un dialogo/pop-up de tipo hora
        btnHoraApertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hourChecker = false;
                popTimePicker(v);
            }
        });

        // Guarda la hora y minuto de cierre desde un dialogo/pop-up de tipo hora
        btnHoraCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hourChecker = true;
                popTimePicker(v);
            }
        });

        btnDiasCentro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog();
            }
        });

        btnRegistrarCentro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarCentro();
            }
        });
        return view;
    }

    // Dialogo para seleccionar los días del centro
    public void CreateAlertDialog(){
        // Array que almacena los días del centro
        diasSelecionados = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccione los días");
        builder.setMultiChoiceItems(R.array.dias, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String arr[] = getResources().getStringArray(R.array.dias);

                if (isChecked) {
                    diasSelecionados.add(arr[which]);
                }
                else diasSelecionados.remove(arr[which]);
            }
        });
        builder.setPositiveButton("Show", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder dias = new StringBuilder();
                for(String item:diasSelecionados){
                    dias.append(" ").append(item);
                }
                btnDiasCentro.setText(dias.toString());
            }
        });

        builder.create();
        builder.show();
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                String horaminuto = String.format("%02d:%02d", selectedHour, selectedMinute);
                if(hourChecker){
                    // Se actualiza la hora de cierre
                    horaCierre = selectedHour;
                    minutoCierre = selectedMinute;
                    btnHoraCierre.setText(horaminuto);
                }else{
                    // Se actualiza la hora de apertura
                    horaApertura = selectedHour;
                    minutoApertura = selectedMinute;
                    btnHoraApertura.setText(horaminuto);
                }
                hora = selectedHour;
                minuto = selectedMinute;
            }
        };


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, hora, minuto, true);
        timePickerDialog.setTitle("Seleccione un horario");
        timePickerDialog.show();

    }

    public void registrarCentro(){
        String nombre = editTextNombre.getText().toString();
        String direccion = editTextDireccion.getText().toString();
        String horaAperturaCentro = Integer.toString(horaApertura) + ":" + Integer.toString(minutoApertura);
        String horaCierreCentro = Integer.toString(horaCierre) + ":" + Integer.toString(minutoCierre);
        float latitud = Float.parseFloat(editTextLatitud.getText().toString());
        float longitud = Float.parseFloat(editTextLongitud.getText().toString());

        Map<String, Object> centro = new HashMap<>();
        centro.put(KEY_NOMBRE, nombre);
        centro.put(KEY_DIRECCION, direccion);
        centro.put(KEY_LATITUD, latitud);
        centro.put(KEY_LONGITUG, longitud);
        centro.put(KEY_DIAS, diasSelecionados);
        centro.put(KEY_HORA_APERTURA, horaAperturaCentro);
        centro.put(KEY_HORA_CIERRE, horaCierreCentro);
        centro.put(KEY_ESTADO,true);

        db.collection("centros").document("Otro centro").set(centro)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Registro de centro exitoso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al registrar centro", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
