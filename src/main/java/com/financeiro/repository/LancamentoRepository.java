package com.financeiro.repository;

import com.financeiro.model.Lancamento;
import com.financeiro.model.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findAllByOrderByDataDesc();

    List<Lancamento> findByDataBetweenOrderByDataDesc(LocalDate inicio, LocalDate fim);

    List<Lancamento> findByTipoOrderByDataDesc(TipoLancamento tipo);

    List<Lancamento> findByCategoriaIdOrderByDataDesc(Long categoriaId);

    boolean existsByCategoriaId(Long categoriaId);
}
