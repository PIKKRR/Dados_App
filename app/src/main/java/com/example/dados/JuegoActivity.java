package com.example.dados;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.Gravity;

import java.util.ArrayList;
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

        // Recuperar jugadores desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("jugadores", MODE_PRIVATE);
        int numJugadores = sharedPreferences.getInt("numJugadores", 0);

        if (numJugadores > 0) {
            // Si hay jugadores, recuperarlos.
            nombresJugadores = new ArrayList<>();
            for (int i = 0; i < numJugadores; i++) {
                String nombreJugador = sharedPreferences.getString("jugador_" + i, "");
                if (!nombreJugador.isEmpty()) {
                    nombresJugadores.add(nombreJugador);
                }
            }

            // Generar secciones para cada jugador
            generarSeccionesJugadores(nombresJugadores);
        } else {
            // Si no se encuentran jugadores en SharedPreferences, mostramos un mensaje.
            TextView errorText = new TextView(this);
            errorText.setText("No hay jugadores disponibles.");
            containerJugadores.addView(errorText);
        }

        // Botón para finalizar el juego y calcular resultados
        btnTerminarJuego.setOnClickListener(view -> {
            ArrayList<String> resultados = new ArrayList<>();
            for (String nombre : nombresJugadores) {
                resultados.add(nombre + ": " + puntuaciones.getOrDefault(nombre, 0) + " puntos");
            }

            // Enviar resultados a la pantalla de resultados
            Intent resultadoIntent = new Intent(JuegoActivity.this, ResultadosActivity.class);
            resultadoIntent.putStringArrayListExtra("resultados", resultados);
            startActivity(resultadoIntent);
            finish();
        });
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
                input.setHint("0");
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