package com.example.ciber_residuos.teste;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciber_residuos.R;

public class MeusAnunciosEnviadosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios_enviados);

        RecyclerView recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setAdapter(new AnuncioAdapter(RegistrarAnuncioActivity.anuncios));
    }
}
