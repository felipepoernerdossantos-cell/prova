package com.financeiro.controller;

import com.financeiro.dto.CategoriaRequest;
import com.financeiro.dto.CategoriaResponse;
import com.financeiro.model.Categoria;
import com.financeiro.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listar().stream()
                .map(CategoriaResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscarPorId(@PathVariable Long id) {
        return CategoriaResponse.from(categoriaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest request) {
        Categoria categoria = categoriaService.criar(new Categoria(null, request.getNome(), request.getTipo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponse.from(categoria));
    }

    @PutMapping("/{id}")
    public CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        Categoria categoriaAtualizada = categoriaService.atualizar(id, new Categoria(id, request.getNome(), request.getTipo()));
        return CategoriaResponse.from(categoriaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
