package com.example.todolist;

import org.junit.Before;
import org.junit.Test;
import java.util.List;


import static org.junit.Assert.*;

public class FakeTaskRepositoryTest {

    private FakeTaskRepository repo;

    @Before
    public void setUp() {
        repo = new FakeTaskRepository();
    }

    @Test
    public void testAgregarTarea() {
        Tarea tarea = new Tarea("1", "Tarea de prueba", "To Do", "Alta", "Java", "Feature", "Dev");
        repo.agregarTarea(tarea);

        List<Tarea> tareas = repo.obtenerTareas();
        assertEquals(1, tareas.size());
        assertEquals("Tarea de prueba", tareas.get(0).nombre);
    }

    @Test
    public void testFiltrarTareasPorEstado() {
        repo.agregarTarea(new Tarea("1", "Tarea 1", "Done", "Media", "Python", "Bug", "Staging"));
        repo.agregarTarea(new Tarea("2", "Tarea 2", "To Do", "Alta", "Java", "Feature", "Dev"));

        List<Tarea> toDo = repo.obtenerTareasPorEstado("To Do");
        assertEquals(1, toDo.size());
        assertEquals("Tarea 2", toDo.get(0).nombre);
    }

    @Test
    public void testEliminarTareaPorNombre() {
        repo.agregarTarea(new Tarea("1", "Tarea eliminar", "Done", "Alta", "Java", "Bug", "Dev"));
        repo.eliminarTareaPorNombre("Tarea eliminar");

        List<Tarea> tareas = repo.obtenerTareas();
        assertTrue(tareas.isEmpty());
    }
}

