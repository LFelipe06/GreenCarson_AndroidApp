package com.example.greencarson;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmationPopUp extends Dialog implements View.OnClickListener {

    private final Activity c;
    private final Runnable onYesClickListener;

    public ConfirmationPopUp(Activity a, String confirmationMessage, Runnable onYesClickListener) {
        super(a);
        this.c = a;
        this.onYesClickListener = onYesClickListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirmation_pop_up);

        // Obtener referencias a los elementos de la interfaz de usuario
        Button yes = findViewById(R.id.btnYes);
        Button no = findViewById(R.id.btnNo);
        TextView confirmationText = findViewById(R.id.confirmationText);

        // Configurar el texto de confirmación
        confirmationText.setText(confirmationMessage);

        // Configurar los listeners de los botones
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnYes) {
            // Llamar a la lógica personalizada al hacer clic en "Yes"
            if (onYesClickListener != null) {
                onYesClickListener.run();
            }
            c.finish();
        } else if (v.getId() == R.id.btnNo) {
            // Descartar el diálogo al hacer clic en "No"
            dismiss();
        }
    }
}