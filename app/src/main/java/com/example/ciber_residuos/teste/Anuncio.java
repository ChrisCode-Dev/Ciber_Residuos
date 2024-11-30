package com.example.ciber_residuos.teste;

import android.graphics.Bitmap;

public class Anuncio {
    private String nome;
    private String valor;
    private String descricao;
    private Bitmap imagem;

    public Anuncio(String nome, String valor, String descricao, Bitmap imagem) {
        this.nome = nome;
        this.valor = valor;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public String getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public Bitmap getImagem() {
        return imagem;
    }
}
