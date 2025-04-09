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
    private List<String> clientHosts = new ArrayList<>();
    private static BerkeleyServer coordinator;
    private static int portaCliente = 9060;

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

    public void registerClient(String clientHost) throws RemoteException {
        if (!clientHosts.contains(clientHost)) {
            clientHosts.add(clientHost);
            System.out.println("Cliente registrado: " + clientHost);
        }
    }

    public static void Sincroniza(BerkeleyServer coordinator, int porta) throws RemoteException, NotBoundException {
        if (coordinator.clientHosts.isEmpty()) {
            System.out.println("Nenhum cliente registrado ainda.");
            return;
        }

        List<Long> tempos = new ArrayList<>();
        System.out.println("Tempo do servidor " + new Date(coordinator.getClockTime()));
        tempos.add(coordinator.getClockTime());

        for (String ip : coordinator.clientHosts) {
            Registry clientReg = LocateRegistry.getRegistry(ip, porta);
            BerkeleyInterface stub = (BerkeleyInterface) clientReg.lookup("ClockService");
            long clientTime = stub.getClockTime();
            tempos.add(clientTime);
            System.out.println("Tempo do cliente " + ip + ": " + new Date(clientTime));
        }

        long media = calculaMedia(tempos);

        long offsetCoordenador = media - coordinator.getClockTime();
        coordinator.adjustClock(offsetCoordenador);

        int i = 1;
        for (String ip : coordinator.clientHosts) {
            Registry clientReg = LocateRegistry.getRegistry(ip, porta);
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

    public static void iniciaRmi(BerkeleyServer coordinator) throws UnknownHostException, RemoteException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.setProperty("java.rmi.server.hostname", ip);
        int portaServidor = 9070;
        Registry registry = LocateRegistry.createRegistry(portaServidor);
        registry.rebind("ClockService", coordinator);

        System.out.println("Servidor (Coordenador) pronto e aguardando registros..." + ip);
    }

    public static void leComando(String comando) throws RemoteException, NotBoundException{
        switch (comando) {
            case "sync":
                Sincroniza(coordinator, portaCliente);
                break;
            case "exit":
                System.exit(1);
            default:
                System.out.println("Comando " + comando + " não reconhecido");
                break;
        }
    }

    public static void printaComandosDisponiveis(){
        System.out.println("+------------------------------------ +");
        System.out.println("|  Insira um dos comando disponíveis: |");
        System.out.println("+------------------------------------ +");
        System.out.println("| - sync                              |");
        System.out.println("| - exit                              |");
        System.out.println("+------------------------------------ +");
    }

    public static void main(String[] args) {
        try {
            coordinator = new BerkeleyServer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            iniciaRmi(coordinator);
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