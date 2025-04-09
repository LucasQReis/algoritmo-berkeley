
# Berkeley

# Equipe

lucas queiroz
maria júlia testoni
micael luan conti
selmo werner

## Compilar
`javac -d . src\berkeley\*.java`

## Iniciar

1 - Subir o RMI: 
`start cmd /k "cd . && start rmiregistry"`

2 - Subir o Servidor:
`java berkeley.BerkeleyServer --porta`

3 - Subir o Cliente:
`java berkeley.BerkeleyClient --ip --porta`
