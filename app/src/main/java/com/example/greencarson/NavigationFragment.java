package com.example.greencarson;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class NavigationFragment extends Fragment implements View.OnClickListener, FilterChangeInterface {
    CategoriesIcons categoriesIcons = CategoriesIcons.getInstance();
    ArrayList<OverlayItem> overlayItemsCentros;
    Map<String, ArrayList<CenterItem>> categorizedCenters;
    BottomSheetBehavior bottomSheetBehavior;
    LocationManager locationManager;
    @SuppressLint("UseCompatLoadingForDrawables")
    LocationListener locationListener;
    View view;
    Boolean mapSelected = true; // true: mapa, false: lista
    MapView mapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8;
    Marker currentLocationMarker;

    OverlayItem overlayItemUbicacion;
    OverlayItem overlayItemCentro;
    GeoPoint user;
    ImageButton btnCenterMap;
    ImageButton seleccionarVista;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<CenterItem> centers;
    ArrayList<CenterItem> filteredCenters;
    ArrayList<CenterItem> finalCenters;
    ArrayList<Item> categorias;

    Set<String> filters;
    androidx.appcompat.widget.SearchView searchView;

    CenterListAdapter centerListAdapter;
    FilterSelectionAdapter filterSelectionAdapter;

    ItemizedIconOverlay<OverlayItem> centersOverlay;
    ItemizedIconOverlay<OverlayItem> ubicacion;



    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla el diseño del fragmento
        view = inflater.inflate(R.layout.fragment_navigation, container, false);
        // Variable para guardar los centros
        centers = new ArrayList<>();
        finalCenters = new ArrayList<>(centers);
        filters = new HashSet<>();
        categorizedCenters = new HashMap<>();
        configureFilterList();
        // Llena la lista de mapas con los filtros que pueda haber
        fetch();
        // Inicialización del mapa
        initMap();

        // Referencia al boton para cambiar vista
        seleccionarVista = view.findViewById(R.id.seleccionarVista);
        seleccionarVista.setOnClickListener(this);
        view.findViewById(R.id.includeLista).setVisibility(View.GONE);
        seleccionarVista.setImageResource(R.drawable.baseline_view_list_24);

        // Bottom sheet
        MaterialCardView filterContainer = view.findViewById(R.id.filterContainer);
        bottomSheetBehavior = BottomSheetBehavior.from(filterContainer);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Search bar
        searchView = view.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchByWord(newText);
                return true;
            }
        });

        return view;
    }

    public void filterCenters() {
        if (filters.size() == 0) {
            finalCenters = new ArrayList<>(centers);
        } else {
            filteredCenters = new ArrayList<>();
            for (String filter : filters) {
                if (categorizedCenters.containsKey(filter)) {
                    filteredCenters.addAll(Objects.requireNonNull(categorizedCenters.get(filter)));
                }
            }
            finalCenters = new ArrayList<>(filteredCenters);
        }
        configureList(finalCenters);
        configureMap(finalCenters);
    }

    private void searchByWord(String text) {
        ArrayList<CenterItem> original;
        finalCenters.clear();
        if (filters.size() == 0) {
            original = centers;
        } else {
            original = filteredCenters;
        }
        if (text.isEmpty()) {
            finalCenters.addAll(original);
        } else {
            text = text.toLowerCase();
            for (CenterItem item : original) {
                if (item.getNombre().toLowerCase().contains(text)) {
                    finalCenters.add(item);
                }
            }
        }
        configureList(finalCenters);
        configureMap(finalCenters);
    }

    private void fetch() {
        //[START OF QUERY]
        db.collection("centros")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CenterItem center = document.toObject(CenterItem.class);
                            center.setId(document.getId());
                            centers.add(center);
                            ArrayList<CenterItem> itemList = categorizedCenters.computeIfAbsent(center.getCategoria(), k -> new ArrayList<>());
                            itemList.add(center);
                        }
                        filterCenters();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        //[END OF QUERY]
    }

    private void configureList(ArrayList<CenterItem> centers) {
        centerListAdapter = new CenterListAdapter(centers);
        RecyclerView recyclerView = view.findViewById(R.id.centrosLista);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(centerListAdapter);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void configureMap(ArrayList<CenterItem> centers) {
        mapView.getOverlays().clear();
        mapView.invalidate();
        overlayItemsCentros = new ArrayList<>();
        mapView.getOverlayManager().getTilesOverlay().setEnabled(true);

        for (CenterItem centro : centers) {
            GeoPoint geoPointCentro = new GeoPoint(centro.getLatitud(), centro.getLongitud());
            overlayItemCentro = new OverlayItem(centro.getNombre(), centro.getDireccion(), geoPointCentro);
            Drawable dr = getResources().getDrawable(categoriesIcons.getIcon(centro.getCategoria()));
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
            int size = 50;
            Drawable icon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
            overlayItemCentro.setMarker(icon);
            overlayItemsCentros.add(overlayItemCentro);
        }

        centersOverlay = new ItemizedIconOverlay<>(overlayItemsCentros,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        showMarkerInfo(item.getTitle(), item.getSnippet());
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        return false;
                    }
                }, requireContext());

        mapView.getOverlays().add(centersOverlay);
        mapView.invalidate();
    }

    private void showMarkerInfo(String title, String snippet) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(snippet)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void configureFilterList() {
        categorias = new ArrayList<>();
        //[START OF QUERY]
        db.collection("categorias")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            categorias.add(new Item(document.getId(), Objects.requireNonNull(document.getData().get("imageUrl")).toString()));
                            filterSelectionAdapter = new FilterSelectionAdapter(categorias, filters, this);
                            RecyclerView recyclerView = view.findViewById(R.id.filterRecyclerView);
                            GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 3);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(filterSelectionAdapter);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        //[END OF QUERY]
    }

    private void initMap() {
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        mapView = view.findViewById(R.id.mapView);
        btnCenterMap = view.findViewById(R.id.btnCenterMap);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(19.0414, -98.2063);
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(13.5);

        double maxLat = 19.2;
        double minLat = 18.85;
        double minLon = -98.4;
        double maxLon = -98.0;
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLon, minLat, minLon);
        mapView.setScrollableAreaLimitDouble(boundingBox);

        mapView.invalidate();

        ImageView searchIcon = null;
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mostrarUbicacionActual();
            searchIcon = view.findViewById(androidx.appcompat.R.id.search_button);
            mapView.getController().setCenter(user);
            mapView.getController().setZoom(13.5);
        }

        btnCenterMap.setOnClickListener(this);

        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.lightGreen),
                android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCenterMap) {
            mapView.getController().setCenter(user);
            mapView.getController().setZoom(18);
        } else if (v.getId() == R.id.seleccionarVista) {
            mapSelected = !mapSelected;
            changeView();
        }
    }

    private void changeView() {
        if (mapSelected) {
            seleccionarVista.setImageResource(R.drawable.baseline_view_list_24);
            view.findViewById(R.id.includeMapa).setVisibility(View.VISIBLE);
            view.findViewById(R.id.includeLista).setVisibility(View.GONE);
        } else {
            seleccionarVista.setImageResource(R.drawable.baseline_map_24);
            view.findViewById(R.id.includeMapa).setVisibility(View.GONE);
            view.findViewById(R.id.includeLista).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mostrarUbicacionActual();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void mostrarUbicacionActual() {
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationListener = location -> {
                double latitud = location.getLatitude();
                double longitud = location.getLongitude();
                user = new GeoPoint(latitud, longitud);

                if (currentLocationMarker != null) {
                    mapView.getOverlays().remove(currentLocationMarker);
                }
                currentLocationMarker = new Marker(mapView);
                currentLocationMarker.setPosition(user);
                currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

                currentLocationMarker.setTitle("Ubicación actual");
                currentLocationMarker.setSnippet("Latitud: " + latitud + ", Longitud: " + longitud);

                overlayItemUbicacion = new OverlayItem("Ubicación actual", "Hola", user);
                //overlayItemUbicacion.setMarker(getResources().getDrawable(R.drawable.marker_icon));
                try {
                    overlayItemsCentros.add(overlayItemUbicacion);
                } catch (Exception e) {
                    // Captura otras excepciones no manejadas
                    Log.e("Error", "Excepción no manejada: " + e.getMessage());
                } finally {
                    // Código que se ejecutará siempre, independientemente de si se lanzó una excepción o no
                    Log.d("Final", "Este bloque siempre se ejecuta");
                }


                mapView.getOverlays().add(currentLocationMarker);
                mapView.invalidate();
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
