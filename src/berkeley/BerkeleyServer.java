package berkeley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class BerkeleyServer extends UnicastRemoteObject implements BerkeleyInterface {
    private long localClock;
    private List<ClienteInfo> clientes = new ArrayList<>();
    private static BerkeleyServer coordinator;

    protected BerkeleyServer() throws RemoteException {
        super();
        localClock = System.currentTimeMillis();
    }

    public long getClockTime() throws RemoteException {
        return localClock;
    }

    public void adjustClock(long offset) throws RemoteException {
        localClock += offset;
        System.out.println("Clock ajustado em " + offset + " ms. Novo tempo: " + new Date(localClock));
    }

    public void registerClient(ClienteInfo clienteInfo) throws RemoteException {
        if (!clientes.contains(clienteInfo)) {
            clientes.add(clienteInfo);
            System.out.println("Cliente registrado: " + clienteInfo);
        }
    }

    public static void Sincroniza(BerkeleyServer coordinator) throws RemoteException, NotBoundException {
        if (coordinator.clientes.isEmpty()) {
            System.out.println("Nenhum cliente registrado ainda.");
            return;
        }

        List<Long> tempos = new ArrayList<>();
        System.out.println("Tempo do servidor " + new Date(coordinator.getClockTime()));
        tempos.add(coordinator.getClockTime());

        for (ClienteInfo cliente : coordinator.clientes) {
            Registry clientReg = LocateRegistry.getRegistry(cliente.ip, cliente.porta);
            BerkeleyInterface stub = (BerkeleyInterface) clientReg.lookup("ClockService");
            long clientTime = stub.getClockTime();
            tempos.add(clientTime);
            System.out.println("Tempo do cliente " + cliente + ": " + new Date(clientTime));
        }

        long media = calculaMedia(tempos);

        long offsetCoordenador = media - coordinator.getClockTime();
        coordinator.adjustClock(offsetCoordenador);

        int i = 1;
        for (ClienteInfo cliente : coordinator.clientes) {
            Registry clientReg = LocateRegistry.getRegistry(cliente.ip, cliente.porta);
            BerkeleyInterface stub = (BerkeleyInterface) clientReg.lookup("ClockService");

            long offset = media - tempos.get(i);
            stub.adjustClock(offset);
            i++;
        }

        System.out.println("Sincronização finalizada.");
    }

    public static long calculaMedia(List<Long> tempos) {
        long soma = 0;
        for (long t : tempos)
            soma += t;
        long media = soma / tempos.size();
        return media;
    }

    public static void iniciaRmi(BerkeleyServer coordinator, int portaServidor)
            throws UnknownHostException, RemoteException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.setProperty("java.rmi.server.hostname", ip);
        Registry registry = LocateRegistry.createRegistry(portaServidor);
        registry.rebind("ClockService", coordinator);

        System.out.println("Servidor (" + ip + ") pronto e aguardando registros...");
    }

    public static void leComando(String comando) throws RemoteException, NotBoundException {
        switch (comando) {
            case "sync":
                Sincroniza(coordinator);
                break;
            case "exit":
                System.exit(1);
                break;
            case "list":
                listaClientes();
                break;
            case "help":
                printaComandosDisponiveis();
                break;
            default:
                System.out.println("Comando " + comando + " não reconhecido");
                break;
        }
    }

    public static void listaClientes() {
        if (coordinator.clientes.isEmpty()) {
            System.out.println("Nenhum cliente registrado ainda.");
            return;
        }

        for (ClienteInfo cliente : coordinator.clientes) {
            System.out.println("Cliente : " + cliente);
        }
    }

    public static void printaComandosDisponiveis() {
        System.out.println("+------------------------------------ +");
        System.out.println("|  Insira um dos comando disponíveis: |");
        System.out.println("+------------------------------------ +");
        System.out.println("| - sync                              |");
        System.out.println("| - list                              |");
        System.out.println("| - help                              |");
        System.out.println("| - exit                              |");
        System.out.println("+------------------------------------ +");
    }

    public static void main(String[] args) {
        try {
            coordinator = new BerkeleyServer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int portaServidor = Integer.parseInt(args[0]);
            iniciaRmi(coordinator, portaServidor);
            printaComandosDisponiveis();

            while (true) {
                String comando = reader.readLine();
                leComando(comando);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}