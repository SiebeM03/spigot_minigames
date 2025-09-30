@echo off
setlocal

call _config.bat

echo === Sending /rl confirm via RCON ===
"%MCRCON_PATH%" -H %RCON_HOST% -P %RCON_PORT% -p %RCON_PASS% "reload confirm"
echo.