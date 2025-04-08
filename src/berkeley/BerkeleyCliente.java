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
        if (args.length < 2) {
            System.out.println("Argumentos necessarios: Cliente e IP");
            System.exit(1);
        }

        String nomeCliente = args[0];
        String ip = args[1];

        BerkeleyCliente cliente = new BerkeleyCliente(nomeCliente);
        Naming.rebind("rmi://" + ip + "/" + nomeCliente, cliente);
    }

    protected BerkeleyCliente(String nomeCliente) throws RemoteException {
        this.nome = nomeCliente;

        Scanner scanner = new Scanner(System.in);
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