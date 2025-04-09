package berkeley;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.util.Date;

public class BerkeleyClient extends UnicastRemoteObject implements BerkeleyInterface {

    private long localClock;

    protected BerkeleyClient() throws java.rmi.RemoteException {
        super();
        localClock = System.currentTimeMillis() + (long)(Math.random() * 10000 - 5000);
        System.out.println("Cliente iniciado com clock: " + new Date(localClock));
    }

    public long getClockTime() throws java.rmi.RemoteException {
        return localClock;
    }

    public void adjustClock(long offset) throws java.rmi.RemoteException {
        localClock += offset;
        System.out.println("Clock ajustado em " + offset + " ms. Novo tempo: " + new Date(localClock));
    }

    public void registerClient(String clientHost) throws java.rmi.RemoteException {
        // não usado no cliente
    }

    public static void main(String[] args) {
        try {
            BerkeleyClient client = new BerkeleyClient();
            int portaServidor = 9070;
            int portaCliente = 9060;
            String ipServidor = args[0];
            Registry registry = LocateRegistry.createRegistry(portaCliente);
            registry.rebind("ClockService", client);

            // Registrar-se no coordenador
            Registry serverRegistry = LocateRegistry.getRegistry(ipServidor, portaServidor);
            BerkeleyInterface stub = (BerkeleyInterface) serverRegistry.lookup("ClockService");

            String myIp = InetAddress.getLocalHost().getHostAddress();
            stub.registerClient(myIp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
