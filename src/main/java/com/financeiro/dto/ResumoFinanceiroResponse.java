package com.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ResumoFinanceiroResponse {
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal saldo;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public ResumoFinanceiroResponse() {
    }

    public ResumoFinanceiroResponse(BigDecimal totalReceitas, BigDecimal totalDespesas, BigDecimal saldo, LocalDate dataInicio, LocalDate dataFim) {
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.saldo = saldo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public BigDecimal getTotalReceitas() {
        return totalReceitas;
    }

    public void setTotalReceitas(BigDecimal totalReceitas) {
        this.totalReceitas = totalReceitas;
    }

    public BigDecimal getTotalDespesas() {
        return totalDespesas;
    }

    public void setTotalDespesas(BigDecimal totalDespesas) {
        this.totalDespesas = totalDespesas;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}
