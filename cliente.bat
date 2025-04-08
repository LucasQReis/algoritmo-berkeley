@echo off
set IP_SERVIDOR=127.0.0.1

javac -d . src\berkeley\*.java

start cmd /k "java berkeley.BerkeleyCliente Cliente1 %IP_SERVIDOR%"
