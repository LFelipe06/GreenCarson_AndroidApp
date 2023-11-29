package my.greenCarson.administradorCentros;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import my.greenCarson.administradorCentros.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EditCenterFragment extends Fragment {
    View view;
    private static final int RESULT_LOAD_IMAGE = 1;
    // Variables para subir la imagen
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String picturePath = "";
    public int hora;
    public int minuto;
    public boolean hourChecker = false; // false para hora de apertura, true para hora de cierre
    private ArrayList<String> diasSelecionados;
    private List<String> materialesSeleccionados;
    private String idCentro;
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
    public EditText editTextNombre;
    private EditText editTextTelefono;
    private EditText editTextDireccion;
    private EditText editTextLatitud;
    private EditText editTextLongitud;
    private Button btnDiasCentro;
    private Button btnHoraApertura;
    private Button btnHoraCierre;
    private Button btnImagen;
    CenterItem centerResult;
    private ImageView imageViewImagen;
    private TextView textViewNombre;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_center, container, false);
        activeMaterials = new HashSet<>();
        btnHoraApertura = view.findViewById(R.id.btnHoraApertura);
        btnHoraCierre = view.findViewById(R.id.btnHoraCierre);
        btnDiasCentro = view.findViewById(R.id.btnDiasCentro);
        btnImagen = view.findViewById(R.id.btn_imagen);
        Button btnActualizarCentro = view.findViewById(R.id.btn_actualizar);
        Button btnCancelarRegistro = view.findViewById(R.id.btn_cancelar);
        Button btnSeleccionarUbicacion = view.findViewById(R.id.btnSeleccionarUbicacion);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        ImageButton btnBorrarCentro = view.findViewById(R.id.btnBorrarCentro);

        editTextNombre = view.findViewById(R.id.editTextNombreCentro);
        editTextTelefono = view.findViewById(R.id.editTextTelefonoCentro);
        editTextDireccion = view.findViewById(R.id.editTextDireccionCentro);
        editTextLatitud = view.findViewById(R.id.editTextLatitudCentro);
        editTextLongitud = view.findViewById(R.id.editTextLongitudCentro);
        imageViewImagen = view.findViewById(R.id.imagenCentro);
        textViewNombre = view.findViewById(R.id.nombreTextView);


        Bundle bundle = getArguments();
        if (bundle != null) {
            idCentro = bundle.getString("id");
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

        btnSeleccionarUbicacion.setOnClickListener(v -> {
            // Crear e iniciar la transacción del fragmento del mapa
            PickLocationFragment pickLocationFragment = new PickLocationFragment();
            Bundle args = new Bundle();
            args.putString("tag", "EditCenterFragment");
            pickLocationFragment.setArguments(args);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, pickLocationFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnActualizarCentro.setOnClickListener(v -> {
            nombre = editTextNombre.getText().toString();
            telefono = editTextTelefono.getText().toString();
            direccion = editTextDireccion.getText().toString();
            latitud = editTextLatitud.getText().toString();
            longitud = editTextLongitud.getText().toString();
            llenarArrayMateriales();
            if (validateData()) {
                actualizarCentro();
            }
        });

        btnImagen.setOnClickListener(v -> imageChooser());

        btnCancelarRegistro.setOnClickListener(v -> regresarANav());

        btnBorrarCentro.setOnClickListener(v -> borrarCentro());

        btnBack.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            getFragmentManager().popBackStack();
        });

        // Crear un TaskCompletionSource para cada operación
        TaskCompletionSource<Void> tarea1Completa = new TaskCompletionSource<>();
        TaskCompletionSource<Void> tarea2Completa = new TaskCompletionSource<>();

        // Llamar a la primera función y adjuntar un listener para indicar la finalización
        configureCategoryList().addOnCompleteListener(result -> {
            // Marcar la tarea como completa
            tarea1Completa.setResult(null);
        });

        // Llamar a la segunda función y adjuntar un listener para indicar la finalización
        configureMaterialList().addOnCompleteListener(result -> {
            // Marcar la tarea como completa
            tarea2Completa.setResult(null);
        });

        // Obtener las tareas correspondientes
        Task<Void> tarea1 = tarea1Completa.getTask();
        Task<Void> tarea2 = tarea2Completa.getTask();

        // Ejecutar la tercera función cuando ambas tareas anteriores estén completas
        Task<List<Task<?>>> tareaCombinada = Tasks.whenAllComplete(tarea1, tarea2);
        tareaCombinada.addOnCompleteListener(result -> {
            // Ahora puedes llamar a la tercera función porque las dos primeras han terminado
            getCenterData();
        });

        return view;
    }


    public Task<Void> configureCategoryList() {
        // Crear un TaskCompletionSource para representar la operación asincrónica
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        ArrayList<Item> categorias = new ArrayList<>();
        db.collection("categorias")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            categorias.add(new Item(document.getId(), Objects.requireNonNull(document.getData().get("imageUrl")).toString()));
                        }
                        categorySelectionAdapter = new CategorySelectionAdapter(categorias);
                        RecyclerView recyclerView = view.findViewById(R.id.categoryRecyclerView);
                        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 3);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(categorySelectionAdapter);

                        // Marcar la tarea como completa
                        tcs.setResult(null);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        // Marcar la tarea como completa con error
                        tcs.setException(Objects.requireNonNull(task.getException()));
                    }
                });

        return tcs.getTask();
    }

    public Task<Void> configureMaterialList() {
        // Crear un TaskCompletionSource para representar la operación asincrónica
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        ArrayList<Item> materiales = new ArrayList<>();
        db.collection("materiales")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            materiales.add(new Item(document.getId(), Objects.requireNonNull(document.getData().get("imageUrl")).toString()));
                        }
                        materialSelectionAdapter = new MaterialSelectionAdapter(materiales, activeMaterials);
                        RecyclerView recyclerView = view.findViewById(R.id.materialRecyclerView);
                        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 3);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(materialSelectionAdapter);

                        // Marcar la tarea como completa
                        tcs.setResult(null);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        // Marcar la tarea como completa con error
                        tcs.setException(Objects.requireNonNull(task.getException()));
                    }
                });

        return tcs.getTask();
    }

    public void getCenterData() {
        // Crear un TaskCompletionSource para representar la operación asincrónica
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        db.collection("centros")
                .document(idCentro)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            centerResult = document.toObject(CenterItem.class);
                            assert centerResult != null;
                            showCenterDetails(centerResult);
                            // Marcar la tarea como completa
                            tcs.setResult(null);
                        } else {
                            Log.e(TAG, "No such document");
                            // Marcar la tarea como completa con error
                            tcs.setException(new Exception("No such document"));
                        }
                    } else {
                        Log.e(TAG, "Error getting document: ", task.getException());
                        // Marcar la tarea como completa con error
                        tcs.setException(Objects.requireNonNull(task.getException()));
                    }
                });

        tcs.getTask();
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
        if (materialesSeleccionados.size() == 0) {
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

    // Dialogo para seleccionar los días del centro
    public void CreateAlertDialog() {
        // Array que almacena los días del centro
        diasSelecionados = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccione los días");
        builder.setMultiChoiceItems(R.array.dias, null, (dialog, which, isChecked) -> {
            String[] arr = getResources().getStringArray(R.array.dias);

            if (isChecked) {
                diasSelecionados.add(arr[which]);
            } else diasSelecionados.remove(arr[which]);
        });
        builder.setPositiveButton("Show", (dialog, which) -> {
            StringBuilder dias = new StringBuilder();
            for (String item : diasSelecionados) {
                dias.append(" ").append(item);
            }
            btnDiasCentro.setText(dias.toString());
        });

        builder.create();
        builder.show();
    }

    public void llenarArrayMateriales() {
        materialesSeleccionados = new ArrayList<>();
        materialesSeleccionados.addAll(activeMaterials);
    }


    public void popTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, selectedHour, selectedMinute) -> {

            @SuppressLint("DefaultLocale") String horaminuto = String.format("%02d:%02d", selectedHour, selectedMinute);
            if (hourChecker) {
                // Se actualiza la hora de cierre
                horaCierreCentro = horaminuto;
                btnHoraCierre.setText(horaminuto);
            } else {
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

    public void actualizarCentro() {
        Toast.makeText(getActivity(), "Actualizando...", Toast.LENGTH_SHORT).show();

        if (!Objects.equals(picturePath,"")) {

            // Subir imagen
            Uri file = Uri.fromFile(new File(picturePath));
            StorageReference ref = storageRef.child("fotosCentros/" + nombre + file.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(file);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                   subirDatosActualizados(task.getResult());
                } else {
                    Toast.makeText(getActivity(), "Error al obtener url de imagen", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            subirDatosActualizados(Uri.parse(centerResult.getImagen()));
        }
    }

    public void subirDatosActualizados(Uri url){
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
        centro.put(KEY_IMAGEN, url);
        db.collection("centros").document(idCentro).update(centro)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Actualización de centro exitosa", Toast.LENGTH_SHORT).show();
                    getCenterData();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al actualizar centro", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                });
    }

    public void showCenterDetails(CenterItem centro) {
        if (!Objects.equals(centro.getImagen(), "")) {
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(centro.getImagen()).into(imageViewImagen);
        }
        textViewNombre.setText(centro.getNombre());
        editTextNombre.setText(centro.getNombre());
        editTextTelefono.setText(centro.getNum_telefonico());
        editTextDireccion.setText(centro.getDireccion());
        editTextLatitud.setText(String.valueOf(centro.getLatitud()));
        editTextLongitud.setText(String.valueOf(centro.getLongitud()));
        horaAperturaCentro = centro.getHora_apertura();
        btnHoraApertura.setText(horaAperturaCentro);
        horaCierreCentro = centro.getHora_cierre();
        btnHoraCierre.setText(horaCierreCentro);
        materialSelectionAdapter.setActiveMaterials(new HashSet<>(centro.getMateriales()));
        categorySelectionAdapter.setSelectedCategory(centro.getCategoria());
        StringBuilder dias = new StringBuilder();
        diasSelecionados = centro.getDias();
        for (String item : diasSelecionados) {
            dias.append(" ").append(item);
        }
        btnDiasCentro.setText(dias.toString());
    }

    public void borrarCentro() {
        ConfirmationPopUp confirmationDialog = new ConfirmationPopUp(
                requireActivity(),
                "¿Estás seguro de que deseas eliminar este centro?", () -> {
            db.collection("centros").document(idCentro).delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Se eliminó el centro", Toast.LENGTH_LONG).show();
                        }
                    });
            regresarANav();
        });
        confirmationDialog.show();
        Objects.requireNonNull(confirmationDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void regresarANav() {
        NavigationFragment someFragment = new NavigationFragment();
        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, someFragment);
        transaction.commit();
    }
}