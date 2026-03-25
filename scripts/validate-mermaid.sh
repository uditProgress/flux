#!/usr/bin/env bash
# validate-mermaid.sh — Validate all Mermaid diagrams in docs/architecture.md
# Requires: @mermaid-js/mermaid-cli (mmdc)
#
# Usage:
#   ./scripts/validate-mermaid.sh [FILE]
#   Default FILE: docs/architecture.md
set -euo pipefail

FILE="${1:-docs/architecture.md}"

if [ ! -f "$FILE" ]; then
  echo "ERROR: $FILE not found" >&2
  exit 1
fi

# Check mmdc is available; install locally if not
if ! command -v mmdc &>/dev/null && [ ! -x ./node_modules/.bin/mmdc ]; then
  echo "Installing @mermaid-js/mermaid-cli..."
  npm install --no-save @mermaid-js/mermaid-cli
fi

MMDC="mmdc"
if ! command -v mmdc &>/dev/null; then
  MMDC="./node_modules/.bin/mmdc"
fi

TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT

# Extract fenced mermaid blocks
INDEX=0
FAILED=0
while IFS= read -r line; do
  if [[ "$line" == '```mermaid' ]]; then
    INDEX=$((INDEX + 1))
    BLOCK_FILE="$TMPDIR/block_${INDEX}.mmd"
    : > "$BLOCK_FILE"
    COLLECTING=true
    continue
  fi
  if [[ "${COLLECTING:-false}" == "true" ]]; then
    if [[ "$line" == '```' ]]; then
      COLLECTING=false
      echo "Validating diagram $INDEX..."
      if "$MMDC" -i "$BLOCK_FILE" -o "$TMPDIR/block_${INDEX}.svg" --quiet 2>&1; then
        echo "  ✓ Diagram $INDEX OK"
      else
        echo "  ✗ Diagram $INDEX FAILED" >&2
        FAILED=$((FAILED + 1))
      fi
    else
      echo "$line" >> "$BLOCK_FILE"
    fi
  fi
done < "$FILE"

if [ "$INDEX" -eq 0 ]; then
  echo "WARNING: No mermaid blocks found in $FILE" >&2
  exit 1
fi

echo ""
echo "Validated $INDEX diagram(s), $FAILED failure(s)."
[ "$FAILED" -eq 0 ] || exit 1
