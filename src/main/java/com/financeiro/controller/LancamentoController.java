package com.financeiro.controller;

import com.financeiro.dto.LancamentoRequest;
import com.financeiro.dto.LancamentoResponse;
import com.financeiro.dto.ResumoFinanceiroResponse;
import com.financeiro.model.Categoria;
import com.financeiro.model.Lancamento;
import com.financeiro.model.TipoLancamento;
import com.financeiro.service.CategoriaService;
import com.financeiro.service.LancamentoService;
import com.financeiro.service.ResumoFinanceiroService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LancamentoController {

    private final LancamentoService lancamentoService;
    private final CategoriaService categoriaService;
    private final ResumoFinanceiroService resumoFinanceiroService;

    public LancamentoController(LancamentoService lancamentoService, CategoriaService categoriaService, ResumoFinanceiroService resumoFinanceiroService) {
        this.lancamentoService = lancamentoService;
        this.categoriaService = categoriaService;
        this.resumoFinanceiroService = resumoFinanceiroService;
    }

    @GetMapping("/lancamentos")
    public List<LancamentoResponse> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) TipoLancamento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        return lancamentoService.listar(categoriaId, tipo, dataInicio, dataFim).stream()
                .map(LancamentoResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/lancamentos/{id}")
    public LancamentoResponse buscarPorId(@PathVariable Long id) {
        return LancamentoResponse.from(lancamentoService.buscarPorId(id));
    }

    @PostMapping("/lancamentos")
    public ResponseEntity<LancamentoResponse> criar(@Valid @RequestBody LancamentoRequest request) {
        Categoria categoria = categoriaService.buscarPorId(request.getCategoriaId());
        Lancamento lancamento = new Lancamento(null, request.getDescricao(), request.getValor(), request.getData(), request.getTipo(), categoria);
        Lancamento salvo = lancamentoService.criar(lancamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(LancamentoResponse.from(salvo));
    }

    @PutMapping("/lancamentos/{id}")
    public LancamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody LancamentoRequest request) {
        Categoria categoria = categoriaService.buscarPorId(request.getCategoriaId());
        Lancamento lancamentoAtualizado = new Lancamento(id, request.getDescricao(), request.getValor(), request.getData(), request.getTipo(), categoria);
        return LancamentoResponse.from(lancamentoService.atualizar(id, lancamentoAtualizado));
    }

    @DeleteMapping("/lancamentos/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        lancamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lancamentos/resumo")
    public ResumoFinanceiroResponse resumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        if (dataInicio == null && dataFim == null) {
            dataInicio = LocalDate.now().withDayOfMonth(1);
            dataFim = LocalDate.now();
        }

        return resumoFinanceiroService.calcular(dataInicio, dataFim);
    }
}
