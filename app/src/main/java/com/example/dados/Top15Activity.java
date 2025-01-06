package com.example.dados;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Top15Activity extends AppCompatActivity {
    private LinearLayout containerTop15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top15);

        containerTop15 = findViewById(R.id.containerTop15);

        // Obtener el Top15 desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);
        List<Jugador> top15 = obtenerTop15(sharedPreferences);

        // Mostrar el Top15
        mostrarTop15(top15);
    }

    private List<Jugador> obtenerTop15(SharedPreferences sharedPreferences) {
        List<Jugador> jugadores = new ArrayList<>();

        // Cargar todos los jugadores y sus puntajes desde SharedPreferences
        for (String nombre : sharedPreferences.getAll().keySet()) {
            int puntaje = sharedPreferences.getInt(nombre, 0);
            jugadores.add(new Jugador(nombre, puntaje));
        }

        // Ordenar la lista por puntaje en orden descendente
        jugadores.sort((a, b) -> Integer.compare(b.getPuntaje(), a.getPuntaje()));

        // Seleccionar los primeros 15 elementos
        return jugadores.subList(0, Math.min(jugadores.size(), 15));
    }

    private void mostrarTop15(List<Jugador> top15) {
        // Limpiar la lista antes de agregar los resultados
        containerTop15.removeAllViews();

        // Crear un TextView para mostrar cada jugador y su puntaje
        for (int i = 0; i < top15.size(); i++) {
            Jugador jugador = top15.get(i);

            TextView textView = new TextView(this);
            textView.setText((i + 1) + ". " + jugador.getNombre() + ": " + jugador.getPuntaje() + " puntos");
            containerTop15.addView(textView);
        }
    }

    // Clase para representar a un jugador
    private static class Jugador {
        private final String nombre;
        private final int puntaje;

        public Jugador(String nombre, int puntaje) {
            this.nombre = nombre;
            this.puntaje = puntaje;
        }

        public String getNombre() {
            return nombre;
        }

        public int getPuntaje() {
            return puntaje;
        }
    }
}