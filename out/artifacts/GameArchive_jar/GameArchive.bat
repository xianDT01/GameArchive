@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
set "PATH_TO_FX=%SCRIPT_DIR%javafx-sdk-22.0.1\lib"
java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -jar "%SCRIPT_DIR%GameArchive.jar"
endlocal
pause
