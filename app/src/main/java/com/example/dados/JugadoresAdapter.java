package com.example.dados;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class JugadoresAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> jugadores;
    private LayoutInflater inflater;

    public JugadoresAdapter(Context context, ArrayList<HashMap<String, String>> jugadores) {
        this.context = context;
        this.jugadores = jugadores;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return jugadores.size();
    }

    @Override
    public Object getItem(int position) {
        return jugadores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fila_jugador, null);
        }

        HashMap<String, String> jugador = jugadores.get(position);

        TextView textNombre = convertView.findViewById(R.id.textNombre);
        TextView textPuntaje = convertView.findViewById(R.id.textPuntaje);
        ImageView iconEditar = convertView.findViewById(R.id.iconEditar);
        ImageView iconEliminar = convertView.findViewById(R.id.iconEliminar);

        textNombre.setText(jugador.get("nombre"));
        textPuntaje.setText(jugador.get("puntaje"));

        // Acción para editar
        iconEditar.setOnClickListener(v -> {
            String nombreJugador = jugador.get("nombre");
            ((ListaJugadoresActivity) context).editarJugador(nombreJugador);
        });

        // Acción para eliminar
        iconEliminar.setOnClickListener(v -> {
            String nombreJugador = jugador.get("nombre");
            ((ListaJugadoresActivity) context).eliminarJugador(nombreJugador);
        });

        return convertView;
    }
}