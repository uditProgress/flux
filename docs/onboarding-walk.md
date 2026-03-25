# Onboarding Prompt — Walk Workflow

> **Purpose:** Paste this entire file into Copilot Chat when you join the project.
> It gives Copilot (and you) the context needed to contribute effectively.

---

## What is Flux?

MarkLogic Flux is a Java 17 CLI and API for importing, exporting, copying, and reprocessing data in MarkLogic.
It is built on Apache Spark and the MarkLogic Spark Connector.

## Repository layout

```
flux/                          Root Gradle project
├── flux-cli/                  Core CLI + public API (entry point: Main.java)
│   └── src/main/java/com/marklogic/flux/
│       ├── api/               Public interfaces (Flux.java, *Importer, *Exporter)
│       ├── cli/               PicocLI entry point (Main.java)
│       ├── impl/              Command implementations
│       │   ├── importdata/    Import commands (Parquet, Avro, ORC, CSV, JSON, …)
│       │   ├── export/        Export commands
│       │   ├── copy/          Cross-database copy
│       │   ├── reprocess/     Reprocessing
│       │   └── custom/        Custom import/export via user code
│       ├── spark/             Spark-submit entry (Submit.java)
│       └── tde/               Template-Driven Extraction
├── flux-embedding-model-*/    Optional LangChain4j embedding plugins
├── flux-custom-splitter-example/  Example custom splitter
├── test-app/                  MarkLogic test application (ml-gradle)
├── examples/                  Getting-started and client-project examples
├── code-coverage-report/      JaCoCo coverage aggregation
├── docs/                      GitHub Pages documentation + architecture diagram
├── scripts/                   Validation and utility scripts
├── .github/                   PR template
├── Jenkinsfile                CI pipeline
└── build.gradle               Root build (SonarQube, dependency rules)
```

## Key commands

| Task | Command |
|------|---------|
| Start infrastructure | `docker compose up -d --build` |
| Deploy test app | `./gradlew -i mlDeploy` |
| Run tests | `./gradlew clean test` |
| Run tests + coverage | `./gradlew clean testCodeCoverageReport` |
| Print coverage summary | `bash scripts/print-coverage-summary.sh` |
| Build distribution | `./gradlew distZip` |
| Validate architecture diagram | `bash scripts/validate-mermaid.sh` |

## Walk workflow (plan-first)

Every change follows: **Plan → Implement → Verify → Document**.

1. **Plan** — Before coding, write down: what files change, why, expected evidence, rollback path.
2. **Implement** — Make changes in small PRs (2–4 files). Use Copilot to generate diffs file-by-file.
3. **Verify** — Run `./gradlew clean test`. Paste coverage output into the PR. Validate docs if touched.
4. **Document** — Include your plan, evidence, and rollback in the PR description.

## Branching

- Branch from `develop` (not `main`).
- Name branches `feature/<name>`, `fix/<name>`, or `refactor/<name>`.
- Merge back to `develop` via PR. CI publishes from `develop`.

## PR checklist

- [ ] Plan in PR description (what, why, which files, impact)
- [ ] Tests green
- [ ] Coverage summary pasted
- [ ] Architecture diagram validated (if docs changed)
- [ ] Rollback documented

## How to ask Copilot for help

Here are starter prompts for common tasks:

**Orientation:**
> "List the entry points, key modules, and their file paths in this repo."

**Find duplication:**
> "Find duplicated code patterns across 2–4 files in flux-cli/src/main/java/com/marklogic/flux/impl/ that could be refactored."

**Plan a change:**
> "Propose a plan for [describe task]. List files to change, reason, expected impact, and rollback steps."

**Generate diffs:**
> "Generate diffs file-by-file for [describe change]. I will review each one before accepting."

**Verify:**
> "Check for compilation errors in the files I just changed."

**Coverage:**
> "Show me how to run coverage and include the results in my PR."

## Key files to read first

1. `CONTRIBUTING.md` — Full setup + Walk workflow details
2. `docs/architecture.md` — Module diagram with file paths and data flows
3. `.github/PULL_REQUEST_TEMPLATE.md` — What every PR must include
4. `flux-cli/src/main/java/com/marklogic/flux/api/Flux.java` — Public API factory
5. `flux-cli/src/main/java/com/marklogic/flux/cli/Main.java` — CLI entry point
