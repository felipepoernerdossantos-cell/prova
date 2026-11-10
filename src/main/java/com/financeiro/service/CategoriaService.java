package com.financeiro.service;

import com.financeiro.exception.CategoriaDuplicadaException;
import com.financeiro.exception.CategoriaEmUsoException;
import com.financeiro.exception.CategoriaNaoEncontradaException;
import com.financeiro.exception.ValidacaoException;
import com.financeiro.model.Categoria;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.LancamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final LancamentoRepository lancamentoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, LancamentoRepository lancamentoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    public Categoria criar(Categoria categoria) {
        validarCategoria(categoria);

        if (categoriaRepository.existsByNomeIgnoreCase(categoria.getNome().trim())) {
            throw new CategoriaDuplicadaException("Já existe uma categoria com esse nome.");
        }

        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listar() {
        return categoriaRepository.findAllByOrderByNomeAsc();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada com o id " + id));
    }

    public Categoria atualizar(Long id, Categoria categoriaAtualizada) {
        Categoria categoriaExistente = buscarPorId(id);
        validarCategoria(categoriaAtualizada);

        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(categoriaAtualizada.getNome().trim(), id)) {
            throw new CategoriaDuplicadaException("Já existe uma categoria com esse nome.");
        }

        categoriaExistente.setNome(categoriaAtualizada.getNome().trim());
        categoriaExistente.setTipo(categoriaAtualizada.getTipo());
        return categoriaRepository.save(categoriaExistente);
    }

    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);

        if (lancamentoRepository.existsByCategoriaId(id)) {
            throw new CategoriaEmUsoException("Não é possível excluir uma categoria que está em uso por lançamentos.");
        }

        categoriaRepository.delete(categoria);
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new ValidacaoException("Categoria inválida.");
        }

        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new ValidacaoException("O nome da categoria é obrigatório.");
        }

        if (categoria.getTipo() == null) {
            throw new ValidacaoException("O tipo da categoria é obrigatório.");
        }
    }
}
