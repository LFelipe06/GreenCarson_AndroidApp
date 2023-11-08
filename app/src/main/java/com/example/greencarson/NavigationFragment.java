package com.example.greencarson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class NavigationFragment extends Fragment {

    MapView mapView;
    private ImageButton btnCenterMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 8;
    private Marker currentLocationMarker;
    ArrayList<OverlayItem> items;
    OverlayItem overlayItem1;
    OverlayItem overlayItem2;
    GeoPoint user;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializa la configuración de osmdroid
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        // Infla el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        btnCenterMap = view.findViewById(R.id.btnCenterMap);
        // Obtiene una referencia al MapView
        mapView = view.findViewById(R.id.mapView);
        // Configura la vista del mapa
        mapView.setMultiTouchControls(true);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Crear un ArrayList "items" para almacenar los marcadores
        items = new ArrayList<>();

        // Habilita la capa de ubicación
        mapView.getOverlayManager().getTilesOverlay().setEnabled(true);

        // Ejemplo de marcador
        GeoPoint poi1 = new GeoPoint(19.0414, -98.2064); // Coordenadas de un establecimiento
        overlayItem1 = new OverlayItem("Nombre del establecimiento", "Descripción", poi1);
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
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Si ya tienes permiso, obtén y muestra la ubicación actual
            mostrarUbicacionActual();

            mapView.getController().setCenter(poi1);
            mapView.getController().setZoom(13.5);
        }

        btnCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getController().setCenter(user);
                mapView.getController().setZoom(13.5);
            }
        });

        return view;
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