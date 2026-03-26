## Title
<!-- Format: GHCP -- Walk: ex# short-name -->

## Summary
<!-- What changed and why. Link to plan or write inline. List files/paths touched. -->

- What changed:
- Why:
- Files touched:

## Evidence
<!-- Paste test output, coverage numbers, validation commands you ran. -->

### Tests
<!-- e.g. ./gradlew clean test — PASSED / X tests -->

### Coverage
<!-- Run `./gradlew clean testCodeCoverageReport` then `bash scripts/print-coverage-summary.sh` and paste below. -->

```
====================================================
  JaCoCo Coverage Summary
====================================================
  INSTRUCTION              X / Y  ( Z.Z%)
  BRANCH                   X / Y  ( Z.Z%)
  LINE                     X / Y  ( Z.Z%)
  METHOD                   X / Y  ( Z.Z%)
  CLASS                    X / Y  ( Z.Z%)

  ** Overall instruction coverage: Z.Z% **
====================================================
```

**Instruction coverage:** <!-- e.g. 84.2% -->

### Other validation
<!-- e.g. bash scripts/validate-mermaid.sh — 5 diagrams OK -->

## Risk & Rollback

- **Risk:** <!-- low / medium / high -->
- **Rollback:** <!-- e.g. git revert <SHA> -->

## Review Focus
<!-- Point reviewer to the 1-2 areas that need careful attention. Include verification steps. -->

-
-

## Checklist

- [ ] Plan included in Summary above
- [ ] Tests pass locally (`./gradlew clean test`)
- [ ] Coverage summary included above
- [ ] Contract tests pass (if CLI commands or API methods changed — see below)
- [ ] Architecture diagram validated (`bash scripts/validate-mermaid.sh`) — if docs changed
- [ ] No new compiler warnings
- [ ] Rollback path documented

## Updating contract golden files

If you added or removed a CLI command or a `Flux` API method, `ContractTest` will fail.
To update:

1. Run `ContractTest` — the failure message shows expected vs. actual.
2. Edit the golden file under `flux-cli/src/test/resources/contract/`:
   - `cli-commands.txt` — one command name per line, sorted alphabetically.
   - `api-methods.txt` — one `methodName=ReturnType` per line, sorted alphabetically.
3. Re-run `ContractTest` and confirm green.

## Track
<!-- Level: Walk / Run / Fly — Exercise: ex# -->

- **Level:** Walk
- **Exercise:**
- [ ] No new compiler warnings
