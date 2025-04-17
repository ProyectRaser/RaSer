package com.example.todolist;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CompletarPerfilActivity extends AppCompatActivity {

    EditText editNombre, editAlias;
    ImageView imageView;
    Button btnGuardar, btnSelecionarFoto;

    String uid, email, nombre;

    FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imagenSeleccionadaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_perfil);

        editNombre = findViewById(R.id.editNombre);
        editAlias = findViewById(R.id.editAlias);
        imageView = findViewById(R.id.imagenPerfil);
        btnGuardar = findViewById(R.id.botonGuardarPerfil);
        btnSelecionarFoto = findViewById(R.id.btnSeleccionarFoto);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        email = intent.getStringExtra("email");
        nombre = intent.getStringExtra("nombre");

        editNombre.setText(nombre);

        db.collection("Usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String urlFoto = documentSnapshot.getString("foto");
                        if (urlFoto != null && !urlFoto.isEmpty()) {
                            Glide.with(this).load(urlFoto).circleCrop().into(imageView);
                        }
                    }
                });

        btnGuardar.setOnClickListener(v -> guardarPerfil());

        verificarPermisos();

        btnSelecionarFoto.setOnClickListener(v -> abrirGaleria());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagenSeleccionadaUri = data.getData();
            Glide.with(this).load(imagenSeleccionadaUri).circleCrop().into(imageView);
        }
    }

    private void verificarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1001);
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            }
        }
    }

    private void guardarPerfil() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String alias = editAlias.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Log.e("UPLOAD", "Usuario no autenticado");
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenSeleccionadaUri != null) {
            String extension = getContentResolver().getType(imagenSeleccionadaUri);
            extension = extension != null ? extension.replace("image/", "") : "jpg";
            String nombreArchivo = "fotos_perfil/" + System.currentTimeMillis() + "." + extension;

            StorageReference ref = FirebaseStorage.getInstance("gs://raser-a5816.firebasestorage.app")
                    .getReference()
                    .child(nombreArchivo);

            Log.d("UPLOAD", "Subiendo imagen a: " + nombreArchivo + " con URI: " + imagenSeleccionadaUri);

            ref.putFile(imagenSeleccionadaUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("UPLOAD", "Subida exitosa");

                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String urlFoto = uri.toString();
                                    Log.d("UPLOAD", "URL obtenida: " + urlFoto);
                                    guardarEnFirestore(userId, nuevoNombre, alias, urlFoto);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("UPLOAD", "Error al obtener URL: " + e.getMessage(), e);
                                    Toast.makeText(this, "Error al obtener URL", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UPLOAD", "Error al subir imagen: " + e.getMessage(), e);
                        Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_LONG).show();
                    });

        } else {
            guardarEnFirestore(userId, nuevoNombre, alias, "");
        }
    }


    private void guardarEnFirestore(String uid, String nombre, String alias, String urlFoto) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("uid", uid);
        datos.put("nombre", nombre);
        datos.put("email", email);
        datos.put("foto", urlFoto);
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
