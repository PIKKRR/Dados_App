package com.example.dados;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ListaJugadoresActivity extends AppCompatActivity {

    private ListView listViewJugadores;
    private Button btnNuevoJugador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_jugadores);

        listViewJugadores = findViewById(R.id.listViewJugadores);
        btnNuevoJugador = findViewById(R.id.btnNuevoJugador);

        // Obtener la lista de jugadores
        actualizarLista();

        // Listener para el botón "Nuevo Jugador"
        btnNuevoJugador.setOnClickListener(v -> mostrarDialogoNuevoJugador());
    }

    private ArrayList<HashMap<String, String>> obtenerJugadores() {
        ArrayList<HashMap<String, String>> jugadores = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);

        // Obtener todos los jugadores y sus puntajes
        for (String nombreJugador : sharedPreferences.getAll().keySet()) {
            int puntaje = sharedPreferences.getInt(nombreJugador, 0);

            HashMap<String, String> jugador = new HashMap<>();
            jugador.put("nombre", nombreJugador);
            jugador.put("puntaje", String.valueOf(puntaje));
            jugadores.add(jugador);
        }

        return jugadores;
    }

    private void mostrarDialogoNuevoJugador() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editar_jugador, null);
        final EditText editTextNombre = view.findViewById(R.id.editTextNombre);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo Jugador")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, id) -> {
                    String nuevoNombre = editTextNombre.getText().toString().trim();

                    if (!nuevoNombre.isEmpty()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);

                        // Verificar si el jugador ya existe
                        if (!sharedPreferences.contains(nuevoNombre)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(nuevoNombre, 0); // Puntaje inicial
                            editor.apply();
                            actualizarLista(); // Actualizar lista en la lista localmente
                            enviarListaActualizada(); // Actualizar lista y enviar a OpcionesActivity
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void editarJugador(String nombreJugador) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editar_jugador, null);
        final EditText editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextNombre.setText(nombreJugador);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Jugador")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, id) -> {
                    String nuevoNombre = editTextNombre.getText().toString().trim();

                    if (!nuevoNombre.isEmpty() && !nuevoNombre.equals(nombreJugador)) {
                        SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        int puntaje = sharedPreferences.getInt(nombreJugador, 0);
                        editor.remove(nombreJugador); // Remueve la clave antigua
                        editor.putInt(nuevoNombre, puntaje); // Transfiere puntaje al nuevo nombre
                        editor.apply();

                        actualizarLista();
                        enviarListaActualizada();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    public void eliminarJugador(String nombreJugador) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Jugador")
                .setMessage("¿Seguro que quieres eliminar a " + nombreJugador + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Eliminar solo al jugador seleccionado
                    editor.remove(nombreJugador);
                    editor.apply();

                    actualizarLista(); // Actualizar la lista localmente
                    enviarListaActualizada();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void actualizarLista() {
        ArrayList<HashMap<String, String>> jugadores = obtenerJugadores();
        JugadoresAdapter adapter = new JugadoresAdapter(this, jugadores);
        listViewJugadores.setAdapter(adapter);
    }

    private void enviarListaActualizada() {
        SharedPreferences sharedPreferences = getSharedPreferences("puntajes", MODE_PRIVATE);
        ArrayList<HashMap<String, String>> jugadores = obtenerJugadores();
        HashMap<String, Integer> jugadoresConPuntajes = new HashMap<>();

        for (HashMap<String, String> jugador : jugadores) {
            String nombre = jugador.get("nombre");
            int puntaje = sharedPreferences.getInt(nombre, 0);
            jugadoresConPuntajes.put(nombre, puntaje);
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("jugadoresConPuntajes", jugadoresConPuntajes); // Agrega jugadores con puntajes
        setResult(RESULT_OK, resultIntent); // Devuelve el resultado
    }
}