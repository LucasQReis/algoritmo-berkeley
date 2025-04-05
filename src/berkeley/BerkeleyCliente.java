package berkeley;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class BerkeleyCliente extends UnicastRemoteObject implements BerkeleyInterface {
    private Date horarioLocal;
    private final String nome;

    public static void main(String[] args) throws Exception {
        BerkeleyCliente cliente = new BerkeleyCliente(args[0]);
        Naming.rebind(args[0], cliente);
    }

    protected BerkeleyCliente(String nome) throws RemoteException {
        this.nome = nome;
        this.horarioLocal = new Date(System.currentTimeMillis() + (long) (Math.random() * 120000 - 60000));
        System.out.println(nome + " iniciado com horário: " + horarioLocal);
    }

    @Override
    public long setDiferencaTempo(long horarioServidor) throws RemoteException {
        long dif = horarioLocal.getTime() - horarioServidor;
        return dif;
    }

    @Override
    public String getNome() throws RemoteException {
        return nome;
    }
}
