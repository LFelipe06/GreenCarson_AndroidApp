package com.example.greencarson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

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
        GeoPoint startPoint = new GeoPoint(40.7128, -74.0060); // Coordenadas de Nueva York
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(12);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Habilita la capa de ubicación
        mapView.getOverlayManager().getTilesOverlay().setEnabled(true);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        /*Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation != null) {
            GeoPoint currentLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            Marker currentLocationMarker = new Marker(mapView);
            currentLocationMarker.setPosition(currentLocation);
            mapView.getOverlays().add(currentLocationMarker);
        }*/

        return view;
    }
}