# Flux Architecture

This document maps the conceptual architecture of **MarkLogic Flux** to actual
file paths in the repository. Diagrams use [Mermaid](https://mermaid.js.org/) syntax.

---

## Module Overview

```mermaid
graph TB
    subgraph "Core Application"
        CLI["CLI Entry Point<br/><i>flux-cli/src/main/java/com/marklogic/flux/cli/Main.java</i>"]
        SPARK["Spark-Submit Entry<br/><i>flux-cli/src/main/java/com/marklogic/flux/spark/Submit.java</i>"]
        API["Public API<br/><i>flux-cli/src/main/java/com/marklogic/flux/api/Flux.java</i>"]
    end

    subgraph "Command Implementations  — flux-cli/src/main/java/com/marklogic/flux/impl/"
        IMPORT["Import Commands<br/><i>impl/importdata/</i>"]
        EXPORT["Export Commands<br/><i>impl/export/</i>"]
        COPY["Copy Command<br/><i>impl/copy/CopyCommand.java</i>"]
        REPROCESS["Reprocess Command<br/><i>impl/reprocess/ReprocessCommand.java</i>"]
        CUSTOM["Custom Commands<br/><i>impl/custom/</i>"]
        TDE["TDE Builder<br/><i>flux-cli/src/main/java/com/marklogic/flux/tde/</i>"]
    end

    subgraph "Embedding Model Plugins"
        AZURE_EMB["Azure OpenAI<br/><i>flux-embedding-model-azure-open-ai/</i>"]
        MINILM["MiniLM<br/><i>flux-embedding-model-minilm/</i>"]
        OLLAMA["Ollama<br/><i>flux-embedding-model-ollama/</i>"]
    end

    subgraph "Examples &amp; Testing"
        GETTING_STARTED["Getting Started<br/><i>examples/getting-started/</i>"]
        CLIENT_PROJ["Client Project<br/><i>examples/client-project/</i>"]
        TEST_APP["Test App<br/><i>test-app/</i>"]
        COVERAGE["Code Coverage<br/><i>code-coverage-report/</i>"]
    end

    CLI --> IMPORT
    CLI --> EXPORT
    CLI --> COPY
    CLI --> REPROCESS
    CLI --> CUSTOM
    SPARK --> CLI
    API --> IMPORT
    API --> EXPORT
    API --> COPY
    API --> REPROCESS
    API --> CUSTOM
    IMPORT --> TDE
    IMPORT -.->|optional| AZURE_EMB
    IMPORT -.->|optional| MINILM
    IMPORT -.->|optional| OLLAMA
    CLIENT_PROJ --> API
    COVERAGE --> CLI
```

---

## Data Flow 1 — File Import into MarkLogic

Shows how files on disk (or cloud storage) are imported into MarkLogic.

```mermaid
flowchart LR
    FILES["Source Files<br/>JSON / XML / CSV /<br/>Parquet / Avro / RDF"]
    READ["ReadFilesParams<br/><i>impl/importdata/ReadFilesParams.java</i>"]
    SPLIT["SplitterParams<br/><i>impl/importdata/SplitterParams.java</i>"]
    EMBED["EmbedderParams<br/><i>impl/importdata/EmbedderParams.java</i>"]
    WRITE["WriteDocumentParams<br/><i>impl/importdata/WriteDocumentParams.java</i>"]
    ML[(MarkLogic)]

    FILES --> READ
    READ -->|Spark DataFrame| SPLIT
    SPLIT -->|chunked text| EMBED
    EMBED -->|vectors added| WRITE
    WRITE -->|Spark Connector| ML
```

---

## Data Flow 2 — Document Export from MarkLogic

Shows how documents or rows are exported from MarkLogic to files or a relational database.

```mermaid
flowchart LR
    ML[(MarkLogic)]
    RDDOC["ReadDocumentParams<br/><i>impl/export/ReadDocumentParams.java</i>"]
    RDROW["ReadRowsParams<br/><i>impl/export/ReadRowsParams.java</i>"]
    WRFILE["WriteFilesParams<br/><i>impl/export/WriteFilesParams.java</i>"]
    JDBC["JdbcParams<br/><i>impl/JdbcParams.java</i>"]
    OUT_FILES["Output Files<br/>Parquet / CSV /<br/>JSON Lines / RDF"]
    OUT_DB["Relational DB<br/>PostgreSQL / etc."]

    ML -->|Optic DSL| RDROW
    ML -->|Document Read| RDDOC
    RDROW --> WRFILE
    RDDOC --> WRFILE
    RDROW --> JDBC
    WRFILE --> OUT_FILES
    JDBC --> OUT_DB
```

---

## Data Flow 3 — Cross-Database Copy

Shows how documents are copied between two MarkLogic databases.

```mermaid
flowchart LR
    SRC[(Source MarkLogic)]
    CONN_IN["ConnectionParams<br/><i>impl/ConnectionParams.java</i>"]
    COPYCMD["CopyCommand<br/><i>impl/copy/CopyCommand.java</i>"]
    CONN_OUT["OutputConnectionParams<br/><i>impl/copy/OutputConnectionParams.java</i>"]
    DST[(Target MarkLogic)]

    SRC --> CONN_IN --> COPYCMD --> CONN_OUT --> DST
```

---

## Dependency Graph (Gradle Modules)

```mermaid
graph TD
    ROOT["flux <i>(root project)</i><br/><i>build.gradle</i>"]
    FCLI["flux-cli<br/><i>flux-cli/build.gradle</i>"]
    EMB_AZ["flux-embedding-model-azure-open-ai<br/><i>flux-embedding-model-azure-open-ai/build.gradle</i>"]
    EMB_ML["flux-embedding-model-minilm<br/><i>flux-embedding-model-minilm/build.gradle</i>"]
    EMB_OL["flux-embedding-model-ollama<br/><i>flux-embedding-model-ollama/build.gradle</i>"]
    SPLITTER["flux-custom-splitter-example<br/><i>flux-custom-splitter-example/build.gradle</i>"]
    TAPP["test-app<br/><i>test-app/build.gradle</i>"]
    COV["code-coverage-report<br/><i>code-coverage-report/build.gradle</i>"]

    ROOT --> FCLI
    ROOT --> EMB_AZ
    ROOT --> EMB_ML
    ROOT --> EMB_OL
    ROOT --> SPLITTER
    ROOT --> TAPP
    ROOT --> COV

    FCLI -->|Spark Connector| SC["marklogic-spark-connector"]
    FCLI -->|CLI framework| PICO["picocli"]
    FCLI -->|distributed processing| SPARK["spark-sql"]
    EMB_AZ -->|embeddings| LC4J_AZ["langchain4j-azure-open-ai"]
    EMB_ML -->|embeddings| LC4J_ML["langchain4j-minilm"]
    EMB_OL -->|embeddings| LC4J_OL["langchain4j-ollama"]
    COV -.->|aggregates coverage| FCLI
    COV -.->|aggregates coverage| EMB_AZ
    COV -.->|aggregates coverage| EMB_ML
    COV -.->|aggregates coverage| EMB_OL
```

---

## Key File Reference

| Concept | File Path |
|---------|-----------|
| CLI entry point | `flux-cli/src/main/java/com/marklogic/flux/cli/Main.java` |
| Spark-submit entry | `flux-cli/src/main/java/com/marklogic/flux/spark/Submit.java` |
| Public API (factory) | `flux-cli/src/main/java/com/marklogic/flux/api/Flux.java` |
| Abstract command base | `flux-cli/src/main/java/com/marklogic/flux/impl/AbstractCommand.java` |
| Import files command | `flux-cli/src/main/java/com/marklogic/flux/impl/importdata/ImportFilesCommand.java` |
| Export Parquet command | `flux-cli/src/main/java/com/marklogic/flux/impl/export/ExportParquetFilesCommand.java` |
| Copy command | `flux-cli/src/main/java/com/marklogic/flux/impl/copy/CopyCommand.java` |
| Reprocess command | `flux-cli/src/main/java/com/marklogic/flux/impl/reprocess/ReprocessCommand.java` |
| Connection params | `flux-cli/src/main/java/com/marklogic/flux/impl/ConnectionParams.java` |
| JDBC params | `flux-cli/src/main/java/com/marklogic/flux/impl/JdbcParams.java` |
| Splitter params | `flux-cli/src/main/java/com/marklogic/flux/impl/importdata/SplitterParams.java` |
| Embedder params | `flux-cli/src/main/java/com/marklogic/flux/impl/importdata/EmbedderParams.java` |
| TDE builder | `flux-cli/src/main/java/com/marklogic/flux/tde/TdeBuilder.java` |
| CI pipeline | `Jenkinsfile` |
| Root build | `build.gradle` |
| Module registry | `settings.gradle` |
