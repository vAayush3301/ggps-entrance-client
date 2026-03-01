@echo off
setlocal enabledelayedexpansion

rem ==============================
rem Config - adjust if needed
rem ==============================
set PROJECT_DIR=D:\CPP\Entrance\Client
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.2
set JFX_JMODS=C:\Program Files\Java\javafx-jmods-21.0.10
set BUILD_DIR=%PROJECT_DIR%\build
set APP_NAME=ExamDesk
set MAIN_CLASS=av.entrance.client.Client
set ICON=%PROJECT_DIR%\src\main\resources\av\entrance\client\images\logos\logo.ico

rem ==============================
rem 1 Create semicolon-separated dependency list (for Class-Path if needed)
rem ==============================
set DEP_JARS=
for %%f in ("%BUILD_DIR%\libs\dependencies\*.jar") do (
    if defined DEP_JARS (
        set DEP_JARS=!DEP_JARS!;%%f
    ) else (
        set DEP_JARS=%%f
    )
)
echo Dependencies found: !DEP_JARS!

rem ==============================
rem 2 Run jpackage - FULL JDK mode (no jlink / no module restrictions)
rem    JavaFX loaded via --java-options
rem ==============================
echo Packaging MSI with full JDK runtime...
"%JAVA_HOME%\bin\jpackage.exe" ^
  --name "%APP_NAME%" ^
  --input "%BUILD_DIR%\libs" ^
  --main-jar "%APP_NAME%-1.0.jar" ^
  --main-class "%MAIN_CLASS%" ^
  --type msi ^
  --icon "%ICON%" ^
  --win-shortcut ^
  --win-menu ^
  --win-dir-chooser ^
  --dest "%BUILD_DIR%\out" ^
  --java-options "--module-path ""%JFX_JMODS%"" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.swing --add-exports jdk.httpserver/com.sun.net.httpserver=ALL-UNNAMED --add-exports java.net.http/java.net.http=ALL-UNNAMED" ^
  --java-options "-Dprism.order=sw" ^
  --verbose ^
  --app-version "1.0" ^
  --vendor "YourNameOrCompany" ^
  --description "ExamDesk Client Application"

if %errorlevel% neq 0 (
    echo jpackage failed! Check the verbose output above.
    pause
    exit /b 1
)

echo ==============================
echo Build complete!
echo MSI created in: %BUILD_DIR%\out\%APP_NAME%-1.0.msi  (or similar)
echo Install it and test the shortcut / Start menu entry.
echo ==============================
pause