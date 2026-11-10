package com.financeiro.service;

import com.financeiro.dto.ResumoFinanceiroResponse;
import com.financeiro.model.Lancamento;
import com.financeiro.model.TipoLancamento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ResumoFinanceiroService {

    private final LancamentoService lancamentoService;

    public ResumoFinanceiroService(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    public ResumoFinanceiroResponse calcular(LocalDate dataInicio, LocalDate dataFim) {
        List<Lancamento> lancamentos = lancamentoService.listar(null, null, dataInicio, dataFim);

        BigDecimal totalReceitas = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.RECEITA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.DESPESA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return new ResumoFinanceiroResponse(totalReceitas, totalDespesas, saldo, dataInicio, dataFim);
    }
}
