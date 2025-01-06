package com.example.dados;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ResultadosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        TextView txtPodio = findViewById(R.id.txtPodio);
        LinearLayout containerListaJugadores = findViewById(R.id.containerListaJugadores);
        Button btnVolverAJugar = findViewById(R.id.btnVolverAJugar);
        Button btnSalirResultados = findViewById(R.id.btnSalirResultados);

        // Obtener resultados y lista de jugadores desde el Intent
        Intent intent = getIntent();
        ArrayList<String> resultados = intent.getStringArrayListExtra("resultados");
        ArrayList<String> jugadores = intent.getStringArrayListExtra("jugadores");

        if (resultados != null && !resultados.isEmpty()) {
            // Ordenar resultados por puntuación
            resultados.sort((r1, r2) -> {
                try {
                    int puntos1 = Integer.parseInt(r1.split(": ")[1].split(" ")[0]);
                    int puntos2 = Integer.parseInt(r2.split(": ")[1].split(" ")[0]);
                    return puntos2 - puntos1;
                } catch (Exception e) {
                    Log.e("ResultadosActivity", "Error al ordenar resultados: " + e.getMessage());
                    return 0;
                }
            });

            // Guardar los resultados en SharedPreferences
            guardarResultadosEnSharedPreferences(resultados);

            // Mostrar el podio
            StringBuilder podio = new StringBuilder();
            for (int i = 0; i < resultados.size(); i++) {
                podio.append(i + 1).append(". ").append(resultados.get(i)).append("\n");
            }
            txtPodio.setText(podio.toString());
        }

        // Botón para volver a jugar
        btnVolverAJugar.setOnClickListener(view -> {
            if (resultados != null && jugadores != null && !resultados.isEmpty()) {
                // Obtener el ganador (primer jugador en "resultados")
                String ganador = resultados.get(0).split(": ")[0];

                // Reorganizar la lista de jugadores en sentido contrario a las agujas del reloj
                ArrayList<String> nuevaListaJugadores = reorganizarJugadores(jugadores, ganador);

                // Enviar la nueva lista a JuegoActivity
                Intent volverIntent = new Intent(ResultadosActivity.this, JuegoActivity.class);
                volverIntent.putStringArrayListExtra("jugadoresSeleccionados", nuevaListaJugadores);
                startActivity(volverIntent);
                finish();
            }
        });

        // Botón para salir
        btnSalirResultados.setOnClickListener(view -> {
            Intent volverIntent = new Intent(ResultadosActivity.this, MainActivity.class);
            startActivity(volverIntent);
            finish();
        });
    }

    private void guardarResultadosEnSharedPreferences(ArrayList<String> resultados) {
        SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (String resultado : resultados) {
            String[] partes = resultado.split(": ");
            String nombreJugador = partes[0];
            int puntajeActual = Integer.parseInt(partes[1].split(" ")[0]);

            // Obtener puntaje existente
            int puntajeGuardado = sharedPreferences.getInt(nombreJugador, 0);

            // Actualizar si el nuevo puntaje es mayor
            if (puntajeActual > puntajeGuardado) {
                editor.putInt(nombreJugador, puntajeActual);
            }
        }

        editor.apply();
    }

    private ArrayList<String> reorganizarJugadores(ArrayList<String> jugadores, String ganador) {
        ArrayList<String> nuevaLista = new ArrayList<>();
        int indexGanador = jugadores.indexOf(ganador);

        if (indexGanador != -1) {
            // Añadir jugadores desde el ganador hasta el final de la lista
            nuevaLista.addAll(jugadores.subList(indexGanador, jugadores.size()));

            // Añadir jugadores desde el principio hasta el ganador
            nuevaLista.addAll(jugadores.subList(0, indexGanador));
        } else {
            Log.e("ResultadosActivity", "El ganador no se encuentra en la lista de jugadores.");
        }

        return nuevaLista;
    }
}
