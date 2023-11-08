package com.example.greencarson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
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

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class NavigationFragment extends Fragment implements View.OnClickListener {
    View view;
    Boolean mapSelected = true; // true: mapa, false: lista
    MapView mapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8;
    private Marker currentLocationMarker;
    ArrayList<OverlayItem> items;
    OverlayItem overlayItem2;
    GeoPoint user;
    ImageButton btnCenterMap;
    ImageButton seleccionarVista;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializa la configuración de osmdroid
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        // Infla el diseño del fragmento
        view = inflater.inflate(R.layout.fragment_navigation, container, false);
        // Obtiene una referencia al MapView y el boton
        mapView = view.findViewById(R.id.mapView);
        btnCenterMap = view.findViewById(R.id.btnCenterMap);
        // Configura la vista del mapa
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Configura la ubicación inicial y el nivel de zoom
        GeoPoint startPoint = new GeoPoint(19.0414, -98.2063); // Coordenadas de Ciudad de Puebla
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(13.5);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Crear un ArrayList "items" para almacenar los marcadores
        items = new ArrayList<>();

        // Habilita la capa de ubicación
        mapView.getOverlayManager().getTilesOverlay().setEnabled(true);

        // Ejemplo de marcador
        GeoPoint poi1 = new GeoPoint(19.0414, -98.2064); // Coordenadas de un establecimiento
        OverlayItem overlayItem1 = new OverlayItem("Nombre del establecimiento", "Descripción", poi1);
        overlayItem1.setMarker(getResources().getDrawable(R.drawable.marker_icon)); // Personaliza el icono del marcador
        items.add(overlayItem1);

        // Define las coordenadas geográficas de los límites máximos y mínimos (latitud y longitud)
        double maxLat = 19.2; // Latitud máxima
        double minLat = 18.85; // Latitud mínima
        double minLon = -98.4; // Longitud mínima
        double maxLon = -98.0; // Longitud máxima

        // Crea un objeto BoundingBox y se establecen los límites definidos
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLon, minLat, minLon);
        mapView.setScrollableAreaLimitDouble(boundingBox);

        // Crear una capa de superposición de marcadores, y se agrega al mapa
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(items, null, requireContext());
        mapView.getOverlays().add(mOverlay);
        // Actualizar el mapa
        mapView.invalidate();

        // Comprueba si tienes permiso de ubicación, Si no tienes permiso, solicítalo
        ImageView searchIcon = null;
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Si ya tienes permiso, obtén y muestra la ubicación actual
            mostrarUbicacionActual();
            searchIcon = view.findViewById(androidx.appcompat.R.id.search_button);

            mapView.getController().setCenter(poi1);
            mapView.getController().setZoom(13.5);
        }

        btnCenterMap.setOnClickListener(this);

        // To change color of close button, use:
        // ImageView searchCloseIcon = (ImageView)searchView
        //        .findViewById(androidx.appcompat.R.id.search_close_btn);
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.lightGreen),
                android.graphics.PorterDuff.Mode.SRC_IN);

        // Referencia al boton para cambiar vista
        seleccionarVista = view.findViewById(R.id.seleccionarVista);
        seleccionarVista.setOnClickListener(this);
        view.findViewById(R.id.includeLista).setVisibility(View.GONE);
        seleccionarVista.setImageResource(R.drawable.baseline_view_list_24);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCenterMap) {
            mapView.getController().setCenter(user);
            mapView.getController().setZoom(13.5);
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
                // Permiso de ubicación otorgado, obtén y muestra la ubicación actual
                mostrarUbicacionActual();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void mostrarUbicacionActual() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // Verifica si tienes permiso de ubicación antes de registrar el LocationListener
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Tienes permiso, configura un LocationListener para recibir actualizaciones de ubicación
            @SuppressLint("UseCompatLoadingForDrawables") LocationListener locationListener = location -> {
                // Se llama cuando se obtiene una nueva ubicación
                double latitud = location.getLatitude();
                double longitud = location.getLongitude();
                user = new GeoPoint(latitud, longitud);

                // Elimina el marcador anterior antes de agregar uno nuevo
                if (currentLocationMarker != null) {
                    mapView.getOverlays().remove(currentLocationMarker);
                }
                // Agrega el nuevo marcador
                currentLocationMarker = new Marker(mapView);
                currentLocationMarker.setPosition(user);
                currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

                // Establece un título para el Marker
                currentLocationMarker.setTitle("Ubicación actual");

                // Agrega información adicional al Marker (opcional)
                currentLocationMarker.setSnippet("Latitud: " + latitud + ", Longitud: " + longitud);

                overlayItem2 = new OverlayItem("Ubicación actual", "Hola", user);
                overlayItem2.setMarker(getResources().getDrawable(R.drawable.marker_icon)); // Personaliza el icono del marcador
                items.add(overlayItem2);

                mapView.getOverlays().add(currentLocationMarker);

                mapView.invalidate();
            };

            // Registra el LocationListener para actualizaciones de ubicación
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // No tienes permiso, debes solicitar permiso al usuario
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}