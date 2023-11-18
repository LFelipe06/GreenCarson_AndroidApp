package com.example.greencarson;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
    private static final String KEY_TELEFONO = "num_telefonico";
    private static final String KEY_DIRECCION = "direccion";
    private static final String KEY_LATITUD = "latitud";
    private static final String KEY_LONGITUD = "longitug";
    private static final String KEY_ESTADO = "estado";
    private static final String KEY_DIAS = "dias";
    private static final String KEY_HORA_APERTURA = "hora_apertura";
    private static final String KEY_HORA_CIERRE = "hora_cierre";
    private EditText editTextNombre;
    private EditText editTextTelefono;
    private EditText editTextDireccion;
    private EditText editTextLatitud;
    private EditText editTextLongitud;
    private Button btnDiasCentro;
    private Button btnHoraApertura;
    private Button btnHoraCierre;
    private Button btnRegistrarCentro;
    private Button btnCancelarRegistro;
    private Button btnSeleccionarUbicacion;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_center, container, false);


        btnHoraApertura = view.findViewById(R.id.btnHoraApertura);
        btnHoraCierre = view.findViewById(R.id.btnHoraCierre);
        btnDiasCentro = view.findViewById(R.id.btnDiasCentro);
        btnRegistrarCentro = view.findViewById(R.id.btn_guardar);
        btnCancelarRegistro = view.findViewById(R.id.btn_cancelar);
        btnSeleccionarUbicacion = view.findViewById(R.id.btnSeleccionarUbicacion);

        editTextNombre = view.findViewById(R.id.editTextNombreCentro);
        editTextTelefono = view.findViewById(R.id.editTextTelefonoCentro);
        editTextDireccion = view.findViewById(R.id.editTextDireccionCentro);
        editTextLatitud = view.findViewById(R.id.editTextLatitudCentro);
        editTextLongitud = view.findViewById(R.id.editTextLongitudCentro);

        Bundle bundle = getArguments();
        if (bundle != null) {
            float variable1 = bundle.getFloat("latitud");
            String lat = String.valueOf(variable1);
            float variable2 = bundle.getFloat("longitud");
            String longi = String.valueOf(variable2);

            editTextLatitud.setText(lat);
            editTextLongitud.setText(longi);

            ReverseGeocodingTask reverseGeocodingTask = new ReverseGeocodingTask(requireActivity(), new ReverseGeocodingTask.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String result) {
                    // El resultado es la dirección obtenida
                    if (result != null) {
                        editTextDireccion.setText(result);
                        Log.d(TAG, "Dirección: " + result);
                    } else {
                        Log.e(TAG, "No se pudo obtener la dirección.");
                    }
                }
            });
            double var1 = variable1;
            double var2 = variable2;

            reverseGeocodingTask.execute(var1, var2);
        }

        btnSeleccionarUbicacion.setOnClickListener(v -> {
            // Crear e iniciar la transacción del fragmento del mapa
            PickLocationFragment pickLocationFragment= new PickLocationFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, pickLocationFragment)
                    .addToBackStack(null)
                    .commit();

        });

        // Guarda la hora y minuto de apertura desde un dialogo/pop-up de tipo hora
        btnHoraApertura.setOnClickListener(v -> {
            hourChecker = false;
            popTimePicker();
        });

        // Guarda la hora y minuto de cierre desde un dialogo/pop-up de tipo hora
        btnHoraCierre.setOnClickListener(v -> {
            hourChecker = true;
            popTimePicker();
        });

        btnDiasCentro.setOnClickListener(v -> CreateAlertDialog());

        btnRegistrarCentro.setOnClickListener(v -> registrarCentro());

        btnCancelarRegistro.setOnClickListener(v -> clearFields());

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
                String[] arr = getResources().getStringArray(R.array.dias);

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

    public void popTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                @SuppressLint("DefaultLocale") String horaminuto = String.format("%02d:%02d", selectedHour, selectedMinute);
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

    public void registrarCentro() {
        String nombre = editTextNombre.getText().toString();
        String telefono = editTextTelefono.getText().toString();
        String direccion = editTextDireccion.getText().toString();
        String horaAperturaCentro = horaApertura + ":" + minutoApertura;
        String horaCierreCentro = horaCierre + ":" + minutoCierre;
        float latitud = Float.parseFloat(editTextLatitud.getText().toString());
        float longitud = Float.parseFloat(editTextLongitud.getText().toString());

        Map<String, Object> centro = new HashMap<>();
        centro.put(KEY_NOMBRE, nombre);
        centro.put(KEY_TELEFONO, telefono);
        centro.put(KEY_DIRECCION, direccion);
        centro.put(KEY_LATITUD, latitud);
        centro.put(KEY_LONGITUD, longitud);
        centro.put(KEY_DIAS, diasSelecionados); // Asumiendo que 'diasSeleccionados' está definido y contiene los días seleccionados
        centro.put(KEY_HORA_APERTURA, horaAperturaCentro);
        centro.put(KEY_HORA_CIERRE, horaCierreCentro);
        centro.put(KEY_ESTADO, true);

        db.collection("centros").add(centro) // Utiliza 'add()' en lugar de 'document("Otro centro").set()'
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String centroId = documentReference.getId(); // Obtiene el ID del nuevo documento
                        Toast.makeText(getActivity(), "Registro de centro exitoso, ID: " + centroId, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al registrar centro", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void clearFields(){
        editTextNombre.setText("");
        editTextTelefono.setText("");
        editTextDireccion.setText("");
        editTextLatitud.setText("");
        editTextLongitud.setText("");
        btnDiasCentro.setText("");
        btnHoraApertura.setText("");
        btnHoraCierre.setText("");
    }
}
