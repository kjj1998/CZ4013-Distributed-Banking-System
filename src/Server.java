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
    public static Map<Integer, Account> accMapping = new HashMap<>();       // maintain a mapping of account numbers to all acounts currently on the server
    public static void main(String[] args) {
        System.out.println("Server started on port " + SERVER_PORT_NUMBER);
        byte[] buffer = new byte[BUFFER_SIZE];
        byte[] reply = new byte[BUFFER_SIZE];
        byte[] data;
        DatagramPacket request = null;

        while (true) {
            try {
                request = receiveRequest(buffer);                                    // listen for requests from clients
                data = request.getData();                                                           // get the data from the request DatagramPacket
                String messageID = new String(Arrays.copyOfRange(data, 0, MESSAGE_ID_LENGTH));      // retrieve the unique message id
                System.out.println("messageID: " + messageID);
                int action = byteArrayToInt(Arrays.copyOfRange(data, MESSAGE_ID_LENGTH, MESSAGE_INFO_START_INDEX));    // get the action to be taken by the server
                byte[] info = Arrays.copyOfRange(data, MESSAGE_INFO_START_INDEX, data.length);                         // get the information from the client

                /* switch statement to select the action to be taken by the server */
                switch (action) {
                    case ACC_CREATION_CODE:
                        reply = processAccCreation(info, accMapping);                   // call the function to process the account creation
                        break;
                    case ACC_BALANCE_CODE:
                        reply = processAccBalanceQuery(info, accMapping);
                        break;
                    case ACC_CLOSING_CODE:
                        reply = processAccClosure(info, accMapping);
                        break;
                }
                sendReply(request, reply);      // send to client the reply message
            } catch (IllegalArgumentException validationError) {
                if (Objects.equals(validationError.getMessage(), NOT_FOUND)) {
                    sendReply(request, marshall(NOT_FOUND));
                    System.out.println("Error: Account Number not found");
                } else if (Objects.equals(validationError.getMessage(), UNAUTHORIZED)) {
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
