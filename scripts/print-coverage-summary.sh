#!/usr/bin/env bash
# scripts/print-coverage-summary.sh
# Parse the JaCoCo aggregate XML report and print a coverage summary.
#
# Usage:
#   ./scripts/print-coverage-summary.sh [REPORT_XML]
#   Default: code-coverage-report/build/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml
set -euo pipefail

REPORT="${1:-code-coverage-report/build/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml}"

if [ ! -f "$REPORT" ]; then
  echo "ERROR: Coverage report not found at $REPORT" >&2
  echo "Run './gradlew clean testCodeCoverageReport' first." >&2
  exit 1
fi

# Use python (available on most CI agents) to parse the XML and summarise.
python3 - "$REPORT" <<'PYEOF'
import sys
import xml.etree.ElementTree as ET

report = sys.argv[1]
tree = ET.parse(report)
root = tree.getroot()

# Collect top-level counters (direct children of <report>)
counters = {}
for counter in root.findall("counter"):
    ctype = counter.get("type")
    missed = int(counter.get("missed", 0))
    covered = int(counter.get("covered", 0))
    total = missed + covered
    pct = (covered / total * 100) if total > 0 else 0
    counters[ctype] = (covered, total, pct)

print("=" * 52)
print(f"  JaCoCo Coverage Summary  —  {report}")
print("=" * 52)
fmt = "  {:<20s} {:>7,d} / {:>7,d}  ({:5.1f}%)"
for ctype in ["INSTRUCTION", "BRANCH", "LINE", "METHOD", "CLASS"]:
    if ctype in counters:
        covered, total, pct = counters[ctype]
        print(fmt.format(ctype, covered, total, pct))

# Print the headline number (instruction coverage)
if "INSTRUCTION" in counters:
    _, _, pct = counters["INSTRUCTION"]
    print()
    print(f"  ** Overall instruction coverage: {pct:.1f}% **")
print("=" * 52)
PYEOF
