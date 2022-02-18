import objects.Account;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import static functionalities.ServerInterface.processAccBalanceQuery;
import static functionalities.ServerInterface.processAccCreation;
import static utils.Constants.*;
import static utils.SocketFunctions.receiveRequest;
import static utils.SocketFunctions.sendReply;
import static utils.UtilityFunctions.byteArrayToInt;

public class Server {
    public static HashMap<Integer, Account> accMapping = new HashMap<>();       // maintain a mapping of account numbers to all acounts currently on the server
    public static void main(String[] args) {
        System.out.println("Server started on port " + SERVER_PORT_NUMBER);
        byte[] buffer = new byte[BUFFER_SIZE];
        byte[] reply = new byte[BUFFER_SIZE];
        byte[] data;

        while (true) {
            DatagramPacket request = receiveRequest(buffer);                                    // listen for requests from clients
            data = request.getData();                                                           // get the data from the request DatagramPacket
            String messageID = new String(Arrays.copyOfRange(data, 0, MESSAGE_ID_LENGTH));      // retrieve the unique message id
            System.out.println("messageID: " + messageID);
            int action = byteArrayToInt(Arrays.copyOfRange(data, MESSAGE_ID_LENGTH, MESSAGE_INFO_START_INDEX));    // get the action to be taken by the server
            byte[] info = Arrays.copyOfRange(data, MESSAGE_INFO_START_INDEX, data.length);                         // get the information from the client

            /* switch statement to select the action to be taken by the server */
            switch (action) {
                /* Acc creation */
                case ACC_OPENING_CODE:
                    int accNumber = processAccCreation(info, accMapping);                   // call the function to process the account creation
                    reply = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(accNumber).array();   // convert the account number into a byte[] array to be sent to client
                    break;
                /* Check current acc balance */
                case ACC_BALANCE_CODE:
                    double currentBalance = processAccBalanceQuery(info, accMapping);
                    System.out.println("current balance: " + currentBalance);
                    reply = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_DOUBLE).putDouble(currentBalance).array();
                    break;
            }

            sendReply(request, reply);      // send to client the reply message
            reply = new byte[BUFFER_SIZE];         // reset buffers
            buffer = new byte[BUFFER_SIZE];
        }
    }
}
