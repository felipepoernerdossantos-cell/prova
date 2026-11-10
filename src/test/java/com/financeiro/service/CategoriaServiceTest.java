package com.financeiro.service;

import com.financeiro.model.Categoria;
import com.financeiro.model.TipoCategoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CategoriaServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    @Test
    void deveSalvarCategoriaValida() {
        Categoria categoria = categoriaService.criar(new Categoria(null, "Salário", TipoCategoria.RECEITA));

        assertNotNull(categoria.getId());
        assertEquals("Salário", categoria.getNome());
        assertEquals(TipoCategoria.RECEITA, categoria.getTipo());
    }

    @Test
    void naoDevePermitirCategoriaDuplicada() {
        categoriaService.criar(new Categoria(null, "Alimentação", TipoCategoria.DESPESA));

        assertThrows(RuntimeException.class, () -> categoriaService.criar(new Categoria(null, "Alimentação", TipoCategoria.DESPESA)));
    }
}
