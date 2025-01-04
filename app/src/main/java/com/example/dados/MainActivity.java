package com.example.dados;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/** @noinspection deprecation*/
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnJugar = findViewById(R.id.btnJugar);
        Button btnOpciones = findViewById(R.id.btnOpciones);
        Button btnSalir = findViewById(R.id.btnSalir);

        btnJugar.setOnClickListener(view -> {
            // Lanzamos OpcionesActivity para que el usuario ingrese los jugadores
            Intent intent = new Intent(MainActivity.this, OpcionesActivity.class);
            startActivityForResult(intent, 1); // Usamos startActivityForResult para recibir datos de vuelta
        });

        btnOpciones.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OpcionesActivity.class);
            startActivity(intent);
        });

        btnSalir.setOnClickListener(view -> finishAffinity());
    }

    // Este método maneja la respuesta que nos manda OpcionesActivity cuando el usuario guarda
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Recibimos los datos de los jugadores
            ArrayList<String> nombresJugadores = data.getStringArrayListExtra("nombresJugadores");

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

    // Manejar el botón Atrás
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // Cierra todas las actividades y finaliza la aplicación
    }
}
