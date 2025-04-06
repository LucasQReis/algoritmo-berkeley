@echo off
setlocal

javac -d . src\berkeley\*.java

start cmd /k "cd . && start rmiregistry"

timeout /t 2 > nul

start cmd /k "java berkeley.BerkeleyCliente Cliente1"
start cmd /k "java berkeley.BerkeleyCliente Cliente2"
start cmd /k "java berkeley.BerkeleyCliente Cliente3"

timeout /t 3 > nul

start cmd /k "java berkeley.BerkeleyServer"

endlocal
