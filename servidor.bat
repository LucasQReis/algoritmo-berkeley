@echo off
set IP_LOCAL=127.0.0.1

javac -d . src\berkeley\*.java

start cmd /k "cd . && rmiregistry"

timeout /t 2 > nul
.
start cmd /k "java berkeley.BerkeleyServer %IP_LOCAL%"
