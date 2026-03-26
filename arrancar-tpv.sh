#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TPV_DIR="$PROJECT_ROOT/tpv"
SQL_FILE="$TPV_DIR/tpv.sql"

if [ ! -f "$TPV_DIR/pom.xml" ]; then
  echo "[ERROR] No se encontro pom.xml en \"$TPV_DIR\"."
  echo "Ejecuta este script desde la raiz del repositorio."
  exit 1
fi

echo ""
echo "================================"
echo "  TPV - Arranque completo"
echo "================================"

# --- Maven ---
if ! command -v mvn &>/dev/null; then
  echo "[ERROR] No se encontro Maven."
  echo "Instala Maven o agrega mvn al PATH."
  exit 1
fi

# --- MySQL/MariaDB client ---
MYSQL_CMD=""
if command -v mysql &>/dev/null; then
  MYSQL_CMD="mysql"
fi

# --- [1/3] Servicio de base de datos ---
echo ""
echo "[1/3] Comprobando servicio de base de datos..."

DB_SERVICE=""
for svc in mysql mariadb mysqld; do
  if systemctl list-units --type=service --all 2>/dev/null | grep -q "${svc}\.service"; then
    DB_SERVICE="$svc"
    break
  fi
done

if [ -n "$DB_SERVICE" ]; then
  if systemctl is-active --quiet "$DB_SERVICE"; then
    echo "[OK] Servicio $DB_SERVICE ya esta en ejecucion."
  else
    echo "[INFO] Intentando arrancar servicio $DB_SERVICE..."
    if sudo systemctl start "$DB_SERVICE" 2>/dev/null; then
      echo "[OK] Servicio $DB_SERVICE arrancado."
    else
      echo "[WARN] No se pudo arrancar el servicio automaticamente."
      echo "[WARN] Si esta parado, inicalo manualmente con: sudo systemctl start $DB_SERVICE"
    fi
  fi
else
  echo "[WARN] No se detecto servicio MySQL/MariaDB en este sistema."
fi

# --- [2/3] Inicializacion opcional de base de datos ---
echo ""
echo "[2/3] Inicializacion opcional de base de datos..."

if [ -n "$MYSQL_CMD" ] && [ -f "$SQL_FILE" ]; then
  echo "[INFO] El script tpv.sql reinicia la base (DROP DATABASE IF EXISTS)."
  read -r -p "Quieres ejecutar tpv.sql ahora? (s/N): " RESP
  case "$RESP" in
    [sS]*)
      read -r -p "Usuario MySQL [root]: " MYSQL_USER
      MYSQL_USER="${MYSQL_USER:-root}"
      read -r -s -p "Password MySQL (deja vacio para prompt interactivo): " MYSQL_PASS
      echo ""

      DB_INIT_OK=true
      if [ -z "$MYSQL_PASS" ]; then
        echo "[INFO] Se abrira prompt de password MySQL..."
        "$MYSQL_CMD" -u "$MYSQL_USER" -p < "$SQL_FILE" || DB_INIT_OK=false
      else
        "$MYSQL_CMD" -u "$MYSQL_USER" -p"$MYSQL_PASS" < "$SQL_FILE" || DB_INIT_OK=false
      fi

      if [ "$DB_INIT_OK" = false ]; then
        echo "[WARN] No se pudo ejecutar tpv.sql."
        echo "[WARN] Continuo con el arranque de la app."
      else
        echo "[OK] Base de datos inicializada correctamente."
      fi
      ;;
    *)
      echo "[INFO] Se omite reinicializacion de BD."
      ;;
  esac
elif [ -z "$MYSQL_CMD" ]; then
  echo "[WARN] No se encontro cliente mysql. Se omite carga automatica de BD."
else
  echo "[WARN] No se encontro $SQL_FILE."
fi

# --- [3/3] Arranque de la aplicacion ---
cd "$TPV_DIR"
echo ""
echo "[3/3] Iniciando TPV..."

if ! mvn clean javafx:run; then
  echo ""
  echo "[ERROR] No se pudo iniciar la aplicacion."
  echo "Verifica Maven, MySQL y credenciales de conexion en el codigo."
  exit 1
fi
