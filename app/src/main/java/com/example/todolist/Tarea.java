package com.example.todolist;

public class Tarea {

    public String id;
    public String nombre;
    public String estado;
    public String prioridad;
    public String lenguaje;
    public String tipo;
    public String entorno;

    public Tarea(){}

    public Tarea(String id, String nombre, String estado, String prioridad, String lenguaje, String tipo, String entorno) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.prioridad = prioridad;
        this.lenguaje = lenguaje;
        this.tipo = tipo;
        this.entorno = entorno;
    }

}
