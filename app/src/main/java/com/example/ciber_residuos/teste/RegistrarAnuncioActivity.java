package com.example.ciber_residuos.teste;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ciber_residuos.R;

import java.util.ArrayList;

public class RegistrarAnuncioActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private ImageView currentImageView;
    private Bitmap currentBitmap;
    private EditText edtNome, edtValor, edtDescricao;
    public static ArrayList<Anuncio> anuncios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_anuncio);

        ImageView image1 = findViewById(R.id.image1);
        edtNome = findViewById(R.id.edtNome);
        edtValor = findViewById(R.id.edtValor);
        edtDescricao = findViewById(R.id.edtDescricao);
        Button btnEnviar = findViewById(R.id.btnEnviar);

        image1.setOnClickListener(v -> openGallery(image1));

        btnEnviar.setOnClickListener(v -> {
            if (currentBitmap == null || edtNome.getText().toString().isEmpty() ||
                    edtValor.getText().toString().isEmpty() || edtDescricao.getText().toString().isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos e selecione uma imagem!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Criar e salvar o anúncio
            Anuncio anuncio = new Anuncio(
                    edtNome.getText().toString(),
                    edtValor.getText().toString(),
                    edtDescricao.getText().toString(),
                    currentBitmap
            );
            anuncios.add(anuncio);

            Toast.makeText(this, "Anúncio enviado!", Toast.LENGTH_SHORT).show();

            // Abrir nova Activity
            Intent intent = new Intent(this, MeusAnunciosEnviadosActivity.class);
            startActivity(intent);
        });
    }

    private void openGallery(ImageView imageView) {
        currentImageView = imageView;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri imageUri = data.getData();
            try {
                currentBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                currentImageView.setImageBitmap(currentBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
