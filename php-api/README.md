# API PHP - Controle Financeiro

Projeto em PHP para uso no Postman.

## Requisitos

- PHP 8+
- PHP CLI

## Executar localmente

```bash
cd /home/felipesantos18/prova/controle-financeiro/php-api
php -S 127.0.0.1:8000
```

A API fica disponível em:

- http://127.0.0.1:8000/api/categorias
- http://127.0.0.1:8000/api/lancamentos
- http://127.0.0.1:8000/api/resumo

## Exemplos de payload

### Categoria

```json
{
  "nome": "Transporte",
  "tipo": "DESPESA"
}
```

### Lançamento

```json
{
  "descricao": "Combustível",
  "valor": 180.50,
  "data": "2026-11-15",
  "tipo": "DESPESA",
  "categoriaId": 3
}
```

## Exemplo de filtro

```http
GET /api/lancamentos?categoriaId=2&tipo=DESPESA&dataInicio=2026-11-01&dataFim=2026-11-30
```

## Resumo financeiro

```http
GET /api/resumo?dataInicio=2026-11-01&dataFim=2026-11-30
```
