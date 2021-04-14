DATABASE_NAME=$1
HOST=$2
SCHEMA_SCRIPT=$3
postgres | psql -h "$HOST" -U postgres -c "DROP DATABASE IF EXISTS ${DATABASE_NAME}"
psql -h "$HOST" -U postgres -c "CREATE DATABASE ${DATABASE_NAME}"
psql -h "$HOST" -U postgres -d "$DATABASE_NAME" -f "$SCHEMA_SCRIPT"