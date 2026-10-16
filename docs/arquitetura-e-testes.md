# Evidências de arquitetura e testes

Repositório de entrega: https://github.com/murilo2710/Testes-arquitetura-

## Integrantes

- Murilo Regnier
- Bernardo Vieira
- Emanuelle Skolut
- Alander Ávila

## Escopo verificado

O checkout analisado contém dois backends: BFF Kotlin/Ktor e microserviço Python/FastAPI. Não contém serviços backend Java, C# ou Node.js; por isso, ArchiUnit.Net e ArchiUnitTS não se aplicam a este código. As ferramentas usadas são ArchUnit para JVM/Kotlin e PyTestArch para Python.

O BFF é um módulo Gradle organizado por pacotes técnicos (`dto`, `model`, `repository`, `db`) e tem roteamento centralizado em `Routing.kt`. O microserviço Python separa `main.py`, `analysis_engine.py`, `drift_detector.py`, `ipdd_adwin.py`, `parser.py` e `graph_miner.py`. O estado não é uma implementação completa de vertical slices/Clean Architecture: parte da orquestração permanece nas rotas e há repositórios que usam contratos DTO. Esta entrega documenta essas fronteiras e automatiza regras sobre as dependências reais sem afirmar uma refatoração que não ocorreu.

## Regras automatizadas

**ArchUnit:** classes de `model` não dependem de `db`, DTO, repositórios, Ktor ou Exposed; tabelas `db` não dependem de DTO, modelo, repositório ou Ktor; DTOs não dependem de implementação de persistência; repositórios não dependem de Ktor Server.

**PyTestArch:** `analysis_engine` não importa API, parser, mineração de grafo ou corpus de pesquisa; `ipdd_adwin` não importa o módulo da API.

## Evidência de execução

Comandos executados em Windows:

```powershell
.\gradlew.bat test
cd mining
.\.venv\Scripts\python.exe -m pytest tests/test_architecture.py -q
.\.venv\Scripts\python.exe -m pytest tests -q
```

Resultado observado:

| Execução | Resultado |
|---|---|
| Gradle `test` | BUILD SUCCESSFUL; 28 testes, 0 falhas, 0 erros. |
| PyTestArch (`tests/test_architecture.py`) | 2 passed. |
| Suíte Python completa | 35 passed, 2 failed. |

As duas falhas são `test_bundled_luiz_corpus_is_used_when_training_runs_are_omitted` e `test_bundled_luiz_corpus_contains_real_failure_labels`. Ambas dependem de `DR_MS_04_matrix.csv` e `DR_MS_ST_25_matrix.csv`, ausentes do checkout. O erro explícito observado foi `Corpus de treinamento ausente: DR_MS_04_matrix.csv.`; os dados não foram inventados nem os testes alterados para contornar a ausência.

## Fontes no GitHub

- [Teste ArchUnit](https://github.com/murilo2710/Testes-arquitetura-/blob/main/src/test/kotlin/ArchitectureTest.kt)
- [Configuração Gradle](https://github.com/murilo2710/Testes-arquitetura-/blob/main/build.gradle.kts)
- [Teste PyTestArch](https://github.com/murilo2710/Testes-arquitetura-/blob/main/mining/tests/test_architecture.py)
- [Dependências de teste Python](https://github.com/murilo2710/Testes-arquitetura-/blob/main/mining/requirements-dev.txt)
- [README de arquitetura e comandos](https://github.com/murilo2710/Testes-arquitetura-/blob/main/README.md)

Os links apontam para a branch `main` do repositório de entrega. A publicação precisa ser confirmada pelo commit remoto antes de usar estes links como evidência final.

## Captura de tela

Este documento traz a transcrição fiel dos resultados produzidos pelos comandos. Para cumprir literalmente a exigência de print screen do terminal, abra um terminal na raiz do projeto, execute os comandos acima e capture a janela com `Win + Shift + S`; não use uma imagem recriada como se fosse captura do terminal.