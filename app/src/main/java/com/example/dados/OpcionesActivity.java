package com.example.dados;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class OpcionesActivity extends AppCompatActivity {

    private static final int REQUEST_NUEVO_JUGADOR = 1; // Código de solicitud
    private LinearLayout containerNombres;
    private final ArrayList<Spinner> spinnerInputs = new ArrayList<>();
    private int numJugadores;
    private ActivityResultLauncher<Intent> listaJugadoresLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        Spinner spinnerJugadores = findViewById(R.id.spinnerJugadores);
        containerNombres = findViewById(R.id.containerNombres);
        Button btnGuardarOpciones = findViewById(R.id.btnGuardarOpciones);
        Button btnListaJugadores = findViewById(R.id.btnListaJugadores);

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

        // Listener para actualizar los campos de selección de jugadores según el número de jugadores.
        spinnerJugadores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numJugadores = (int) parent.getItemAtPosition(position);
                generarCamposDeJugadores(numJugadores);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Guardar las opciones y pasar los datos a JuegoActivity.
        btnGuardarOpciones.setOnClickListener(view -> {
            ArrayList<String> jugadoresSeleccionados = new ArrayList<>();
            for (Spinner spinner : spinnerInputs) {
                String jugadorSeleccionado = (String) spinner.getSelectedItem();
                if (!jugadorSeleccionado.equals("Selecciona un jugador...")) {
                    jugadoresSeleccionados.add(jugadorSeleccionado);
                }
            }

            // Validar si todos los campos están llenos
            if (jugadoresSeleccionados.size() < numJugadores) {
                Toast.makeText(this, "Necesitas llenar todos los cupos de jugadores.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar si no se ha seleccionado ningún jugador
            if (jugadoresSeleccionados.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona los jugadores para jugar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que no haya jugadores duplicados
            if (!validarJugadoresUnicos(jugadoresSeleccionados)) {
                Toast.makeText(this, "No se puede incluir dos veces al mismo jugador.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar los datos en SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("jugadores", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Guardar el número de jugadores y los jugadores seleccionados
            editor.putInt("numJugadores", numJugadores);
            for (int i = 0; i < spinnerInputs.size(); i++) {
                editor.putString("jugador_" + i, (String) spinnerInputs.get(i).getSelectedItem());
            }

            // Guardar los cambios
            editor.apply();

            // Pasar los datos a la actividad JuegoActivity
            Intent intent = new Intent(OpcionesActivity.this, JuegoActivity.class);
            intent.putStringArrayListExtra("jugadoresSeleccionados", jugadoresSeleccionados);

            startActivity(intent);
            finish();
        });

        // Acción para abrir la lista de jugadores
        btnListaJugadores.setOnClickListener(view -> {
            Intent intent = new Intent(OpcionesActivity.this, ListaJugadoresActivity.class);
            startActivityForResult(intent, REQUEST_NUEVO_JUGADOR);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_NUEVO_JUGADOR && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("jugadoresConPuntajes")) {
                // Recibe el HashMap con los jugadores y sus puntajes
                HashMap<String, Integer> jugadoresConPuntajes =
                        (HashMap<String, Integer>) data.getSerializableExtra("jugadoresConPuntajes");

                // Actualizar SharedPreferences con los nuevos datos
                SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Limpia los datos antiguos
                for (String nombre : jugadoresConPuntajes.keySet()) {
                    editor.putInt(nombre, jugadoresConPuntajes.get(nombre)); // Mantén puntajes existentes
                }
                editor.apply();

                // Actualizar los campos de selección de jugadores
                generarCamposDeJugadores(numJugadores);
                Toast.makeText(this, "Lista de jugadores y puntajes actualizada.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void generarCamposDeJugadores(int numJugadores) {
        containerNombres.removeAllViews();
        spinnerInputs.clear();

        SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);
        ArrayList<String> jugadoresGuardados = new ArrayList<>(sharedPreferences.getAll().keySet());

        for (int i = 0; i < numJugadores; i++) {
            // Crear el texto con "Jugador X:"
            TextView textViewJugador = new TextView(this);
            textViewJugador.setText("Jugador " + (i + 1) + ":");
            textViewJugador.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            containerNombres.addView(textViewJugador);

            // Crear el Spinner con la lista de jugadores guardados
            Spinner spinnerJugador = new Spinner(this);
            ArrayList<String> jugadoresConPlaceholder = new ArrayList<>();
            jugadoresConPlaceholder.add("Selecciona un jugador...");
            jugadoresConPlaceholder.addAll(jugadoresGuardados);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    jugadoresConPlaceholder);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerJugador.setAdapter(spinnerAdapter);

            // Añadir el spinner al layout
            spinnerJugador.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            containerNombres.addView(spinnerJugador);
            spinnerInputs.add(spinnerJugador);
        }
    }


    private boolean validarJugadoresUnicos(ArrayList<String> jugadoresSeleccionados) {
        HashSet<String> jugadoresUnicos = new HashSet<>(jugadoresSeleccionados);
        return jugadoresUnicos.size() == jugadoresSeleccionados.size();
    }
}