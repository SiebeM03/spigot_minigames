@echo off
setlocal

call _config.bat

echo === Deleting /plugins/%PLUGIN_NAME% folder ===
rmdir /s /q "%SERVER_DIR%\plugins\%PLUGIN_NAME%"

endlocal