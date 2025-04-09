package berkeley;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BerkeleyInterface extends Remote {
    long getClockTime() throws RemoteException;
    void adjustClock(long offset) throws RemoteException;
    void registerClient(String clientHost) throws RemoteException;
}
