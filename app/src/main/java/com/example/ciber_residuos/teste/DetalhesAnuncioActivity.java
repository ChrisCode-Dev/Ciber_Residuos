package com.example.ciber_residuos.teste;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ciber_residuos.R;

import java.io.File;

public class DetalhesAnuncioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_anuncio);

        ImageView imgDetalhe = findViewById(R.id.imgDetalhe);
        TextView txtNomeDetalhe = findViewById(R.id.txtNomeDetalhe);
        TextView txtValorDetalhe = findViewById(R.id.txtValorDetalhe);
        TextView txtDescricaoDetalhe = findViewById(R.id.txtDescricaoDetalhe);

        // Recuperar os dados passados
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nome = extras.getString("nome", "Sem Nome");
            String valor = extras.getString("valor", "0.00");
            String descricao = extras.getString("descricao", "Sem Descrição");
            String imagemUri = extras.getString("imagemUri");

            // Exibir os dados nos componentes
            txtNomeDetalhe.setText(nome);
            txtValorDetalhe.setText("R$ " + valor);
            txtDescricaoDetalhe.setText(descricao);

            // Carregar a imagem a partir do URI
            if (imagemUri != null) {
                File imgFile = new File(imagemUri);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgDetalhe.setImageBitmap(bitmap);
                }
            }
        }
    }
}
