package com.example.dados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResultadosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        TextView txtPodio = findViewById(R.id.txtPodio);
        LinearLayout containerListaJugadores = findViewById(R.id.containerListaJugadores);
        Button btnVolverAJugar = findViewById(R.id.btnVolverAJugar);
        Button btnSalirResultados = findViewById(R.id.btnSalirResultados);

        // Obtener resultados desde el Intent.
        Intent intent = getIntent();
        ArrayList<String> resultados = intent.getStringArrayListExtra("resultados");

        if (resultados != null && !resultados.isEmpty()) {
            // Ordenar resultados por puntuación.
            Collections.sort(resultados, new Comparator<String>() {
                @Override
                public int compare(String r1, String r2) {
                    int puntos1 = Integer.parseInt(r1.split(": ")[1].split(" ")[0]);
                    int puntos2 = Integer.parseInt(r2.split(": ")[1].split(" ")[0]);
                    return puntos2 - puntos1; // Orden descendente.
                }
            });

            // Mostrar los tres primeros en el podio.
            StringBuilder podio = new StringBuilder();
            for (int i = 0; i < Math.min(3, resultados.size()); i++) {
                podio.append(i + 1).append(". ").append(resultados.get(i)).append("\n");
            }
            txtPodio.setText(podio.toString());

            // Mostrar el resto de jugadores en una lista.
            for (int i = 3; i < resultados.size(); i++) {
                TextView jugador = new TextView(this);
                jugador.setText(resultados.get(i));
                jugador.setTextSize(16f);
                containerListaJugadores.addView(jugador);
            }
        }

        // Botón para volver a jugar.
        btnVolverAJugar.setOnClickListener(view -> {
            Intent volverIntent = new Intent(ResultadosActivity.this, JuegoActivity.class);
            startActivity(volverIntent);
            finish();
        });

        // Botón para salir.
        btnSalirResultados.setOnClickListener(view -> {
            Intent volverIntent = new Intent(ResultadosActivity.this, MainActivity.class);
            startActivity(volverIntent);
            finish();
        });
    }
}