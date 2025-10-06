#!/usr/bin/env bash
# Flyway REPAIR helper — updates Flyway schema history checksums
# Usage:
#   ./scripts/flyway_repair.sh
# Optional env overrides (defaults mirror src/main/resources/application.yml dev profile):
#   DB_HOST (default: localhost)
#   DB_PORT (default: 5432)
#   DB_NAME (default: mydatabase)
#   DB_USER (default: myuser)
#   DB_PASSWORD (default: secret)
#   DRY_RUN=1 (preview commands without executing)
#
# Notes:
# - This script does NOT drop data. It only runs Flyway REPAIR to fix checksums
#   and failed migration states in flyway_schema_history.
# - After repair, start the app or run `./gradlew flywayMigrate` to apply pending migrations.

set -euo pipefail

DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-mydatabase}
DB_USER=${DB_USER:-myuser}
DB_PASSWORD=${DB_PASSWORD:-secret}
DRY_RUN=${DRY_RUN:-0}

say() { echo "[Flyway Repair] $*"; }
run() {
  if [[ "$DRY_RUN" == "1" ]]; then
    say "DRY RUN: $*"
  else
    eval "$@"
  fi
}

# Preconditions
if [[ ! -f "gradlew" ]]; then
  say "ERROR: gradlew not found. Run from the repo root."
  exit 1
fi

JDBC_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"

say "Target: ${JDBC_URL} (user=${DB_USER})"

# Run Flyway REPAIR via Gradle plugin, passing connection details explicitly.
# Use filesystem location to avoid requiring a built classpath.
GRADLE_CMD=(
  ./gradlew flywayRepair --no-daemon \
    -Dflyway.url="${JDBC_URL}" \
    -Dflyway.user="${DB_USER}" \
    -Dflyway.password="${DB_PASSWORD}" \
    -Dflyway.locations=filesystem:src/main/resources/db/migration \
    -Dflyway.baselineOnMigrate=true \
    -Dflyway.baselineVersion=0 \
    -Dflyway.validateOnMigrate=true
)

run "${GRADLE_CMD[@]}"

say "Repair complete. You may now start the app or run ./gradlew flywayMigrate to apply pending migrations."
