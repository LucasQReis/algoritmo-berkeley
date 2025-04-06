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
        if (!nome.isEmpty()) {
            this.horarioLocal = new Date(System.currentTimeMillis() + (long) (Math.random() * 120000 - 60000));
            System.out.println(nome + " iniciado com horário: " + horarioLocal);
        }
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
