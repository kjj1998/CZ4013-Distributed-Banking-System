package utils;

import objects.Account;
import objects.Pointer;

import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;

import static utils.ClientMessage.DisplayAccountDetailsMonitoring;
import static utils.Constants.*;
import static utils.MarshallFunctions.unmarshall;
import static utils.MarshallFunctions.unmarshallAccount;

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

            //If exceed timeout period, exception will be raised
            aSocket.setSoTimeout(atLeastOnceTimeout); //2000s set inside constants.java
            aSocket.receive(reply);
            return reply.getData();
        }
        catch(SocketTimeoutException e) {
            return null;    // so that the parent function can resend this request
        }
        catch (Exception e) {
            System.out.println();
        }

        return null;
    }

    /**
     * Function to send a request to the server for the client to added into the monitoring list
     *
     * @param marshall the byte array to be sent over
     * @param aSocket the DatagramSocket opened
     * @return the reply message from the server
     */
    public static byte[] sendRequestForMonitoring(byte[] marshall, DatagramSocket aSocket) {
        try {
            InetAddress aHost = InetAddress.getByName(HOST_NAME);     // translate user-specified hostname to Internet address

            DatagramPacket request = new DatagramPacket(marshall, marshall.length, aHost, SERVER_PORT_NUMBER);
            aSocket.send(request);

            byte[] buffer = new byte[BUFFER_SIZE];     // a buffer for receive
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            //If exceed timeout period, exception will be raised
            aSocket.setSoTimeout(atLeastOnceTimeout); //2000s set inside constants.java
            aSocket.receive(reply);
            return reply.getData();
        }
        catch(SocketTimeoutException e) {
            return null;    // so that parent function can resend this request
        }
        catch (Exception e) {
            System.out.println();
        }
        return null;
    }

    /**
     * Function for client to monitor updates from the server
     * Timekeeping will be done on the client side where we calculate the future time by which the monitoring should be stopped
     * As DatagramSocket.receive() function is blocking, we set it to check for replies from the server for 1 millisecond
     * using DatagramSocket.setSoTimeout(), then switch to check if monitoring should be stopped and then repeats checking
     * for replies from the server.
     *
     * @param duration amount in seconds to monitor server
     * @param aSocket the DatagramSocket opened to receive data from the server
     * @exception IOException for unknown exceptions thrown
     */
    public static void monitorServer(int duration, DatagramSocket aSocket) throws IOException {
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(duration);
        while (LocalDateTime.now().isBefore(endTime)) {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];     // a buffer for receive
                DatagramPacket update = new DatagramPacket(buffer, buffer.length);
                aSocket.setSoTimeout(1);
                aSocket.receive(update);

                Pointer pointer = new Pointer(0);
                String statusCode = unmarshall(pointer, update.getData());
                switch (statusCode) {
                    case OK: {
                        Account acc = unmarshallAccount(pointer, update.getData());
                        DisplayAccountDetailsMonitoring(acc.getAccNumber(), acc.getName(), acc.getCurrency(), acc.getAccBalance(), acc.getAction());
                        System.out.println("Monitoring updates...");
                        System.out.println();
                        break;
                    }
                    /* the below cases will not happen */
                    case NOT_FOUND:
                        throw new IllegalArgumentException(NOT_FOUND);
                    case UNAUTHORIZED:
                        throw new IllegalArgumentException(UNAUTHORIZED);
                    default:
                        throw new Exception();
                }
            } catch (SocketTimeoutException ignored) {
                // to exit checking for replies from the server
            } catch (Exception e) {
                throw new IOException();
            }
        }
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
    public static void sendReply(DatagramPacket request, byte[] reply, float failureProb) {
        try (DatagramSocket aSocket = new DatagramSocket(SERVER_PORT_NUMBER)) {
            DatagramPacket replyPacket = new DatagramPacket(reply, reply.length,
                    request.getAddress(), request.getPort());
            aSocket.send(replyPacket);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Function for the Observer objects to notify the clients they represent of the updates taking place in the server
     * @param reply the byte array containing the update to be sent to the client
     * @param ip ip address of client
     * @param port port the client is listening on
     */
    public static void sendMonitorReply(byte[] reply, InetAddress ip, int port) {
        try (DatagramSocket aSocket = new DatagramSocket(SERVER_PORT_NUMBER)) {
            DatagramPacket replyPacket = new DatagramPacket(reply, reply.length,
                    ip, port);
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
