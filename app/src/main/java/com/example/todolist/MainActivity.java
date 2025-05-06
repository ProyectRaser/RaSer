package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        int id = item.getItemId();

        if (id == R.id.add) {
            mostrarDialogoNuevaTarea();
            return true;
        } else if (id == R.id.out) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return true;
        } else if (id == R.id.perfil) {
            Intent intent = new Intent(MainActivity.this, CompletarPerfilActivity.class);
            intent.putExtra("uid", idUser);
            intent.putExtra("email", mAuth.getCurrentUser().getEmail());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






    private void mostrarDialogoNuevaTarea() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        Spinner spinnerEstado = crearSpinner(new String[]{"Estado", "To Do", "In Progress", "Testing", "Deployed", "Done"}, "Estado", layout);
        Spinner spinnerPrioridad = crearSpinner(new String[]{"Prioridad", "Alta", "Media", "Baja"}, "Prioridad", layout);
        Spinner spinnerLenguaje = crearSpinner(new String[]{"Lenguaje", "Java", "Python", "YAML", "Terraform", "Otro"}, "Lenguaje", layout);
        Spinner spinnerTipo = crearSpinner(new String[]{"Tipo", "CI/CD", "Infraestructura", "Monitorización", "Bug", "Feature"}, "Tipo", layout);
        Spinner spinnerEntorno = crearSpinner(new String[]{"Entorno", "Dev", "Staging", "Prod", "Minikube"}, "Entorno", layout);

        final EditText tarea = new EditText(this);
        tarea.setHint("Nombre de la tarea");
        layout.addView(tarea);


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nueva Tarea")
                .setMessage("Introduce los detalles de la nueva tarea")
                .setView(layout)
                .setPositiveButton("Añadir", (dialogInterface, i) -> {
                    String nuevaTarea = tarea.getText().toString().trim();

                    if (nuevaTarea.isEmpty()) {
                        Toast.makeText(MainActivity.this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("nombreTarea", nuevaTarea);
                    data.put("estado", spinnerEstado.getSelectedItem().toString());
                    data.put("prioridad", spinnerPrioridad.getSelectedItem().toString());
                    data.put("lenguaje", spinnerLenguaje.getSelectedItem().toString());
                    data.put("tipo", spinnerTipo.getSelectedItem().toString());
                    data.put("entorno", spinnerEntorno.getSelectedItem().toString());
                    data.put("usuario", idUser);

                    db.collection("Tareas")
                            .add(data)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("FIRESTORE", "Tarea creada con ID: " + documentReference.getId());
                                actualizarUI();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FIRESTORE", "Error al crear tarea", e);
                                Toast.makeText(MainActivity.this, "Error al crear la tarea: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();
    }

    private void mostrarDialogoEdicion(Tarea tarea) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        Spinner spinnerEstado = crearSpinner(new String[]{"Estado", "To Do", "In Progress", "Testing", "Deployed", "Done"}, "Estado", layout);
        Spinner spinnerPrioridad = crearSpinner(new String[]{"Prioridad", "Alta", "Media", "Baja"}, "Prioridad", layout);
        Spinner spinnerLenguaje = crearSpinner(new String[]{"Lenguaje", "Java", "Python", "YAML", "Terraform", "Otro"}, "Lenguaje", layout);
        Spinner spinnerTipo = crearSpinner(new String[]{"Tipo", "CI/CD", "Infraestructura", "Monitorización", "Bug", "Feature"}, "Tipo", layout);
        Spinner spinnerEntorno = crearSpinner(new String[]{"Entorno", "Dev", "Staging", "Prod", "Minikube"}, "Entorno", layout);

        final EditText inputNombre = new EditText(this);
        inputNombre.setText(tarea.nombre);
        layout.addView(inputNombre);


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
                            .addOnSuccessListener(aVoid -> {
                                Log.d("FIRESTORE", "Tarea actualizada");
                                actualizarUI();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FIRESTORE", "Error al actualizar tarea", e);
                                Toast.makeText(MainActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private Spinner crearSpinner(String[] opcionesConTitulo, String seleccion, LinearLayout layout) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesConTitulo);
        spinner.setAdapter(adapter);
        int index = java.util.Arrays.asList(opcionesConTitulo).indexOf(seleccion);
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
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error al obtener tareas", e));
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

    public void borrarTarea(View view) {
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.textViewTarea);
        String nombre = tareaTextView.getText().toString();

        for (Tarea tarea : listaTareas) {
            if (tarea.nombre.equals(nombre)) {
                db.collection("Tareas").document(tarea.id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FIRESTORE", "Tarea eliminada");
                            Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                            actualizarUI();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FIRESTORE", "Error al eliminar tarea", e);
                            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                        });
                break;
            }
        }
    }

    public void entrarperfil(View view) {
        Intent intent = new Intent(MainActivity.this, CompletarPerfilActivity.class);
        intent.putExtra("uid", idUser); // importante si lo usas dentro
        intent.putExtra("email", mAuth.getCurrentUser().getEmail());
        intent.putExtra("nombre", ""); // puedes precargar el nombre si lo tienes
        startActivity(intent);
    }

}