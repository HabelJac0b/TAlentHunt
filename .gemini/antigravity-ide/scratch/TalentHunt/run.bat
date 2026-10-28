@echo off
echo ========================================================
echo   Building and Starting Talent Hunt System (Java + MySQL)
echo ========================================================
if not exist bin mkdir bin

"C:\Program Files\Android\Android Studio\jbr\bin\javac.exe" -cp "lib/h2.jar;lib/mysql-connector.jar;lib/javax.servlet-api.jar" -d bin src/main/java/com/talenthunt/exception/*.java src/main/java/com/talenthunt/util/*.java src/main/java/com/talenthunt/model/*.java src/main/java/com/talenthunt/db/*.java src/main/java/com/talenthunt/dao/*.java src/main/java/com/talenthunt/service/*.java src/main/java/com/talenthunt/service/impl/*.java src/main/java/com/talenthunt/servlet/*.java src/main/java/com/talenthunt/server/*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b %ERRORLEVEL%
)

echo Starting Talent Hunt Web Server...
"C:\Program Files\Android\Android Studio\jbr\bin\java.exe" -cp "bin;lib/h2.jar;lib/mysql-connector.jar;lib/javax.servlet-api.jar" com.talenthunt.server.TalentHuntServer
