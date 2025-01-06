package com.example.dados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> opcionesLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnJugar = findViewById(R.id.btnJugar);
        Button btnSalir = findViewById(R.id.btnSalir);

        // Registrar el lanzador para recibir resultados de OpcionesActivity
        opcionesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> nombresJugadores = result.getData().getStringArrayListExtra("nombresJugadores");

                        // Verificar que no esté vacío y pasar los datos a JuegoActivity
                        if (nombresJugadores != null && !nombresJugadores.isEmpty()) {
                            Intent intent = new Intent(MainActivity.this, JuegoActivity.class);
                            intent.putStringArrayListExtra("nombresJugadores", nombresJugadores); // Enviar los nombres de los jugadores
                            startActivity(intent); // Lanzar JuegoActivity con los datos
                        } else {
                            Toast.makeText(MainActivity.this, "No hay jugadores disponibles", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        btnJugar.setOnClickListener(view -> {
            // Lanzamos OpcionesActivity para que el usuario ingrese los jugadores
            Intent intent = new Intent(MainActivity.this, OpcionesActivity.class);
            opcionesLauncher.launch(intent); // Llamamos al lanzador en lugar de startActivityForResult
        });

        btnSalir.setOnClickListener(view -> finishAffinity());

        // Manejar el botón Atrás usando OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Acción personalizada al presionar el botón Atrás
                finishAffinity(); // Cierra todas las actividades y finaliza la aplicación
            }
        });

        // Botón para ver el Top 15
        Button btnVerTop15 = findViewById(R.id.btnVerTop15);

        btnVerTop15.setOnClickListener(view -> {
            // Llamamos a la actividad que mostrará el Top 15
            Intent intent = new Intent(MainActivity.this, Top15Activity.class);
            startActivity(intent); // Lanzamos la actividad
        });
    }
}