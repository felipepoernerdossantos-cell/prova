<?php

declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

function jsonResponse($statusCode, $payload): void
{
    http_response_code($statusCode);
    echo json_encode($payload, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit;
}

function dataFilePath(): string
{
    return __DIR__ . '/storage/data.json';
}

function ensureDataFile(): void
{
    $path = dataFilePath();
    $dir = dirname($path);

    if (!is_dir($dir)) {
        mkdir($dir, 0777, true);
    }

    if (!file_exists($path)) {
        $data = [
            'categorias' => [
                ['id' => 1, 'nome' => 'Salário', 'tipo' => 'RECEITA'],
                ['id' => 2, 'nome' => 'Alimentação', 'tipo' => 'DESPESA'],
            ],
            'lancamentos' => [
                ['id' => 1, 'descricao' => 'Salário principal', 'valor' => 5000.00, 'data' => '2026-11-10', 'tipo' => 'RECEITA', 'categoriaId' => 1],
                ['id' => 2, 'descricao' => 'Mercado', 'valor' => 250.75, 'data' => '2026-11-12', 'tipo' => 'DESPESA', 'categoriaId' => 2],
            ],
        ];
        file_put_contents($path, json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
    }
}

function loadData(): array
{
    ensureDataFile();
    $content = file_get_contents(dataFilePath());
    $data = json_decode($content, true);

    if (!is_array($data)) {
        return ['categorias' => [], 'lancamentos' => []];
    }

    return [
        'categorias' => $data['categorias'] ?? [],
        'lancamentos' => $data['lancamentos'] ?? [],
    ];
}

function saveData(array $data): void
{
    file_put_contents(dataFilePath(), json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));
}

function readJsonBody(): array
{
    $input = file_get_contents('php://input');
    if ($input === '') {
        return [];
    }

    $decoded = json_decode($input, true);
    return is_array($decoded) ? $decoded : [];
}

function findCategoriaById(array $categorias, int $id): ?array
{
    foreach ($categorias as $categoria) {
        if ((int)$categoria['id'] === $id) {
            return $categoria;
        }
    }

    return null;
}

function findLancamentoById(array $lancamentos, int $id): ?array
{
    foreach ($lancamentos as $lancamento) {
        if ((int)$lancamento['id'] === $id) {
            return $lancamento;
        }
    }

    return null;
}

function isValidTipo(string $tipo): bool
{
    return in_array($tipo, ['RECEITA', 'DESPESA'], true);
}

function normalizeCategoriaPayload(array $payload): array
{
    $nome = trim((string)($payload['nome'] ?? ''));
    $tipo = strtoupper((string)($payload['tipo'] ?? ''));

    if ($nome === '') {
        jsonResponse(400, ['message' => 'O nome da categoria é obrigatório.']);
    }

    if (!isValidTipo($tipo)) {
        jsonResponse(400, ['message' => 'O tipo da categoria deve ser RECEITA ou DESPESA.']);
    }

    return ['nome' => $nome, 'tipo' => $tipo];
}

function normalizeLancamentoPayload(array $payload): array
{
    $descricao = trim((string)($payload['descricao'] ?? ''));
    $valor = (float)($payload['valor'] ?? 0);
    $data = (string)($payload['data'] ?? '');
    $tipo = strtoupper((string)($payload['tipo'] ?? ''));
    $categoriaId = isset($payload['categoriaId']) ? (int)$payload['categoriaId'] : 0;

    if ($descricao === '') {
        jsonResponse(400, ['message' => 'A descrição do lançamento é obrigatória.']);
    }

    if ($valor <= 0) {
        jsonResponse(400, ['message' => 'O valor do lançamento deve ser maior que zero.']);
    }

    if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $data)) {
        jsonResponse(400, ['message' => 'A data deve estar no formato YYYY-MM-DD.']);
    }

    if (!isValidTipo($tipo)) {
        jsonResponse(400, ['message' => 'O tipo do lançamento deve ser RECEITA ou DESPESA.']);
    }

    if ($categoriaId <= 0) {
        jsonResponse(400, ['message' => 'A categoria do lançamento é obrigatória.']);
    }

    return [
        'descricao' => $descricao,
        'valor' => round($valor, 2),
        'data' => $data,
        'tipo' => $tipo,
        'categoriaId' => $categoriaId,
    ];
}

function applyFilters(array $lancamentos, array $queryParams): array
{
    $categoriaId = isset($queryParams['categoriaId']) ? (int)$queryParams['categoriaId'] : null;
    $tipo = isset($queryParams['tipo']) ? strtoupper((string)$queryParams['tipo']) : null;
    $dataInicio = $queryParams['dataInicio'] ?? null;
    $dataFim = $queryParams['dataFim'] ?? null;

    $filtered = $lancamentos;

    if ($categoriaId !== null && $categoriaId > 0) {
        $filtered = array_values(array_filter($filtered, fn($item) => (int)$item['categoriaId'] === $categoriaId));
    }

    if ($tipo !== null && isValidTipo($tipo)) {
        $filtered = array_values(array_filter($filtered, fn($item) => strtoupper((string)$item['tipo']) === $tipo));
    }

    if ($dataInicio !== null && preg_match('/^\d{4}-\d{2}-\d{2}$/', (string)$dataInicio)) {
        $filtered = array_values(array_filter($filtered, fn($item) => $item['data'] >= $dataInicio));
    }

    if ($dataFim !== null && preg_match('/^\d{4}-\d{2}-\d{2}$/', (string)$dataFim)) {
        $filtered = array_values(array_filter($filtered, fn($item) => $item['data'] <= $dataFim));
    }

    usort($filtered, fn($a, $b) => strcmp($b['data'], $a['data']));

    return $filtered;
}

function summaryFor(array $lancamentos, ?string $dataInicio, ?string $dataFim): array
{
    $filtered = $lancamentos;

    if ($dataInicio !== null && preg_match('/^\d{4}-\d{2}-\d{2}$/', $dataInicio)) {
        $filtered = array_values(array_filter($filtered, fn($item) => $item['data'] >= $dataInicio));
    }

    if ($dataFim !== null && preg_match('/^\d{4}-\d{2}-\d{2}$/', $dataFim)) {
        $filtered = array_values(array_filter($filtered, fn($item) => $item['data'] <= $dataFim));
    }

    $totalReceitas = array_reduce(
        array_values(array_filter($filtered, fn($item) => strtoupper((string)$item['tipo']) === 'RECEITA')),
        fn($sum, $item) => $sum + (float)$item['valor'],
        0.0
    );

    $totalDespesas = array_reduce(
        array_values(array_filter($filtered, fn($item) => strtoupper((string)$item['tipo']) === 'DESPESA')),
        fn($sum, $item) => $sum + (float)$item['valor'],
        0.0
    );

    return [
        'totalReceitas' => round($totalReceitas, 2),
        'totalDespesas' => round($totalDespesas, 2),
        'saldo' => round($totalReceitas - $totalDespesas, 2),
        'dataInicio' => $dataInicio,
        'dataFim' => $dataFim,
    ];
}

$method = $_SERVER['REQUEST_METHOD'];
$uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH) ?? '/';
$parts = array_values(array_filter(explode('/', trim($uri, '/')), fn($segment) => $segment !== ''));

if ($parts === []) {
    jsonResponse(404, ['message' => 'Rota não encontrada.']);
}

if ($parts[0] !== 'api') {
    jsonResponse(404, ['message' => 'Rota não encontrada.']);
}

$data = loadData();

if (($parts[1] ?? null) === 'resumo' || (($parts[1] ?? null) === 'lancamentos' && (($parts[2] ?? null) === 'resumo'))) {
    if ($method === 'GET') {
        $inicio = $_GET['dataInicio'] ?? null;
        $fim = $_GET['dataFim'] ?? null;
        jsonResponse(200, summaryFor($data['lancamentos'], $inicio, $fim));
    }
}

if ($parts[1] === 'categorias') {
    if ($method === 'GET') {
        if (count($parts) === 2) {
            jsonResponse(200, $data['categorias']);
        }

        $id = (int)($parts[2] ?? 0);
        if ($id <= 0) {
            jsonResponse(400, ['message' => 'ID inválido.']);
        }

        $categoria = findCategoriaById($data['categorias'], $id);
        if ($categoria === null) {
            jsonResponse(404, ['message' => 'Categoria não encontrada.']);
        }

        jsonResponse(200, $categoria);
    }

    if ($method === 'POST') {
        $payload = normalizeCategoriaPayload(readJsonBody());

        foreach ($data['categorias'] as $categoria) {
            if (strcasecmp((string)$categoria['nome'], $payload['nome']) === 0) {
                jsonResponse(409, ['message' => 'Já existe uma categoria com esse nome.']);
            }
        }

        $nextId = 1;
        foreach ($data['categorias'] as $categoria) {
            $nextId = max($nextId, (int)$categoria['id'] + 1);
        }

        $novaCategoria = ['id' => $nextId, 'nome' => $payload['nome'], 'tipo' => $payload['tipo']];
        $data['categorias'][] = $novaCategoria;
        saveData($data);

        jsonResponse(201, $novaCategoria);
    }

    if ($method === 'PUT') {
        $id = (int)($parts[2] ?? 0);
        if ($id <= 0) {
            jsonResponse(400, ['message' => 'ID inválido.']);
        }

        $categoriaExistente = findCategoriaById($data['categorias'], $id);
        if ($categoriaExistente === null) {
            jsonResponse(404, ['message' => 'Categoria não encontrada.']);
        }

        $payload = normalizeCategoriaPayload(readJsonBody());

        foreach ($data['categorias'] as $index => $categoria) {
            if ((int)$categoria['id'] !== $id && strcasecmp((string)$categoria['nome'], $payload['nome']) === 0) {
                jsonResponse(409, ['message' => 'Já existe uma categoria com esse nome.']);
            }
        }

        $data['categorias'][$id - 1] = ['id' => $id, 'nome' => $payload['nome'], 'tipo' => $payload['tipo']];
        saveData($data);

        jsonResponse(200, $data['categorias'][$id - 1]);
    }

    if ($method === 'DELETE') {
        $id = (int)($parts[2] ?? 0);
        if ($id <= 0) {
            jsonResponse(400, ['message' => 'ID inválido.']);
        }

        $categoriaExistente = findCategoriaById($data['categorias'], $id);
        if ($categoriaExistente === null) {
            jsonResponse(404, ['message' => 'Categoria não encontrada.']);
        }

        $emUso = false;
        foreach ($data['lancamentos'] as $lancamento) {
            if ((int)$lancamento['categoriaId'] === $id) {
                $emUso = true;
                break;
            }
        }

        if ($emUso) {
            jsonResponse(409, ['message' => 'Não é possível excluir uma categoria que está em uso por lançamentos.']);
        }

        $data['categorias'] = array_values(array_filter($data['categorias'], fn($categoria) => (int)$categoria['id'] !== $id));
        saveData($data);

        jsonResponse(204, null);
    }
}

if ($parts[1] === 'lancamentos') {
    if ($method === 'GET') {
        if (count($parts) === 2) {
            $lista = applyFilters($data['lancamentos'], $_GET);
            jsonResponse(200, $lista);
        }

        $id = (int)($parts[2] ?? 0);
        if ($id <= 0) {
            jsonResponse(400, ['message' => 'ID inválido.']);
        }

        $lancamento = findLancamentoById($data['lancamentos'], $id);
        if ($lancamento === null) {
            jsonResponse(404, ['message' => 'Lançamento não encontrado.']);
        }

        jsonResponse(200, $lancamento);
    }

    if ($method === 'POST') {
        $payload = normalizeLancamentoPayload(readJsonBody());

        $categoria = findCategoriaById($data['categorias'], $payload['categoriaId']);
        if ($categoria === null) {
            jsonResponse(404, ['message' => 'Categoria não encontrada.']);
        }

        if (strtoupper((string)$categoria['tipo']) !== $payload['tipo']) {
            jsonResponse(400, ['message' => 'O tipo do lançamento deve ser compatível com o tipo da categoria.']);
        }

        $nextId = 1;
        foreach ($data['lancamentos'] as $lancamento) {
            $nextId = max($nextId, (int)$lancamento['id'] + 1);
        }

        $novoLancamento = [
            'id' => $nextId,
            'descricao' => $payload['descricao'],
            'valor' => round((float)$payload['valor'], 2),
            'data' => $payload['data'],
            'tipo' => $payload['tipo'],
            'categoriaId' => $payload['categoriaId'],
        ];

        $data['lancamentos'][] = $novoLancamento;
        saveData($data);

        jsonResponse(201, $novoLancamento);
    }

    if ($method === 'PUT') {
        $id = (int)($parts[2] ?? 0);
        if ($id <= 0) {
            jsonResponse(400, ['message' => 'ID inválido.']);
        }

        $lancamentoExistente = findLancamentoById($data['lancamentos'], $id);
        if ($lancamentoExistente === null) {
            jsonResponse(404, ['message' => 'Lançamento não encontrado.']);
        }

        $payload = normalizeLancamentoPayload(readJsonBody());
        $categoria = findCategoriaById($data['categorias'], $payload['categoriaId']);
        if ($categoria === null) {
            jsonResponse(404, ['message' => 'Categoria não encontrada.']);
        }

        if (strtoupper((string)$categoria['tipo']) !== $payload['tipo']) {
            jsonResponse(400, ['message' => 'O tipo do lançamento deve ser compatível com o tipo da categoria.']);
        }

        $index = null;
        foreach ($data['lancamentos'] as $i => $lancamento) {
            if ((int)$lancamento['id'] === $id) {
                $index = $i;
                break;
            }
        }

        $data['lancamentos'][$index] = [
            'id' => $id,
            'descricao' => $payload['descricao'],
            'valor' => round((float)$payload['valor'], 2),
            'data' => $payload['data'],
            'tipo' => $payload['tipo'],
            'categoriaId' => $payload['categoriaId'],
        ];
        saveData($data);

        jsonResponse(200, $data['lancamentos'][$index]);
    }

    if ($method === 'DELETE') {
        $id = (int)($parts[2] ?? 0);
        if ($id <= 0) {
            jsonResponse(400, ['message' => 'ID inválido.']);
        }

        $lancamentoExistente = findLancamentoById($data['lancamentos'], $id);
        if ($lancamentoExistente === null) {
            jsonResponse(404, ['message' => 'Lançamento não encontrado.']);
        }

        $data['lancamentos'] = array_values(array_filter($data['lancamentos'], fn($item) => (int)$item['id'] !== $id));
        saveData($data);

        jsonResponse(204, null);
    }
}

if (($parts[1] ?? null) === 'health') {
    jsonResponse(200, ['status' => 'ok']);
}

jsonResponse(404, ['message' => 'Rota não encontrada.']);
