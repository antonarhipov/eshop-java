#!/usr/bin/env bash
# Plan B — Drop/recreate local Postgres DB and re-run Flyway migrations
# Usage:
#   ./scripts/plan_b_reset_db.sh
# Optional env overrides:
#   DB_HOST (default: localhost)
#   DB_PORT (default: 5432)
#   DB_NAME (default: mydatabase)
#   DB_USER (default: myuser)
#   DB_PASSWORD (default: secret)
#   SPRING_PROFILE (default: dev)
#   DRY_RUN=1 (preview commands without executing)

set -euo pipefail

# Defaults aligned with src/main/resources/application.yml (dev profile)
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-mydatabase}
DB_USER=${DB_USER:-myuser}
DB_PASSWORD=${DB_PASSWORD:-secret}
SPRING_PROFILE=${SPRING_PROFILE:-dev}
DRY_RUN=${DRY_RUN:-0}

say() { echo "[Plan B] $*"; }
run() {
  if [[ "$DRY_RUN" == "1" ]]; then
    say "DRY RUN: $*"
  else
    eval "$@"
  fi
}

# Preconditions
if ! command -v psql >/dev/null 2>&1; then
  say "ERROR: psql not found. Install PostgreSQL client tools first (e.g., brew install libpq && brew link --force libpq)."
  exit 1
fi

if [[ ! -f "gradlew" ]]; then
  say "ERROR: gradlew not found. Run from the repo root."
  exit 1
fi

say "Target: postgresql://${DB_USER}:***@${DB_HOST}:${DB_PORT}/${DB_NAME} (profile=${SPRING_PROFILE})"

# 1) Drop and recreate the database using the admin database 'postgres'
PSQL_URL="postgresql://${DB_USER}:${DB_PASSWORD}@${DB_HOST}:${DB_PORT}/postgres"

say "Dropping database '${DB_NAME}' if exists..."
run "PGOPTIONS='-v ON_ERROR_STOP=1' psql '${PSQL_URL}' -c \"SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='${DB_NAME}' AND pid <> pg_backend_pid();\""
run "PGOPTIONS='-v ON_ERROR_STOP=1' psql '${PSQL_URL}' -c \"DROP DATABASE IF EXISTS ${DB_NAME};\""

say "Creating database '${DB_NAME}'..."
run "PGOPTIONS='-v ON_ERROR_STOP=1' psql '${PSQL_URL}' -c \"CREATE DATABASE ${DB_NAME};\""

# 2) Run Flyway migrations via Gradle
say "Running Flyway migrations..."
run "./gradlew -Dspring.profiles.active=${SPRING_PROFILE} flywayMigrate --no-daemon"

say "Done. Database has been reset and migrations reapplied."
