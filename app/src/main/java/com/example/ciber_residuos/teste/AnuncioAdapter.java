package com.example.ciber_residuos.teste;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ciber_residuos.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private final ArrayList<Anuncio> anuncios;

    public AnuncioAdapter(ArrayList<Anuncio> anuncios) {
        this.anuncios = anuncios;
    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio, parent, false);
        return new AnuncioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
        Anuncio anuncio = anuncios.get(position);

        holder.txtNome.setText(anuncio.getNome());
        holder.txtValor.setText("R$ " + anuncio.getValor());
        holder.txtDescricao.setText(anuncio.getDescricao());
        holder.imgAnuncio.setImageBitmap(anuncio.getImagem());

        // Configurando o clique no item
        holder.itemView.setOnClickListener(v -> {
            // Salvar a imagem no cache
            String fileName = "anuncio_" + position + ".png";
            File cacheDir = v.getContext().getCacheDir();
            File file = new File(cacheDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                anuncio.getImagem().compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Criar Intent e passar os dados
            Intent intent = new Intent(v.getContext(), DetalhesAnuncioActivity.class);
            intent.putExtra("nome", anuncio.getNome());
            intent.putExtra("valor", anuncio.getValor());
            intent.putExtra("descricao", anuncio.getDescricao());
            intent.putExtra("imagemUri", file.getAbsolutePath());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtValor, txtDescricao;
        ImageView imgAnuncio;

        public AnuncioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
            imgAnuncio = itemView.findViewById(R.id.imgAnuncio);
        }
    }
}
