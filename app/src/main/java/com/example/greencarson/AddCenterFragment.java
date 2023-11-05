package com.example.greencarson;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Locale;

import javax.xml.namespace.QName;

public class AddCenterFragment extends Fragment {

    Button btnTiempoApertura;
    Button btnTiempoCierre;
    public int horaApertura, horaCierre, minutoApertura, minutoCierre, hora, minuto;
    public boolean hourChecker = false; // false para hora de apertura, true para hora de cierre

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_center, container, false);

        btnTiempoApertura = view.findViewById(R.id.btnTiempoApertura);
        btnTiempoCierre = view.findViewById(R.id.btnTiempoCierre);

        // Guarda la hora y minuto de apertura desde un dialogo/pop-up de tipo hora
        btnTiempoApertura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hourChecker = false;
                popTimePicker(v);
            }
        });

        // Guarda la hora y minuto de cierre desde un dialogo/pop-up de tipo hora
        btnTiempoCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hourChecker = true;
                popTimePicker(v);
            }
        });

        return view;
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                String horaminuto = String.format("%02d:%02d", selectedHour, selectedMinute);
                if(hourChecker){
                    // Se actualiza la hora de cierre
                    horaCierre = selectedHour;
                    minutoCierre = selectedMinute;
                    btnTiempoCierre.setText(horaminuto);
                }else{
                    // Se actualiza la hora de apertura
                    horaApertura = selectedHour;
                    minutoApertura = selectedMinute;
                    btnTiempoApertura.setText(horaminuto);
                }
                hora = selectedHour;
                minuto = selectedMinute;
            }
        };


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, hora, minuto, true);
        timePickerDialog.setTitle("Seleccione un horario");
        timePickerDialog.show();

    }
}
