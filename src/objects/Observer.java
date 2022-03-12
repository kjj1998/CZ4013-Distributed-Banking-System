package objects;

import java.net.InetAddress;

import static utils.SocketFunctions.sendMonitorReply;

public class Observer {

    private final InetAddress ip;
    private final int port;

    public Observer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void notify(byte[] reply) {
        System.out.println(ip);
        System.out.println(port);
        sendMonitorReply(reply, ip, port);
    }
}
