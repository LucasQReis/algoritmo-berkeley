package berkeley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class BerkeleyServer extends UnicastRemoteObject implements BerkeleyInterface {

    private long localClock;
    private List<String> clientHosts = new ArrayList<>();

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

        // Sincroniza os relógios após o tempo de registro
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

        long soma = 0;
        for (long t : tempos)
            soma += t;
        long media = soma / tempos.size();

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

    public static void main(String[] args) {
        try {
            int portaServidor = 9070;
            int portaCliente = 9060;
            BerkeleyServer coordinator = new BerkeleyServer();
            Registry registry = LocateRegistry.createRegistry(portaServidor);
            registry.rebind("ClockService", coordinator);
            String localIp = InetAddress.getLocalHost().getHostAddress();
            System.out.println("IP do servidor: " + localIp);
            System.out.println("Servidor (Coordenador) pronto e aguardando registros...");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                System.out.println("\nPressione ENTER para iniciar a sincronização...");
                reader.readLine();
                Sincroniza(coordinator,portaCliente);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}