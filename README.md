Backend do Deviante

Backend do Deviante composto pela API BFF em Kotlin/Ktor e pelo serviço Python de mineração, chamado somente pela API. A persistência e a autorização de negócio permanecem no BFF; o microserviço Python fornece parsing e cálculos especializados.

## Arquitetura atual

O BFF é um único módulo Gradle (`com.deviante`). Está organizado principalmente por responsabilidade técnica: `Routing.kt` define rotas e atualmente também concentra validação/orquestração; `repository/` acessa persistência com Exposed; `db/` declara tabelas; `model/` contém records; `dto/` contém contratos HTTP e conversões. Os records não dependem de Ktor, Exposed ou das outras camadas. Alguns repositórios convertem modelos para DTOs, portanto a separação ainda não é Clean Architecture estrita.

O serviço Python, em `mining/app/`, separa a API FastAPI (`main.py`), motor (`analysis_engine.py`), contrato/adaptador de detecção (`drift_detector.py`), algoritmo de pesquisa (`ipdd_adwin.py`), parsing (`parser.py`) e mineração de grafo (`graph_miner.py`). O BFF chama o serviço; o navegador não o acessa diretamente.

O estado deste checkout é uma arquitetura em camadas com módulos de domínio especializados, não uma migração completa para vertical slices. Para evoluir sem quebrar contratos existentes, cada slice deverá agrupar endpoint, caso de uso, contrato e porta/adaptador de persistência por capacidade (por exemplo, `processes`, `event-logs`, `analysis` e `maintenance`), mantendo dependências apontadas para dentro. As regras descritas abaixo protegem os limites já reais enquanto essa extração incremental acontece; elas não afirmam que os endpoints já foram reorganizados.

## Testes de arquitetura

As regras rodam junto com os testes unitários e inspecionam dependências reais, não nomes em comentários.

| Ferramenta | Projeto | Regras cobertas |
|---|---|---|
| ArchUnit 1.4.1 | Kotlin/Ktor | `model` não depende de entrega/persistência; `db` não depende das camadas de aplicação; DTOs não dependem de persistência; repositórios não dependem de Ktor Server. |
| PyTestArch 5.0.0 | Python/FastAPI | motor de análise não depende da API, parser, mineração ou corpus; algoritmo de pesquisa não depende da API. |

### Executar no Windows

Na raiz:

```powershell
.\gradlew.bat test
```

No serviço Python:

```powershell
cd mining
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements-dev.txt
\.venv\Scripts\python.exe -m pytest tests -q
```

Para rodar somente os testes arquiteturais Python: `\.venv\Scripts\python.exe -m pytest tests/test_architecture.py -q` dentro de `mining`.

### Resultado observado durante esta atividade

- `gradlew.bat test`: passou.
- `pytest tests/test_architecture.py -q`: 2 passaram.
- `pytest tests -q`: 33 passaram e 2 falharam porque os arquivos `DR_MS_04_matrix.csv` e `DR_MS_ST_25_matrix.csv` do corpus de pesquisa não estão neste checkout. Os dois testes dependentes do corpus não devem ser reportados como aprovados.

## Integridade da evidência

Os resultados do documento de entrega devem refletir as saídas e relatórios produzidos pelos comandos acima. Não foram incluídos os CSVs de pesquisa ausentes nem alegada uma refatoração completa da API para slices.

## Build e execução

```powershell
.\gradlew.bat build
.\gradlew.bat run
```

O serviço Python e as instruções de deploy estão em [`mining/README.md`](mining/README.md).
