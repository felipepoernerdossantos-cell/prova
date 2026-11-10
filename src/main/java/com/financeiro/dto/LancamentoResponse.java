package com.financeiro.dto;

import com.financeiro.model.Lancamento;
import com.financeiro.model.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoResponse {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private TipoLancamento tipo;
    private CategoriaResponse categoria;

    public LancamentoResponse() {
    }

    public LancamentoResponse(Long id, String descricao, BigDecimal valor, LocalDate data, TipoLancamento tipo, CategoriaResponse categoria) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.tipo = tipo;
        this.categoria = categoria;
    }

    public static LancamentoResponse from(Lancamento lancamento) {
        return new LancamentoResponse(
                lancamento.getId(),
                lancamento.getDescricao(),
                lancamento.getValor(),
                lancamento.getData(),
                lancamento.getTipo(),
                CategoriaResponse.from(lancamento.getCategoria())
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CategoriaResponse getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponse categoria) {
        this.categoria = categoria;
    }
}
