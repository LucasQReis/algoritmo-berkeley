package berkeley;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BerkeleyServer extends UnicastRemoteObject {
    private final Map<BerkeleyInterface, Long> clientes = new HashMap<>();
    private final Date horaServidor = new Date();

    public BerkeleyServer() throws RemoteException {}

    public void sincronizar() throws RemoteException {
        System.out.println("Servidor iniciou: " + horaServidor);

        try {
            String[] nomes = Naming.list("rmi://localhost/");
            for (String nome : nomes) {
                if (!nome.contains("Servidor")) {
                    BerkeleyInterface cliente = (BerkeleyInterface) Naming.lookup(nome);
                    long dif = cliente.setDiferencaTempo(horaServidor.getTime());
                    clientes.put(cliente, dif);
                    System.out.println(cliente.getNome() + " diferença: " + dif + " ms");
                }
            }

            long soma = 0;
            for (long dif : clientes.values()) {
                soma += dif;
            }
            long media = soma / (clientes.size() + 1);
            System.out.println("Média da diferença: " + media + " ms");

            for (Map.Entry<BerkeleyInterface, Long> entry : clientes.entrySet()) {
                long ajuste = media - entry.getValue();
                entry.getKey().setAjustarRelogio(ajuste);
                System.out.println(entry.getKey().getNome() + " ajustado em " + ajuste + " ms");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        BerkeleyServer servidor = new BerkeleyServer();
        Naming.rebind("Servidor", servidor);
        servidor.sincronizar();
    }
}
