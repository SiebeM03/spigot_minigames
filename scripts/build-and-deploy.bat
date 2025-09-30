@echo off
setlocal

call _config.bat


echo === Building plugin via Maven ===
cd /d "%PROJECT_DIR%"
call mvn clean install

IF %ERRORLEVEL% NEQ 0 (
    echo Maven build failed. Aborting.
    exit /b %ERRORLEVEL%
)
echo.

echo === Copying plugin to server plugins folder ===
copy /Y "%PROJECT_DIR%\target\%MAVEN_PROJECT_NAME%-%MAVEN_PROJECT_VERSION%.jar" "%SERVER_DIR%\plugins\"
echo.

@REM echo === Launching Minecraft server ===
@REM cd /d "%SERVER_DIR%"
@REM java -Xms2048M -Xmx2048M -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1HeapRegionSize=8M -XX:G1HeapWastePercent=5 -XX:G1MaxNewSizePercent=40 -XX:G1MixedGCCountTarget=4 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1NewSizePercent=30 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=15 -XX:MaxGCPauseMillis=200 -XX:MaxTenuringThreshold=1 -XX:SurvivorRatio=32 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -jar paper.jar nogui
@REM echo.

endlocal