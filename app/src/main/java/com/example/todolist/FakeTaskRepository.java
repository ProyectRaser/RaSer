package com.example.todolist;

import java.util.List;
import java.util.ArrayList;

public class FakeTaskRepository {
    private final List<Tarea> tareas = new ArrayList<>();

    public void agregarTarea(Tarea tarea) {
        tareas.add(tarea);
    }

    public List<Tarea> obtenerTareas() {
        return new ArrayList<>(tareas);
    }

    public List<Tarea> obtenerTareasPorEstado(String estado) {
        List<Tarea> tareasFiltradas = new ArrayList<>();
        for (Tarea tarea : tareas) {
            if (tarea.estado.equalsIgnoreCase(estado)) {
                tareasFiltradas.add(tarea);
            }
        }
        return tareasFiltradas;
    }

    public void eliminarTareaPorNombre(String nombre) {
        tareas.removeIf(t -> t.nombre.equals(nombre));
    }
}
