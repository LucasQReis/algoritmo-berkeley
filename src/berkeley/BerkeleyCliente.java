package berkeley;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Scanner;

public class BerkeleyCliente extends UnicastRemoteObject implements BerkeleyInterface {
    private Date horarioLocal;
    private final String nome;

    public static void main(String[] args) throws Exception {
        try {
            BerkeleyCliente cliente = new BerkeleyCliente();
            Naming.rebind(cliente.nome, cliente);
        } catch (RemoteException e) {
            throw new IllegalArgumentException("Informe o nome do cliente: " + e.getMessage());
        }
    }

    protected BerkeleyCliente() throws RemoteException {
        Scanner scanner = new Scanner(System.in);
    
        System.out.println("Digite o nome do cliente: ");
        this.nome = scanner.nextLine();
    
        if (nome.isEmpty()) {
            System.out.println("Nome do cliente não pode estar vazio.");
            System.exit(1);
        }
        System.out.println("Digite o horário local no formato HH:mm:ss");
        String horarioInput = scanner.nextLine();
    
        if (horarioInput.isEmpty()) {
            this.horarioLocal = new Date(System.currentTimeMillis() + (long) (Math.random() * 120000 - 60000));
        } else {
            try {
                String[] partes = horarioInput.split(":");
                int horas = Integer.parseInt(partes[0]);
                int minutos = Integer.parseInt(partes[1]);
                int segundos = Integer.parseInt(partes[2]);
                Date agora = new Date();
                this.horarioLocal = new Date(
                    agora.getYear(),agora.getMonth(),agora.getDate(),horas,minutos,segundos
                );
            } catch (Exception e) {
                System.out.println("Formato inválido. Gerando horário aleatório.");
                this.horarioLocal = new Date(System.currentTimeMillis() + (long) (Math.random() * 120000 - 60000));
            }
        }
    
        System.out.println(nome + " iniciado com horário: " + horarioLocal);
    }
    
    

    @Override
    public long setDiferencaTempo(long horarioServidor) throws RemoteException {
        long dif = horarioLocal.getTime() - horarioServidor;
        return dif;
    }

    @Override
    public void setAjustarRelogio(long diferenca) {
        horarioLocal = new Date(horarioLocal.getTime() + diferenca);
        System.out.println(nome + " horário ajustado: " + horarioLocal);
    }

    @Override
    public String getNome() throws RemoteException {
        return nome;
    }
}