package my.greenCarson.administradorCentros;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import my.greenCarson.administradorCentros.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class AddCenterFragment extends Fragment {
    // Variables para subir la imagen
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String picturePath = "";
    View view;
    public int hora;
    public int minuto;
    public boolean hourChecker = false; // false para hora de apertura, true para hora de cierre
    private List<String> diasSelecionados;
    private List<String> materialesSeleccionados;
    private static final String TAG = "AddCenterFragment";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_TELEFONO = "num_telefonico";
    private static final String KEY_DIRECCION = "direccion";
    private static final String KEY_LATITUD = "latitud";
    private static final String KEY_LONGITUD = "longitud";
    private static final String KEY_ESTADO = "estado";
    private static final String KEY_DIAS = "dias";
    private static final String KEY_HORA_APERTURA = "hora_apertura";
    private static final String KEY_HORA_CIERRE = "hora_cierre";
    private static final String KEY_IMAGEN = "imagen";
    private static final String KEY_MATERIALES = "materiales";
    private static final String KEY_CATEGORIA = "categoria";
    private EditText editTextNombre;
    private EditText editTextTelefono;
    private EditText editTextDireccion;
    private EditText editTextLatitud;
    private EditText editTextLongitud;
    private Button btnDiasCentro;
    private Button btnHoraApertura;
    private Button btnHoraCierre;
    private Button btnImagen;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int RESULT_LOAD_IMAGE = 1;
    private String nombre;
    private String telefono;
    private String direccion;
    private String horaAperturaCentro = "";
    private String horaCierreCentro = "";
    private String latitud;
    private String longitud;
    private Set<String> activeMaterials;

    MaterialSelectionAdapter materialSelectionAdapter;
    CategorySelectionAdapter categorySelectionAdapter;

    public AddCenterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_center, container, false);

        activeMaterials = new HashSet<>();

        btnHoraApertura = view.findViewById(R.id.btnHoraApertura);
        btnHoraCierre = view.findViewById(R.id.btnHoraCierre);
        btnDiasCentro = view.findViewById(R.id.btnDiasCentro);
        btnImagen = view.findViewById(R.id.btn_imagen);
        Button btnRegistrarCentro = view.findViewById(R.id.btn_actualizar);
        Button btnCancelarRegistro = view.findViewById(R.id.btn_cancelar);
        Button btnSeleccionarUbicacion = view.findViewById(R.id.btnSeleccionarUbicacion);

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

            ReverseGeocodingTask reverseGeocodingTask = new ReverseGeocodingTask(requireActivity(), result -> {
                // El resultado es la dirección obtenida
                if (result != null) {
                    editTextDireccion.setText(result);
                    editTextLatitud.setText(lat);
                    editTextLongitud.setText(longi);
                    Log.d(TAG, "Dirección: " + result);
                } else {
                    Log.e(TAG, "No se pudo obtener la dirección.");
                }
            });

            reverseGeocodingTask.execute((double) variable1, (double) variable2);
        }

        btnSeleccionarUbicacion.setOnClickListener(v -> {
            // Crear e iniciar la transacción del fragmento del mapa
            PickLocationFragment pickLocationFragment= new PickLocationFragment();
            Bundle args = new Bundle();
            args.putString("tag", "AddCenterFragment");
            pickLocationFragment.setArguments(args);
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
        btnImagen.setOnClickListener(v -> imageChooser());
        btnRegistrarCentro.setOnClickListener(v -> {
                    nombre = editTextNombre.getText().toString();
                    telefono = editTextTelefono.getText().toString();
                    direccion = editTextDireccion.getText().toString();
                    latitud = editTextLatitud.getText().toString();
                    longitud = editTextLongitud.getText().toString();
                    llenarArrayMateriales();
                    if (validateData()) {
                        registrarCentro();
                    }
                }
        );

        configureMaterialList();
        configureCategoryList();

        btnCancelarRegistro.setOnClickListener(v -> clearFields());

        return view;
    }

    private boolean validateData() {
        if (Objects.equals(nombre, "")) {
            Toast.makeText(getActivity(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Objects.equals(direccion, "")) {
            Toast.makeText(getActivity(), "La dirección no puede estar vacía", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Objects.equals(horaAperturaCentro, "")) {
            Toast.makeText(getActivity(), "La hora de apertura no puede estar vacía", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Objects.equals(horaCierreCentro, "")) {
            Toast.makeText(getActivity(), "La hora de cierre no puede estar vacía", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Objects.equals(latitud, "")) {
            Toast.makeText(getActivity(), "La latitud no puede estar vacía", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Objects.equals(longitud, "")) {
            Toast.makeText(getActivity(), "La longitud no puede estar vacía", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Objects.equals(picturePath, "")) {
            Toast.makeText(getActivity(), "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (materialesSeleccionados.size() == 0 && !Objects.equals(categorySelectionAdapter.getSelectedCategory(), "Estación de carga")) {
            Toast.makeText(getActivity(), "Selecciona al menos un material", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void imageChooser() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            assert selectedImage != null;
            Cursor cursor = requireContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            btnImagen.setText(picturePath);
            cursor.close();
        }
    }

    public void configureMaterialList(){
        ArrayList<Item> materiales = new ArrayList<>();

        db.collection("materiales")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            materiales.add(new Item(document.getId(), Objects.requireNonNull(document.getData().get("imageUrl")).toString()));
                            // materialSelectiondapter = new MaterialSelectionAdapter(materiales, filters, this);

                        }
                        materialSelectionAdapter = new MaterialSelectionAdapter(materiales, activeMaterials);
                        RecyclerView recyclerView = view.findViewById(R.id.materialRecyclerView);
                        GridLayoutManager layoutManager=new GridLayoutManager(this.getContext(),3);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(materialSelectionAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }

    public void llenarArrayMateriales(){
        materialesSeleccionados = new ArrayList<>();
        materialesSeleccionados.addAll(activeMaterials);
    }

    private void configureCategoryList(){
        ArrayList<Item> categorias = new ArrayList<>();
        //[START OF QUERY]
        db.collection("categorias")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            categorias.add(new Item(document.getId(), Objects.requireNonNull(document.getData().get("imageUrl")).toString()));
                        }
                        categorySelectionAdapter = new CategorySelectionAdapter(categorias);
                        RecyclerView recyclerView = view.findViewById(R.id.categoryRecyclerView);
                        GridLayoutManager layoutManager=new GridLayoutManager(this.getContext(),3);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(categorySelectionAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        //[END OF QUERY]
    }

    // Dialogo para seleccionar los días del centro
    public void CreateAlertDialog(){
        // Array que almacena los días del centro
        diasSelecionados = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccione los días");
        builder.setMultiChoiceItems(R.array.dias, null, (dialog, which, isChecked) -> {
            String[] arr = getResources().getStringArray(R.array.dias);

            if (isChecked) {
                diasSelecionados.add(arr[which]);
            }
            else diasSelecionados.remove(arr[which]);
        });
        builder.setPositiveButton("Show", (dialog, which) -> {
            StringBuilder dias = new StringBuilder();
            for(String item:diasSelecionados){
                dias.append(" ").append(item);
            }
            btnDiasCentro.setText(dias.toString());
        });

        builder.create();
        builder.show();
    }

    public void popTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, selectedHour, selectedMinute) -> {

            @SuppressLint("DefaultLocale") String horaminuto = String.format("%02d:%02d", selectedHour, selectedMinute);
            if(hourChecker){
                // Se actualiza la hora de cierre
                horaCierreCentro = horaminuto;
                btnHoraCierre.setText(horaminuto);
            }else{
                // Se actualiza la hora de apertura
                horaAperturaCentro = horaminuto;
                btnHoraApertura.setText(horaminuto);
            }
            hora = selectedHour;
            minuto = selectedMinute;
        };


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, hora, minuto, true);
        timePickerDialog.setTitle("Seleccione un horario");
        timePickerDialog.show();

    }

    public void registrarCentro() {
        Toast.makeText(getActivity(), "Registrando...", Toast.LENGTH_SHORT).show();

        // Subir imagen
        Uri file = Uri.fromFile(new File(picturePath));
        StorageReference ref = storageRef.child("fotosCentros/"+nombre+file.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(file);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            // Continue with the task to get the download URL
            return ref.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> centro = new HashMap<>();
                centro.put(KEY_NOMBRE, nombre);
                centro.put(KEY_TELEFONO, telefono);
                centro.put(KEY_DIRECCION, direccion);
                centro.put(KEY_LATITUD, Float.parseFloat(latitud));
                centro.put(KEY_LONGITUD, Float.parseFloat(longitud));
                centro.put(KEY_DIAS, diasSelecionados); // Asumiendo que 'diasSeleccionados' está definido y contiene los días seleccionados
                centro.put(KEY_HORA_APERTURA, horaAperturaCentro);
                centro.put(KEY_HORA_CIERRE, horaCierreCentro);
                centro.put(KEY_MATERIALES, materialesSeleccionados);
                centro.put(KEY_CATEGORIA, categorySelectionAdapter.getSelectedCategory());
                centro.put(KEY_ESTADO, true);
                centro.put(KEY_IMAGEN, task.getResult());
                db.collection("centros").add(centro) // Utiliza 'add()' en lugar de 'document("Otro centro").set()'
                        .addOnSuccessListener(documentReference -> {
                            String centroId = documentReference.getId(); // Obtiene el ID del nuevo documento
                            Toast.makeText(getActivity(), "Registro de centro exitoso, ID: " + centroId, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Error al registrar centro", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        });
            } else {
                Toast.makeText(getActivity(), "Error al obtener url de imagen", Toast.LENGTH_SHORT).show();
            }
        });
            }

    private void clearFields(){
        editTextNombre.setText("");
        editTextTelefono.setText("");
        editTextDireccion.setText("");
        editTextLatitud.setText("");
        editTextLongitud.setText("");
        picturePath = "";
        btnImagen.setText(R.string.seleccionar_imagen);
        btnDiasCentro.setText("");
        btnHoraApertura.setText("");
        btnHoraCierre.setText("");
        materialSelectionAdapter.clearSelection();
        categorySelectionAdapter.clearSelection();
    }
}
