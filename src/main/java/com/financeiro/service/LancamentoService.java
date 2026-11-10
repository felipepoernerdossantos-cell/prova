package com.financeiro.service;

import com.financeiro.exception.CategoriaNaoEncontradaException;
import com.financeiro.exception.LancamentoNaoEncontradaException;
import com.financeiro.exception.ValidacaoException;
import com.financeiro.model.Categoria;
import com.financeiro.model.Lancamento;
import com.financeiro.model.TipoLancamento;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.LancamentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;
    private final CategoriaRepository categoriaRepository;

    public LancamentoService(LancamentoRepository lancamentoRepository, CategoriaRepository categoriaRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Lancamento criar(Lancamento lancamento) {
        validarLancamento(lancamento);

        Categoria categoria = categoriaRepository.findById(lancamento.getCategoria().getId())
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada para o lançamento."));

        if (categoria.getTipo().name().equals(lancamento.getTipo().name()) == false) {
            throw new ValidacaoException("O tipo do lançamento deve ser compatível com o tipo da categoria.");
        }

        lancamento.setCategoria(categoria);
        return lancamentoRepository.save(lancamento);
    }

    public List<Lancamento> listar(Long categoriaId, TipoLancamento tipo, LocalDate dataInicio, LocalDate dataFim) {
        List<Lancamento> lancamentos = lancamentoRepository.findAllByOrderByDataDesc();

        if (categoriaId != null) {
            lancamentos = lancamentos.stream()
                    .filter(item -> item.getCategoria() != null && categoriaId.equals(item.getCategoria().getId()))
                    .collect(Collectors.toList());
        }

        if (tipo != null) {
            lancamentos = lancamentos.stream()
                    .filter(item -> item.getTipo() == tipo)
                    .collect(Collectors.toList());
        }

        if (dataInicio != null) {
            lancamentos = lancamentos.stream()
                    .filter(item -> !item.getData().isBefore(dataInicio))
                    .collect(Collectors.toList());
        }

        if (dataFim != null) {
            lancamentos = lancamentos.stream()
                    .filter(item -> !item.getData().isAfter(dataFim))
                    .collect(Collectors.toList());
        }

        lancamentos.sort(Comparator.comparing(Lancamento::getData).reversed());
        return lancamentos;
    }

    public Lancamento buscarPorId(Long id) {
        return lancamentoRepository.findById(id)
                .orElseThrow(() -> new LancamentoNaoEncontradaException("Lançamento não encontrado com o id " + id));
    }

    public Lancamento atualizar(Long id, Lancamento lancamentoAtualizado) {
        Lancamento lancamentoExistente = buscarPorId(id);
        validarLancamento(lancamentoAtualizado);

        Categoria categoria = categoriaRepository.findById(lancamentoAtualizado.getCategoria().getId())
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada para o lançamento."));

        if (categoria.getTipo().name().equals(lancamentoAtualizado.getTipo().name()) == false) {
            throw new ValidacaoException("O tipo do lançamento deve ser compatível com o tipo da categoria.");
        }

        lancamentoExistente.setDescricao(lancamentoAtualizado.getDescricao());
        lancamentoExistente.setValor(lancamentoAtualizado.getValor());
        lancamentoExistente.setData(lancamentoAtualizado.getData());
        lancamentoExistente.setTipo(lancamentoAtualizado.getTipo());
        lancamentoExistente.setCategoria(categoria);

        return lancamentoRepository.save(lancamentoExistente);
    }

    public void excluir(Long id) {
        Lancamento lancamento = buscarPorId(id);
        lancamentoRepository.delete(lancamento);
    }

    private void validarLancamento(Lancamento lancamento) {
        if (lancamento == null) {
            throw new ValidacaoException("Lançamento inválido.");
        }

        if (lancamento.getDescricao() == null || lancamento.getDescricao().isBlank()) {
            throw new ValidacaoException("A descrição do lançamento é obrigatória.");
        }

        if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("O valor do lançamento deve ser maior que zero.");
        }

        if (lancamento.getData() == null) {
            throw new ValidacaoException("A data do lançamento é obrigatória.");
        }

        if (lancamento.getTipo() == null) {
            throw new ValidacaoException("O tipo do lançamento é obrigatório.");
        }

        if (lancamento.getCategoria() == null || lancamento.getCategoria().getId() == null) {
            throw new ValidacaoException("A categoria do lançamento é obrigatória.");
        }
    }
}
