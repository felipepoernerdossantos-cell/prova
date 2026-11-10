# Controle Financeiro

API REST para cadastro de categorias e lançamentos financeiros pessoais.

## Requisitos atendidos

- CRUD de categorias
- CRUD de lançamentos
- Validações de regras de negócio
- Filtros por categoria, tipo e período
- Resumo financeiro por período
- Testes automatizados com JUnit e Spring Boot

## Como executar

1. Certifique-se de ter Java 21 ou superior instalado.
2. Na raiz do projeto, execute:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
mvn spring-boot:run
```

3. A API ficará disponível em:
   - http://localhost:8080/api/categorias
   - http://localhost:8080/api/lancamentos
   - http://localhost:8080/api/lancamentos/resumo

## Banco de dados

A aplicação usa H2 em memória para facilitar testes e execução local.
A console H2 pode ser acessada em:

- http://localhost:8080/h2-console

## Exemplo de payload

### Categoria

```json
{
  "nome": "Salário",
  "tipo": "RECEITA"
}
```

### Lançamento

```json
{
  "descricao": "Pagamento do mês",
  "valor": 2500.00,
  "data": "2026-11-10",
  "tipo": "DESPESA",
  "categoriaId": 1
}
```
