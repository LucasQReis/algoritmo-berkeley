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

    public BerkeleyServer() throws RemoteException {
    }

    public void sincronizar(String ip) throws RemoteException {
        System.out.println("Servidor iniciou: " + horaServidor);

        try {
            String[] nomes = Naming.list("rmi://" + ip + "/");
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

    private static void VerificaArgumentos(String[] args) {
        if (args.length < 1) {
            System.out.println("Argumento necessario: IP");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        VerificaArgumentos(args);

        String ip = args[0];
        BerkeleyServer servidor = new BerkeleyServer();

        try {
            Naming.lookup("rmi://" + ip + "/Servidor");
            System.out.println("Objeto com o nome 'Servidor' já exite");
            System.exit(1);
        } catch (NotBoundException e) {
            Naming.rebind("rmi://" + ip + "/Servidor", servidor);

            System.out.println("Servidor registrado no RMI.");
            System.out.println("Aguardando clientes, pressione ENTER para tentar sincronizar.");
            System.in.read();

            String[] nomes = Naming.list("rmi://" + ip + "/");
            int totalClientes = 0;
            for (String nome : nomes) {
                if (!nome.contains("Servidor")) {
                    totalClientes++;
                }
            }

            if (totalClientes < 2) {
                System.out.println("Necessário registrar ao menos 2 clientes antes de sincronizar.");
                Naming.unbind("rmi://" + ip + "/Servidor");
                System.exit(1);
            }

            servidor.sincronizar(ip);
            Naming.unbind("rmi://" + ip + "/Servidor");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
