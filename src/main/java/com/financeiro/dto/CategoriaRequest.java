package com.financeiro.dto;

import com.financeiro.model.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoriaRequest {

    @NotBlank(message = "O nome da categoria é obrigatório.")
    private String nome;

    @NotNull(message = "O tipo da categoria é obrigatório.")
    private TipoCategoria tipo;

    public CategoriaRequest() {
    }

    public CategoriaRequest(String nome, TipoCategoria tipo) {
        this.nome = nome;
        this.tipo = tipo;
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
