package com.example.greencarson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import java.util.ArrayList;

public class NavigationFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inicializa la configuración de osmdroid
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        // Infla el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        // Obtiene una referencia al MapView
        MapView mapView = view.findViewById(R.id.mapView);
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
        ArrayList<OverlayItem> items = new ArrayList<>();

        // Habilita la capa de ubicación
        mapView.getOverlayManager().getTilesOverlay().setEnabled(true);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        /*if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                GeoPoint currentLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                Marker currentLocationMarker = new Marker(mapView);
                currentLocationMarker.setPosition(currentLocation);
                mapView.getOverlays().add(currentLocationMarker);
            }
        }*/

        // Ejemplo de marcador
        GeoPoint poi1 = new GeoPoint(19.0414, -98.2064); // Coordenadas de un establecimiento
        OverlayItem overlayItem1 = new OverlayItem("Nombre del establecimiento", "Descripción", poi1);
        overlayItem1.setMarker(getResources().getDrawable(R.drawable.marker_icon)); // Personaliza el icono del marcador
        items.add(overlayItem1);

        // Crear una capa de superposición de marcadores
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(items, null, getContext());
        // Agregar la capa de superposición al mapa
        mapView.getOverlays().add(mOverlay);
        // Actualizar el mapa
        mapView.invalidate();



        return view;
    }
}