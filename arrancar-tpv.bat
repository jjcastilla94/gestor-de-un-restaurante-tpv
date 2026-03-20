@echo off
setlocal

set "PROJECT_ROOT=%~dp0"
set "TPV_DIR=%PROJECT_ROOT%tpv"
set "SQL_FILE=%TPV_DIR%\tpv.sql"

if not exist "%TPV_DIR%\pom.xml" (
  echo [ERROR] No se encontro pom.xml en "%TPV_DIR%".
  echo Ejecuta este script desde la raiz del repositorio.
  pause
  exit /b 1
)

echo.
echo ================================
echo   TPV - Arranque completo
echo ================================

set "MVN_CMD=mvn"
where mvn >nul 2>nul
if errorlevel 1 (
  if exist "C:\Program Files\Maven\mvn\bin\mvn.cmd" (
    set "MVN_CMD=C:\Program Files\Maven\mvn\bin\mvn.cmd"
  ) else (
    echo [ERROR] No se encontro Maven.
    echo Instala Maven o agrega mvn al PATH.
    pause
    exit /b 1
  )
)

set "MYSQL_CMD=mysql"
where mysql >nul 2>nul
if errorlevel 1 (
  set "MYSQL_CMD="
  if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
  if not defined MYSQL_CMD if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
  if not defined MYSQL_CMD if exist "C:\Program Files\MariaDB 11.4\bin\mysql.exe" set "MYSQL_CMD=C:\Program Files\MariaDB 11.4\bin\mysql.exe"
)

echo.
echo [1/3] Comprobando servicio de base de datos...
set "MYSQL_SERVICE="
for /f "usebackq delims=" %%S in (`powershell -NoProfile -Command "(Get-Service ^| Where-Object { $_.Name -match 'mysql^|maria' -or $_.DisplayName -match 'MySQL^|MariaDB' } ^| Select-Object -First 1 -ExpandProperty Name)"`) do set "MYSQL_SERVICE=%%S"

if defined MYSQL_SERVICE (
  for /f "usebackq delims=" %%T in (`powershell -NoProfile -Command "(Get-Service -Name '%MYSQL_SERVICE%').Status"`) do set "MYSQL_STATUS=%%T"
  if /i "%MYSQL_STATUS%"=="Running" (
    echo [OK] Servicio %MYSQL_SERVICE% ya esta en ejecucion.
  ) else (
    echo [INFO] Intentando arrancar servicio %MYSQL_SERVICE%...
    powershell -NoProfile -Command "Start-Service -Name '%MYSQL_SERVICE%'" >nul 2>nul
    if errorlevel 1 (
      echo [WARN] No se pudo arrancar el servicio automaticamente.
      echo [WARN] Si esta parado, inicalo manualmente como administrador.
    ) else (
      echo [OK] Servicio %MYSQL_SERVICE% arrancado.
    )
  )
) else (
  echo [WARN] No se detecto servicio MySQL/MariaDB en Windows.
)

echo.
echo [2/3] Inicializacion opcional de base de datos...
if defined MYSQL_CMD (
  if exist "%SQL_FILE%" (
    echo [INFO] El script tpv.sql reinicia la base (DROP DATABASE IF EXISTS).
    choice /C SN /N /M "Quieres ejecutar tpv.sql ahora? (S/N): "
    if errorlevel 2 (
      echo [INFO] Se omite reinicializacion de BD.
    ) else (
      set "MYSQL_USER=root"
      set "MYSQL_PASS="
      set /p MYSQL_USER=Usuario MySQL [root]: 
      if "%MYSQL_USER%"=="" set "MYSQL_USER=root"
      set /p MYSQL_PASS=Password MySQL (deja vacio para prompt): 

      if "%MYSQL_PASS%"=="" (
        echo [INFO] Se abrira prompt de password MySQL...
        "%MYSQL_CMD%" -u "%MYSQL_USER%" -p < "%SQL_FILE%"
      ) else (
        "%MYSQL_CMD%" -u "%MYSQL_USER%" -p"%MYSQL_PASS%" < "%SQL_FILE%"
      )

      if errorlevel 1 (
        echo [WARN] No se pudo ejecutar tpv.sql.
        echo [WARN] Continuo con el arranque de la app.
      ) else (
        echo [OK] Base de datos inicializada correctamente.
      )
    )
  ) else (
    echo [WARN] No se encontro %SQL_FILE%.
  )
) else (
  echo [WARN] No se encontro cliente mysql. Se omite carga automatica de BD.
)

cd /d "%TPV_DIR%"
echo.
echo [3/3] Iniciando TPV...
echo Iniciando TPV...

if /i "%MVN_CMD%"=="mvn" (
  mvn clean javafx:run
) else (
  call "%MVN_CMD%" clean javafx:run
)

if errorlevel 1 (
  echo.
  echo [ERROR] No se pudo iniciar la aplicacion.
  echo Verifica Maven, MySQL y credenciales de conexion en el codigo.
  pause
  exit /b 1
)

endlocal
