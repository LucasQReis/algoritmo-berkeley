package berkeley;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BerkeleyInterface extends Remote {
    long setDiferencaTempo(long horarioServidor) throws RemoteException;
    void setAjustarRelogio(long diferenca) throws RemoteException;
    String getNome() throws RemoteException;
}
