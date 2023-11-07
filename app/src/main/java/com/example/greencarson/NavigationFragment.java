package com.example.greencarson;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Objects;

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
        requireActivity().getSystemService(Context.LOCATION_SERVICE);

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
        ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(items, null, requireContext());
        // Agregar la capa de superposición al mapa
        mapView.getOverlays().add(mOverlay);
        // Actualizar el mapa
        mapView.invalidate();

        ImageView searchIcon=
                view.findViewById(androidx.appcompat.R.id.search_button);

        // To change color of close button, use:
        // ImageView searchCloseIcon = (ImageView)searchView
        //        .findViewById(androidx.appcompat.R.id.search_close_btn);

        searchIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.lightGreen),
                android.graphics.PorterDuff.Mode.SRC_IN);

        return view;
    }
}