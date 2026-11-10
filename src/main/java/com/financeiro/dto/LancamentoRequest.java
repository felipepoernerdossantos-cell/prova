package com.financeiro.dto;

import com.financeiro.model.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoRequest {

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotNull(message = "O valor é obrigatório.")
    private BigDecimal valor;

    @NotNull(message = "A data é obrigatória.")
    private LocalDate data;

    @NotNull(message = "O tipo do lançamento é obrigatório.")
    private TipoLancamento tipo;

    @NotNull(message = "A categoria é obrigatória.")
    private Long categoriaId;

    public LancamentoRequest() {
    }

    public LancamentoRequest(String descricao, BigDecimal valor, LocalDate data, TipoLancamento tipo, Long categoriaId) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.tipo = tipo;
        this.categoriaId = categoriaId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
