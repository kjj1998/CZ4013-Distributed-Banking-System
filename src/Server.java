import objects.Account;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static functionalities.ServerInterface.*;
import static utils.Constants.*;
import static utils.SocketFunctions.*;
import static utils.UtilityFunctions.byteArrayToInt;
import static utils.UtilityFunctions.marshall;

public class Server {
    public static Map<Integer, Account> accMapping = new HashMap<>();       // maintain a mapping of account numbers to all accounts currently on the server
    public static void main(String[] args) {
        System.out.println("Server started on port " + SERVER_PORT_NUMBER);
        byte[] buffer = new byte[BUFFER_SIZE];
        byte[] reply = new byte[BUFFER_SIZE];
        byte[] data;
        DatagramPacket request = null;
        String messageID;

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                request = receiveRequest(buffer);                                                   // listen for requests from clients
                assert request != null : "Data from client is null";
                data = request.getData();                                                           // get the data from the request DatagramPacket

                messageID = new String(Arrays.copyOfRange(data, 0, MESSAGE_ID_LENGTH));             // retrieve the unique message id
                System.out.printf("\nmessageID: %s\n", messageID);

                int action = byteArrayToInt(Arrays.copyOfRange(data, MESSAGE_ID_LENGTH, MESSAGE_INFO_START_INDEX));    // get the action to be taken by the server
                byte[] info = Arrays.copyOfRange(data, MESSAGE_INFO_START_INDEX, data.length);                         // get the information from the client

                /* switch statement to select the action to be taken by the server */
                switch (action) {
                    case ACC_CREATION_CODE:
                        System.out.println("Creating account...");
                        reply = processAccCreation(info, accMapping);
                        System.out.println("Account created");
                        break;
                    case ACC_BALANCE_CODE:
                        System.out.println("Querying account balance...");
                        reply = processAccBalanceQuery(info, accMapping);
                        System.out.println("Account balance queried");
                        break;
                    case ACC_CLOSING_CODE:
                        System.out.println("Closing account...");
                        reply = processAccClosure(info, accMapping);
                        System.out.println("Account closed");
                        break;
                }

                sendReply(request, reply);      // send to client the reply message
            } catch (IllegalArgumentException validationError) {
                if (Objects.equals(validationError.getMessage(), NOT_FOUND)) {
                    assert request != null;
                    sendReply(request, marshall(NOT_FOUND));
                    System.out.println("Error: Account Number not found");
                } else if (Objects.equals(validationError.getMessage(), UNAUTHORIZED)) {
                    assert request != null;
                    sendReply(request, marshall(UNAUTHORIZED));
                    System.out.println("Error: Wrong name/password entered.");
                }
            } finally {
                reply = new byte[BUFFER_SIZE];         // reset buffers
                buffer = new byte[BUFFER_SIZE];
            }
        }
    }
}
