package com.example.dados;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class JuegoActivity extends AppCompatActivity {
    private LinearLayout containerJugadores;
    private ArrayList<String> nombresJugadores;
    private final HashMap<String, Integer> puntuaciones = new HashMap<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        containerJugadores = findViewById(R.id.containerJugadores);
        Button btnTerminarJuego = findViewById(R.id.btnTerminarJuego);

        // Manejo del botón de retroceso
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Mostrar mensaje cuando el usuario presiona el botón de retroceso
                Toast.makeText(JuegoActivity.this, "No puedes volver atrás sin finalizar la partida.", Toast.LENGTH_SHORT).show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Recuperar los datos pasados desde OpcionesActivity
        Intent intent = getIntent();
        int numJugadores = intent.getIntExtra("numJugadores", 0);
        ArrayList<String> jugadoresSeleccionados = intent.getStringArrayListExtra("jugadoresSeleccionados");

        // Verificar si los jugadores están vacíos o nulos
        if (jugadoresSeleccionados != null && !jugadoresSeleccionados.isEmpty()) {
            Log.d("JuegoActivity", "Jugadores seleccionados: " + jugadoresSeleccionados);
            nombresJugadores = new ArrayList<>(jugadoresSeleccionados);
            generarSeccionesJugadores(nombresJugadores);
        } else {
            Log.e("JuegoActivity", "No se recibieron jugadores o la lista está vacía.");
        }


        // Botón para finalizar el juego y calcular resultados
        btnTerminarJuego.setOnClickListener(view -> {
            ArrayList<String> resultados = new ArrayList<>();
            for (String nombre : nombresJugadores) {
                resultados.add(nombre + ": " + puntuaciones.getOrDefault(nombre, 0) + " puntos");
            }

            // Verificar si algún jugador ha batido el récord
            for (String jugador : nombresJugadores) {
                int puntajeActual = puntuaciones.getOrDefault(jugador, 0);
                if (esNuevoRecord(jugador, puntajeActual)) {
                    mostrarNotificacionRecord(jugador);
                    actualizarTop15(jugador, puntajeActual);
                }
            }

            // Enviar resultados a la pantalla de resultados
            Intent resultadoIntent = new Intent(JuegoActivity.this, ResultadosActivity.class);
            resultadoIntent.putStringArrayListExtra("resultados", resultados);
            resultadoIntent.putStringArrayListExtra("jugadores", nombresJugadores);
            startActivity(resultadoIntent);
            finish();
        });
    }

    private boolean esNuevoRecord(String jugador, int puntajeActual) {
        SharedPreferences sharedPreferences = getSharedPreferences("top15", MODE_PRIVATE);
        ArrayList<HashMap<String, Integer>> top15 = obtenerTop15(sharedPreferences);

        // Verificar si el puntaje del jugador supera el mínimo en el top15
        for (HashMap<String, Integer> entry : top15) {
            for (String key : entry.keySet()) {
                int puntajeTop = entry.get(key);
                if (puntajeActual > puntajeTop) {
                    return true; // Es un nuevo récord
                }
            }
        }
        return false;
    }

    private void mostrarNotificacionRecord(String jugador) {
        Toast.makeText(JuegoActivity.this, "¡Jugador " + jugador + " ha batido récord de puntos!", Toast.LENGTH_LONG).show();
    }

    private void actualizarTop15(String jugador, int puntaje) {
        SharedPreferences sharedPreferences = getSharedPreferences("top15", MODE_PRIVATE);
        ArrayList<HashMap<String, Integer>> top15 = obtenerTop15(sharedPreferences);

        // Agregar el nuevo jugador si su puntaje es mayor que alguno en el top
        top15.add(new HashMap<String, Integer>() {{
            put(jugador, puntaje);
        }});

        // Ordenar por puntaje (descendente)
        Collections.sort(top15, (a, b) -> b.values().iterator().next() - a.values().iterator().next());

        // Mantener solo los 15 mejores puntajes
        if (top15.size() > 15) {
            top15 = new ArrayList<>(top15.subList(0, 15));
        }

        // Guardar el nuevo top15
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < top15.size(); i++) {
            String jugadorTop = (String) top15.get(i).keySet().toArray()[0];
            int puntajeTop = top15.get(i).get(jugadorTop);
            editor.putInt(jugadorTop, puntajeTop);
        }
        editor.apply();
    }

    private ArrayList<HashMap<String, Integer>> obtenerTop15(SharedPreferences sharedPreferences) {
        ArrayList<HashMap<String, Integer>> top15 = new ArrayList<>();

        // Cargar el top15 desde SharedPreferences
        for (String jugador : sharedPreferences.getAll().keySet()) {
            int puntaje = sharedPreferences.getInt(jugador, 0);
            HashMap<String, Integer> map = new HashMap<>();
            map.put(jugador, puntaje);
            top15.add(map);
        }

        return top15;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Guardar el estado de las puntuaciones antes de que se refresque el activity
        for (String nombre : nombresJugadores) {
            int puntaje = puntuaciones.getOrDefault(nombre, 0);
            outState.putInt(nombre, puntaje);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restaurar el estado de las puntuaciones
        for (String nombre : nombresJugadores) {
            if (savedInstanceState.containsKey(nombre)) {
                int puntaje = savedInstanceState.getInt(nombre, 0);
                puntuaciones.put(nombre, puntaje);
            }
        }

        // Actualizar las vistas con los puntajes restaurados
        actualizarPuntajesEnPantalla();
    }

    @SuppressLint("SetTextI18n")
    private void actualizarPuntajesEnPantalla() {
        for (int i = 0; i < containerJugadores.getChildCount(); i++) {
            LinearLayout seccionJugador = (LinearLayout) containerJugadores.getChildAt(i);
            TextView puntajeTotal = (TextView) seccionJugador.getChildAt(seccionJugador.getChildCount() - 1);

            String nombreJugador = nombresJugadores.get(i);
            int totalPuntos = puntuaciones.getOrDefault(nombreJugador, 0);

            // Actualizar el TextView de puntaje total
            puntajeTotal.setText("Total Puntos: " + totalPuntos);
        }
    }

    @SuppressLint("SetTextI18n")
    private void generarSeccionesJugadores(ArrayList<String> nombres) {
        int[] imagenes = {
                R.drawable.icon_as,
                R.drawable.icon_k,
                R.drawable.icon_q,
                R.drawable.icon_j,
                R.drawable.icon_rojas,
                R.drawable.icon_negras
        };

        int[] multiplicadores = {6, 5, 4, 3, 2, 1};

        for (String nombre : nombres) {
            // Crear un LinearLayout con un fondo de borde
            LinearLayout seccionJugador = new LinearLayout(this);
            seccionJugador.setOrientation(LinearLayout.VERTICAL);
            seccionJugador.setPadding(8, 8, 8, 8);
            seccionJugador.setBackgroundResource(R.drawable.border); // Aplicar el borde

            // Agregamos el nombre del jugador al inicio de la sección
            TextView titulo = new TextView(this);
            titulo.setText("Jugador: " + nombre);
            titulo.setTextSize(18f);
            seccionJugador.addView(titulo);

            // Controles para puntos
            EditText[] puntosInputs = new EditText[imagenes.length];

            for (int i = 0; i < imagenes.length; i++) {
                LinearLayout fila = new LinearLayout(this);
                fila.setOrientation(LinearLayout.HORIZONTAL);
                fila.setGravity(Gravity.CENTER_VERTICAL); // Centra verticalmente los elementos

                // Configuración del ImageView (icono)
                ImageView icono = new ImageView(this);
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                        48, 48 // Tamaño fijo para mantener consistencia
                );
                iconParams.setMargins(8, 8, 8, 8);
                icono.setLayoutParams(iconParams);
                icono.setImageResource(imagenes[i]);
                icono.setAdjustViewBounds(true);
                icono.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                // Configuración del EditText (campo de puntos)
                EditText input = new EditText(this);
                input.setHint("Introduce la puntuación...");
                input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 2 // Peso flexible para adaptarse
                );
                input.setLayoutParams(inputParams);
                input.setGravity(Gravity.CENTER_VERTICAL); // Centra texto en el campo

                puntosInputs[i] = input;

                // Agregar los elementos a la fila
                fila.addView(icono);
                fila.addView(input);
                seccionJugador.addView(fila);
            }

            // Campo para mostrar puntaje total
            TextView puntajeTotal = new TextView(this);
            puntajeTotal.setText("Total Puntos: 0");
            puntajeTotal.setTextSize(16f);

            seccionJugador.addView(puntajeTotal);

            // Actualización dinámica del puntaje
            for (EditText input : puntosInputs) {
                input.addTextChangedListener(new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int total = 0;
                        for (int j = 0; j < puntosInputs.length; j++) {
                            String valor = puntosInputs[j].getText().toString();
                            int cantidad = valor.isEmpty() ? 0 : Integer.parseInt(valor);
                            total += cantidad * multiplicadores[j];
                        }

                        puntajeTotal.setText("Total Puntos: " + total);
                        puntuaciones.put(nombre, total); // Almacena el puntaje total
                    }

                    @Override
                    public void afterTextChanged(android.text.Editable s) {
                    }
                });
            }

            containerJugadores.addView(seccionJugador);
        }
    }
}