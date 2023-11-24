package com.example.greencarson;

import java.util.HashMap;

public class CategoriesIcons {
    private static CategoriesIcons instance;
    private final HashMap<String, Integer> categories;

    private CategoriesIcons() {
        categories = new HashMap<>();
        // inicializar datos
        categories.put("Punto verde", R.drawable.campana);
        categories.put("Acopio", R.drawable.acopio);
        categories.put("Acciones municipales", R.drawable.municipales);
        categories.put("Compra-venta", R.drawable.compra_venta);
        categories.put("Estación de carga", R.drawable.estacion_carga);
        categories.put("Membresía", R.drawable.membresia);
        categories.put("Donaciones", R.drawable.donaciones);
        categories.put("Reparación", R.drawable.reparacion);

    }

    public static synchronized CategoriesIcons getInstance() {
        if (instance == null) {
            instance = new CategoriesIcons();
        }
        return instance;
    }

    /** @noinspection DataFlowIssue*/
    public int getIcon(String name) {
        return categories.getOrDefault(name, R.drawable.acopio);
    }
}
