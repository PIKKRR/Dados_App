package com.example.dados;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OpcionesActivity extends AppCompatActivity {
    private LinearLayout containerNombres;
    private final ArrayList<EditText> nombreInputs = new ArrayList<>();
    private int numJugadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        Spinner spinnerJugadores = findViewById(R.id.spinnerJugadores);
        containerNombres = findViewById(R.id.containerNombres);
        Button btnGuardarOpciones = findViewById(R.id.btnGuardarOpciones);

        // Configurar el Spinner con opciones de número de jugadores (2 a 20).
        ArrayList<Integer> opcionesJugadores = new ArrayList<>();
        for (int i = 2; i <= 20; i++) {
            opcionesJugadores.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                opcionesJugadores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJugadores.setAdapter(adapter);

        // Listener para actualizar los campos de nombres según el número de jugadores.
        spinnerJugadores.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                numJugadores = (int) parent.getItemAtPosition(position);
                generarCamposDeNombres(numJugadores);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Guardar las opciones y volver al menú principal.
        btnGuardarOpciones.setOnClickListener(view -> {
            ArrayList<String> nombresJugadores = new ArrayList<>();
            for (EditText input : nombreInputs) {
                nombresJugadores.add(input.getText().toString().trim());
            }

            // Guardar los datos en SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("jugadores", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Guardar el número de jugadores y sus nombres
            editor.putInt("numJugadores", numJugadores);
            for (int i = 0; i < nombreInputs.size(); i++) {
                editor.putString("jugador_" + i, nombreInputs.get(i).getText().toString().trim());
            }

            // Guardar los cambios
            editor.apply();

            // Pasar los datos a la actividad principal
            Intent intent = new Intent();
            intent.putExtra("numJugadores", numJugadores);
            intent.putStringArrayListExtra("nombresJugadores", nombresJugadores);
            setResult(RESULT_OK, intent);

            // Finalizar la actividad
            finish();
        });
    }

    private void generarCamposDeNombres(int numJugadores) {
        containerNombres.removeAllViews();
        nombreInputs.clear();

        for (int i = 0; i < numJugadores; i++) {
            EditText editText = new EditText(this);
            editText.setHint("Nombre del jugador " + (i + 1));
            editText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            containerNombres.addView(editText);
            nombreInputs.add(editText);
        }
    }
}