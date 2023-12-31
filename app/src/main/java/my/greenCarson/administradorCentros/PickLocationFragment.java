package my.greenCarson.administradorCentros;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import my.greenCarson.administradorCentros.R;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class PickLocationFragment extends Fragment {
    private MapView mapView;
    private String tag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get arguments
        Bundle args = getArguments();
        assert args != null;
        tag = args.getString("tag");

        View view = inflater.inflate(R.layout.fragment_pick_location, container, false);

        mapView = view.findViewById(R.id.mapView);

        Button btnCapturarUbicacion = view.findViewById(R.id.btnCapturarUbicacion);

        // Configura el mapa y el indicador según tus necesidades
        // Configura la ubicación inicial y el nivel de zoom
        GeoPoint startPoint = new GeoPoint(19.0414, -98.2063); // Coordenadas de Ciudad de Puebla
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(13.5);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        // Configura la vista del mapa

        // Define las coordenadas geográficas de los límites máximos y mínimos (latitud y longitud)
        double maxLat = 19.2; // Latitud máxima
        double minLat = 18.85; // Latitud mínima
        double minLon = -98.4; // Longitud mínima
        double maxLon = -98.0; // Longitud máxima

        // Crea un objeto BoundingBox y se establecen los límites definidos
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLon, minLat, minLon);
        mapView.setScrollableAreaLimitDouble(boundingBox);

        // Configura la ubicación inicial y el nivel de zoom
        mapView.getController().setZoom(13.5);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        btnCapturarUbicacion.setOnClickListener(v -> capturarPosicionCentroMapa());


        return view;
    }

    private void capturarPosicionCentroMapa() {
        if (mapView != null) {
            BoundingBox boundingBox = mapView.getBoundingBox(); // Obtén la caja delimitadora del mapa
            GeoPoint centroMapa = boundingBox.getCenter(); // Obtén el punto central de la caja delimitadora

            double latitud = centroMapa.getLatitude();
            double longitud = centroMapa.getLongitude();
            float lat = (float)latitud;
            float longi = (float)longitud;

            // Ahora puedes usar la latitud y longitud como desees
            // Por ejemplo, mostrarlas en un Toast
            // String mensaje = "Latitud: " + latitud + "\nLongitud: " + longitud;
            // Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            sendDataToSecondFragment(lat, longi);

        }
    }

    private Bundle createFloatBundle(float lat, float longi) {
        Bundle bundle = new Bundle();
        bundle.putFloat("latitud", lat);
        bundle.putFloat("longitud", longi);
        return bundle;
    }

    private void sendDataToSecondFragment(float variable1, float variable2) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Bundle dataBundle = createFloatBundle(variable1, variable2);
        AddCenterFragment secondFragment = (AddCenterFragment) fragmentManager.findFragmentByTag(tag);
        assert secondFragment != null;
        secondFragment.setArguments(dataBundle);
        // Use FragmentManager to replace or add the second fragment
        assert getFragmentManager() != null;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, secondFragment);
        transaction.commit();
    }

}