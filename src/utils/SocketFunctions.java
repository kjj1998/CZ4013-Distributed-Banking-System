package utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static utils.Constants.*;
import static utils.Constants.SERVER_PORT_NUMBER;

public class SocketFunctions {
    /**
     * Function to send data in the form of a byte array to the server for processing
     *
     * @param marshall the byte array to be sent over
     * @return the reply message from the server
     */
    public static byte[] sendRequest(byte[] marshall) {
        try (DatagramSocket aSocket = new DatagramSocket()) {
            InetAddress aHost = InetAddress.getByName(HOST_NAME);     // translate user-specified hostname to Internet address

            DatagramPacket request = new DatagramPacket(marshall, marshall.length, aHost, SERVER_PORT_NUMBER);
            aSocket.send(request);

            byte[] buffer = new byte[BUFFER_SIZE];     // a buffer for receive
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);

            return reply.getData();
        } catch (Exception e) {
            System.out.println();
        }
        return null;
    }

    /**
     * Function to receive requests from the clients
     *
     * @param buffer byte array to be used in the DatagramPacket
     * @return DatagramPacket with data from client
     */
    public static DatagramPacket receiveRequest(byte[] buffer) {
        try (DatagramSocket aSocket = new DatagramSocket(SERVER_PORT_NUMBER)) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(request);
            return request;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    /**
     * Function to send reply from server to client
     *
     * @param request Original DatagramPacket from client
     * @param reply   byte array to for the reply DatagramPacket
     */
    public static void sendReply(DatagramPacket request, byte[] reply) {
        try (DatagramSocket aSocket = new DatagramSocket(6789)) {
            DatagramPacket replyPacket = new DatagramPacket(reply, reply.length,
                    request.getAddress(), request.getPort());
            aSocket.send(replyPacket);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    /**
     * Function to send reply form server to client
     *
     * @param reply Reply in the form of a datagram packet
     */
    public static void sendReply(DatagramPacket reply){
        try (DatagramSocket aSocket = new DatagramSocket(6789)) {
            aSocket.send(reply);
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}
