package com.financeiro.dto;

import com.financeiro.model.Categoria;
import com.financeiro.model.TipoCategoria;

public class CategoriaResponse {
    private Long id;
    private String nome;
    private TipoCategoria tipo;

    public CategoriaResponse() {
    }

    public CategoriaResponse(Long id, String nome, TipoCategoria tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getTipo());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoCategoria getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoria tipo) {
        this.tipo = tipo;
    }
}
