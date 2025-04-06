package com.example.todolist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CompletarPerfilActivity extends AppCompatActivity {

    EditText editNombre, editAlias;
    ImageView imageView;
    Button btnGuardar;

    String uid, email, nombre, foto;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_perfil);

        editNombre = findViewById(R.id.editNombre);
        editAlias = findViewById(R.id.editAlias);
        imageView = findViewById(R.id.imagenPerfil);
        btnGuardar = findViewById(R.id.botonGuardarPerfil);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        email = intent.getStringExtra("email");
        nombre = intent.getStringExtra("nombre");
        foto = intent.getStringExtra("foto");

        editNombre.setText(nombre);

        if (foto != null && !foto.isEmpty()) {
            Glide.with(this).load(Uri.parse(foto)).circleCrop().into(imageView);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPerfil();
            }
        });
    }

    private void guardarPerfil() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String alias = editAlias.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("uid", uid);
        datos.put("nombre", nuevoNombre);
        datos.put("email", email);
        datos.put("foto", foto);
        datos.put("alias", alias);

        db.collection("Usuarios").document(uid).set(datos)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Perfil completado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar el perfil", Toast.LENGTH_SHORT).show();
                });
    }
}
