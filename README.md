
# Berkeley

## Compilar
`javac -d . src\berkeley\*.java`

## Iniciar

1 - Subir o RMI: 
`start cmd /k "cd . && start rmiregistry"`

2 - Subir o Servidor:
`java berkeley.BerkeleyServer 127.0.0.1`

3 - Subir o Cliente:
`java berkeley.BerkeleyCliente Cliente1 127.0.0.1`
