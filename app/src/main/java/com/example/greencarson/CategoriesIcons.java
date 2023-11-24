package com.example.greencarson;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.HashMap;

public class CategoriesIcons {
    private final HashMap<String, Drawable> categories;
    private final Resources res;

    public CategoriesIcons(Resources res) {
        this.res = res;
        categories = new HashMap<>();
        // inicializar datos
        categories.put("Punto verde", scaleDrawable(R.drawable.campana));
        categories.put("Acopio", scaleDrawable(R.drawable.acopio));
        categories.put("Acciones municipales", scaleDrawable(R.drawable.municipales));
        categories.put("Compra-venta", scaleDrawable(R.drawable.compra_venta));
        categories.put("Estación de carga", scaleDrawable(R.drawable.estacion_carga));
        categories.put("Membresía", scaleDrawable(R.drawable.membresia));
        categories.put("Donaciones", scaleDrawable(R.drawable.donaciones));
        categories.put("Reparación", scaleDrawable(R.drawable.reparacion));
    }

    private  Drawable scaleDrawable(int id){
        int size = 120;
        Bitmap bMap = BitmapFactory.decodeResource(res, id);
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, size, size, true);
        return new BitmapDrawable(res, bMapScaled);
    }

    public Drawable getIcon(String name) {
        return categories.get(name);
    }
}
