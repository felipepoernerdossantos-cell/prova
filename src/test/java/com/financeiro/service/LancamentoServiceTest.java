package com.financeiro.service;

import com.financeiro.model.Categoria;
import com.financeiro.model.Lancamento;
import com.financeiro.model.TipoCategoria;
import com.financeiro.model.TipoLancamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LancamentoServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private LancamentoService lancamentoService;

    @Test
    void deveSalvarLancamentoComCategoriaCompativel() {
        Categoria categoria = categoriaService.criar(new Categoria(null, "Mercado", TipoCategoria.DESPESA));

        Lancamento lancamento = lancamentoService.criar(new Lancamento(null, "Compra do mês", new BigDecimal("150.50"), LocalDate.now(), TipoLancamento.DESPESA, categoria));

        assertNotNull(lancamento.getId());
        assertEquals("Mercado", lancamento.getCategoria().getNome());
    }

    @Test
    void naoDevePermitirLancamentoSemValorPositivo() {
        Categoria categoria = categoriaService.criar(new Categoria(null, "Salário", TipoCategoria.RECEITA));

        assertThrows(RuntimeException.class, () -> lancamentoService.criar(new Lancamento(null, "Salário", BigDecimal.ZERO, LocalDate.now(), TipoLancamento.RECEITA, categoria)));
    }
}
