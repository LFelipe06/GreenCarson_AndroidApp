package com.example.greencarson;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {

    private static final String TAG = "ReverseGeocodingTask";
    private Context context;
    private OnTaskCompleted listener;

    public ReverseGeocodingTask(Context context, OnTaskCompleted listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Double... params) {
        if (params.length < 2) {
            return null;
        }

        double latitude = params[0];
        double longitude = params[1];

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Obtén la primera línea de la dirección
            }

        } catch (IOException e) {
            Log.e(TAG, "Error al obtener la dirección desde coordenadas", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onTaskCompleted(result);
        }
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }
}
