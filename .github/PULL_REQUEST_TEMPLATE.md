## Description

<!-- Briefly describe the change and link any related issues. -->

## Coverage

<!-- Run `./gradlew clean testCodeCoverageReport` then `bash scripts/print-coverage-summary.sh` and paste the output below. -->

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

## Checklist

- [ ] Tests pass locally (`./gradlew clean test`)
- [ ] Coverage report generated (`./gradlew clean testCodeCoverageReport`)
- [ ] Coverage summary included above
- [ ] Architecture diagram validated (`bash scripts/validate-mermaid.sh`) — if docs changed
- [ ] No new compiler warnings
