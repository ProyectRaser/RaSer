package com.example.todolist;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class TareaAdapter extends ArrayAdapter<Tarea> {

    private final Activity context;
    private final List<Tarea> tareas;

    public TareaAdapter(Activity context, List<Tarea> tareas) {
        super(context, R.layout.tareas, tareas);
        this.context = context;
        this.tareas = tareas;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.tareas, parent, false);
        }

        Tarea tarea = tareas.get(position);

        TextView nombreView = rowView.findViewById(R.id.textViewTarea);
        TextView estadoView = rowView.findViewById(R.id.textViewEstado);
        TextView prioridadView = rowView.findViewById(R.id.textViewPrioridad);
        TextView lenguajeView = rowView.findViewById(R.id.textViewLenguaje);
        TextView tipoView = rowView.findViewById(R.id.textViewTipo);
        TextView entornoView = rowView.findViewById(R.id.textViewEntorno);

        nombreView.setText(tarea.nombre);
        estadoView.setText("Estado: " + tarea.estado);
        prioridadView.setText("Prioridad: " + tarea.prioridad);
        lenguajeView.setText("Lenguaje: " + tarea.lenguaje);
        tipoView.setText("Tipo: " + tarea.tipo);
        entornoView.setText("Entorno: " + tarea.entorno);

        // 🎨 Color según prioridad
        switch (tarea.prioridad.toLowerCase()) {
            case "alta":
                prioridadView.setTextColor(Color.RED);
                break;
            case "media":
                prioridadView.setTextColor(Color.rgb(255, 165, 0)); // naranja
                break;
            case "baja":
                prioridadView.setTextColor(Color.GREEN);
                break;
            default:
                prioridadView.setTextColor(Color.BLACK);
        }

        // 🎨 Color según estado
        switch (tarea.estado.toLowerCase()) {
            case "to do":
                estadoView.setTextColor(Color.BLUE);
                break;
            case "in progress":
                estadoView.setTextColor(Color.rgb(255, 193, 7)); // amarillo
                break;
            case "testing":
                estadoView.setTextColor(Color.MAGENTA); // violeta
                break;
            case "deployed":
                estadoView.setTextColor(Color.rgb(255, 140, 0)); // naranja oscuro
                break;
            case "done":
                estadoView.setTextColor(Color.parseColor("#4CAF50")); // verde
                break;
            default:
                estadoView.setTextColor(Color.DKGRAY);
        }

        return rowView;
    }
}
