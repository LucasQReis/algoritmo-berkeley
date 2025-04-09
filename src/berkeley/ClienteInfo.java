package berkeley;

import java.io.Serializable;

public class ClienteInfo implements Serializable{
    String ip;
    int porta;

    public ClienteInfo(String ip, int porta){
        this.ip = ip;
        this.porta = porta;
    }

    @Override
    public String toString() {
        return ip + ":" + porta;
    }
} 
