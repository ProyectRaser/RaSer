package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String idUser;

    ListView listViewTareas;
    List<Tarea> listaTareas = new ArrayList<>();
    TareaAdapter adapterTareas;

    Spinner spinnerEstadoFiltro, spinnerPrioridadFiltro, spinnerLenguajeFiltro, spinnerTipoFiltro, spinnerEntornoFiltro;
    String estadoSeleccionado = "Todos";
    String prioridadSeleccionada = "Todas";
    String lenguajeSeleccionado = "Todos";
    String tipoSeleccionado = "Todos";
    String entornoSeleccionado = "Todos";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            idUser = mAuth.getCurrentUser().getUid();
        } else {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        listViewTareas = findViewById(R.id.tarea);

        spinnerEstadoFiltro = findViewById(R.id.spinnerEstadoFiltro);
        spinnerPrioridadFiltro = findViewById(R.id.spinnerPrioridadFiltro);
        spinnerLenguajeFiltro = findViewById(R.id.spinnerLenguajeFiltro);
        spinnerTipoFiltro = findViewById(R.id.spinnerTipoFiltro);
        spinnerEntornoFiltro = findViewById(R.id.spinnerEntornoFiltro);

        String[] estados = {"Todos", "To Do", "In Progress", "Testing", "Deployed", "Done"};
        String[] prioridades = {"Todas", "Alta", "Media", "Baja"};
        String[] lenguajes = {"Todos", "Java", "Python", "YAML", "Terraform", "Otro"};
        String[] tipos = {"Todos", "CI/CD", "Infraestructura", "Monitorización", "Bug", "Feature"};
        String[] entornos = {"Todos", "Dev", "Staging", "Prod", "Minikube"};

        spinnerEstadoFiltro.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados));
        spinnerPrioridadFiltro.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, prioridades));
        spinnerLenguajeFiltro.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lenguajes));
        spinnerTipoFiltro.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos));
        spinnerEntornoFiltro.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, entornos));

        spinnerEstadoFiltro.setOnItemSelectedListener(filtroListener(val -> { estadoSeleccionado = val; actualizarUI(); }));
        spinnerPrioridadFiltro.setOnItemSelectedListener(filtroListener(val -> { prioridadSeleccionada = val; actualizarUI(); }));
        spinnerLenguajeFiltro.setOnItemSelectedListener(filtroListener(val -> { lenguajeSeleccionado = val; actualizarUI(); }));
        spinnerTipoFiltro.setOnItemSelectedListener(filtroListener(val -> { tipoSeleccionado = val; actualizarUI(); }));
        spinnerEntornoFiltro.setOnItemSelectedListener(filtroListener(val -> { entornoSeleccionado = val; actualizarUI(); }));

        adapterTareas = new TareaAdapter(this, listaTareas);
        listViewTareas.setAdapter(adapterTareas);

        actualizarUI();
    }

    private AdapterView.OnItemSelectedListener filtroListener(Consumer<String> setter) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setter.accept(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            mostrarDialogoNuevaTarea();
            return true;
        } else if (item.getItemId() == R.id.out) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void mostrarDialogoNuevaTarea() {
        final EditText tarea = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nueva Tarea")
                .setMessage("Introduce tu nueva tarea")
                .setView(tarea)
                .setPositiveButton("Añadir", (dialogInterface, i) -> {
                    String nuevaTarea = tarea.getText().toString();

                    if (nuevaTarea.isEmpty()) {
                        Toast.makeText(MainActivity.this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("nombreTarea", nuevaTarea);
                    data.put("estado", "To Do");
                    data.put("prioridad", "Media");
                    data.put("lenguaje", "Otro");
                    data.put("tipo", "Feature");
                    data.put("entorno", "Dev");
                    data.put("usuario", idUser);

                    db.collection("Tareas")
                            .add(data)
                            .addOnSuccessListener(documentReference -> actualizarUI())
                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error al crear la tarea: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();
    }

    private void mostrarDialogoEdicion(Tarea tarea) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputNombre = new EditText(this);
        inputNombre.setText(tarea.nombre);
        layout.addView(inputNombre);

        Spinner spinnerEstado = crearSpinner(new String[]{"To Do", "In Progress", "Testing", "Deployed", "Done"}, tarea.estado, layout);
        Spinner spinnerPrioridad = crearSpinner(new String[]{"Alta", "Media", "Baja"}, tarea.prioridad, layout);
        Spinner spinnerLenguaje = crearSpinner(new String[]{"Java", "Python", "YAML", "Terraform", "Otro"}, tarea.lenguaje, layout);
        Spinner spinnerTipo = crearSpinner(new String[]{"CI/CD", "Infraestructura", "Monitorización", "Bug", "Feature"}, tarea.tipo, layout);
        Spinner spinnerEntorno = crearSpinner(new String[]{"Dev", "Staging", "Prod", "Minikube"}, tarea.entorno, layout);

        new AlertDialog.Builder(this)
                .setTitle("Editar Tarea")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("nombreTarea", inputNombre.getText().toString());
                    data.put("estado", spinnerEstado.getSelectedItem().toString());
                    data.put("prioridad", spinnerPrioridad.getSelectedItem().toString());
                    data.put("lenguaje", spinnerLenguaje.getSelectedItem().toString());
                    data.put("tipo", spinnerTipo.getSelectedItem().toString());
                    data.put("entorno", spinnerEntorno.getSelectedItem().toString());

                    db.collection("Tareas").document(tarea.id).update(data)
                            .addOnSuccessListener(aVoid -> actualizarUI())
                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private Spinner crearSpinner(String[] opciones, String seleccion, LinearLayout layout) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spinner.setAdapter(adapter);
        int index = java.util.Arrays.asList(opciones).indexOf(seleccion);
        if (index >= 0) spinner.setSelection(index);
        layout.addView(spinner);
        return spinner;
    }

    private void actualizarUI() {
        db.collection("Tareas")
                .whereEqualTo("usuario", idUser)
                .get()
                .addOnSuccessListener(value -> {
                    listaTareas.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String id = doc.getId();
                        String nombre = doc.getString("nombreTarea");
                        String estado = doc.getString("estado");
                        String prioridad = doc.getString("prioridad");
                        String lenguaje = doc.getString("lenguaje");
                        String tipo = doc.getString("tipo");
                        String entorno = doc.getString("entorno");

                        if (!estadoSeleccionado.equals("Todos") && !estadoSeleccionado.equals(estado)) continue;
                        if (!prioridadSeleccionada.equals("Todas") && !prioridadSeleccionada.equals(prioridad)) continue;
                        if (!lenguajeSeleccionado.equals("Todos") && !lenguajeSeleccionado.equals(lenguaje)) continue;
                        if (!tipoSeleccionado.equals("Todos") && !tipoSeleccionado.equals(tipo)) continue;
                        if (!entornoSeleccionado.equals("Todos") && !entornoSeleccionado.equals(entorno)) continue;

                        Tarea tarea = new Tarea(id, nombre, estado, prioridad, lenguaje, tipo, entorno);
                        listaTareas.add(tarea);
                    }
                    adapterTareas.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("MainActivity", "Error al obtener tareas", e));
    }

    public void limpiarFiltros(View view) {
        spinnerEstadoFiltro.setSelection(0);
        spinnerPrioridadFiltro.setSelection(0);
        spinnerLenguajeFiltro.setSelection(0);
        spinnerTipoFiltro.setSelection(0);
        spinnerEntornoFiltro.setSelection(0);
    }

    public void editarTarea(View view) {
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.textViewTarea);
        String nombre = tareaTextView.getText().toString();
        for (Tarea tarea : listaTareas) {
            if (tarea.nombre.equals(nombre)) {
                mostrarDialogoEdicion(tarea);
                break;
            }
        }
    }
}
