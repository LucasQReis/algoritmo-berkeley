package berkeley;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.net.ServerSocket;
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

    public void registerClient(ClienteInfo clientInfo) throws java.rmi.RemoteException {
        // não usado no cliente
    }

    public static int getPortaLivre() {
    try (ServerSocket socket = new ServerSocket(0)) {
        return socket.getLocalPort();
    } catch (Exception e) {
        throw new RuntimeException("Não foi possível obter uma porta livre.", e);
    }
}

    public static void main(String[] args) {
        try {
            BerkeleyClient client = new BerkeleyClient();

            String ipServidor = args[0];
            int portaServidor = Integer.parseInt(args[1]);
            int portaCliente = getPortaLivre();

            Registry registry = LocateRegistry.createRegistry(portaCliente);
            registry.rebind("ClockService", client);

            // Registrar-se no coordenador
            Registry serverRegistry = LocateRegistry.getRegistry(ipServidor, portaServidor);
            BerkeleyInterface stub = (BerkeleyInterface) serverRegistry.lookup("ClockService");

            String myIp = InetAddress.getLocalHost().getHostAddress();
            stub.registerClient(new ClienteInfo(myIp, portaCliente));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
