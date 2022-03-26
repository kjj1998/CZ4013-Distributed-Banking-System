import objects.Observer;
import objects.Account;
import objects.LruReplyHistory;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;

import static functionalities.ServerInterface.*;
import static utils.Constants.*;
import static utils.MarshallFunctions.marshall;
import static utils.SocketFunctions.receiveRequest;
import static utils.SocketFunctions.sendReply;
import static utils.UtilityFunctions.byteArrayToInt;
import static utils.UtilityFunctions.failMessage;

public class Server {
    public static Map<Integer, Account> accMapping = new HashMap<>();                                               // maintain a mapping of account numbers to all accounts currently on the server
    private static final LruReplyHistory<String, byte[]> replyHistory = new LruReplyHistory<>(LRU_CACHE_SIZE);      // maintain a history of replies base on the least recently used scheme
    private static final Map<String, Observer> observerMap = new HashMap<>();                                       // maintain a mapping of clients who are currently monitoring the server for updates

    public static void main(String[] args) {
        System.out.println("Server started on port " + SERVER_PORT_NUMBER);
        byte[] buffer = new byte[BUFFER_SIZE];
        byte[] reply = new byte[BUFFER_SIZE];
        byte[] data;
        DatagramPacket request = null;
        InetAddress clientIp;
        int clientPort;
        String messageID, clientIdentifier;

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                request = receiveRequest(buffer);                                                   // listen for requests from clients
                assert request != null : "Data from client is null";
                data = request.getData();                                                           // get the data from the request DatagramPacket
                clientIp = request.getAddress();                                                    // get the client ip address
                clientPort = request.getPort();                                                     // get the client port number
                clientIdentifier = clientPort + clientIp.toString();                                // construct the client identifier
                messageID = new String(Arrays.copyOfRange(data, 0, MESSAGE_ID_LENGTH));             // retrieve the unique message id
                System.out.printf("\nmessageID: %s\n", messageID);

                Optional<byte[]> cachedReply = replyHistory.getReply(messageID);    //Check if message reply has already been stored

                int action;
                // if message reply was cached, and we are using At-Most-Once semantics, tell server to send cached reply
                if(cachedReply.isPresent() && !AT_LEAST_ONCE){
                    reply = cachedReply.get();
                    action = CACHED_REPLY;
                }else {
                    action = byteArrayToInt(Arrays.copyOfRange(data, MESSAGE_ID_LENGTH, MESSAGE_INFO_START_INDEX));    // get the action to be taken by the server
                }
                byte[] info = Arrays.copyOfRange(data, MESSAGE_INFO_START_INDEX, data.length);                         // get the information sent from the client


                // switch statement to select the action to be taken by the server
                switch (action) {
                    case ACC_CREATION_CODE: {
                        System.out.println("Creating account...");
                        reply = processAccCreation(info, accMapping);

                        if (!AT_LEAST_ONCE)
                            replyHistory.putReply(messageID, reply);

                        System.out.println("Account created");
                        break;
                    }
                    case ACC_BALANCE_CODE: {
                        System.out.println("Querying account balance...");
                        reply = processAccBalanceQuery(info, accMapping);

                        if (!AT_LEAST_ONCE)
                            replyHistory.putReply(messageID, reply);

                        System.out.println("Account balance queried");
                        break;
                    }
                    case ACC_CLOSING_CODE: {
                        System.out.println("Closing account...");
                        reply = processAccClosure(info, accMapping);

                        if (!AT_LEAST_ONCE)
                            replyHistory.putReply(messageID, reply);

                        System.out.println("Account closed");
                        break;
                    }
                    case DEPOSIT_MONEY_CODE:
                    {
                        System.out.println("Depositing money...");
                        reply = depositMoney(info, accMapping);

                        if(!AT_LEAST_ONCE)
                            replyHistory.putReply(messageID, reply);

                        System.out.println("Money deposited");
                        break;
                    }
                    case WITHDRAW_MONEY_CODE:
                    {
                        System.out.println("Withdrawing money...");
                        reply = withdrawMoney(info, accMapping);

                        if(!AT_LEAST_ONCE)
                            replyHistory.putReply(messageID, reply);

                        System.out.println("Money withdrawn");
                        break;
                    }
                    case TRANSFER_MONEY_CODE:
                    {
                        System.out.println("Transferring money...");
                        reply = transferMoney(info, accMapping);

                        if(!AT_LEAST_ONCE)
                            replyHistory.putReply(messageID, reply);

                        System.out.println("Money transferred");
                        break;
                    }
                    case CACHED_REPLY: {
                        System.out.println("Sending reply from cache");
                        break;
                    }
                    case ADD_OBSERVERS_FOR_MONITORING_CODE: {
                        System.out.println("Adding client " + clientIdentifier + " for monitoring...");
                        Observer o = new Observer(clientIp, clientPort);
                        reply = addObserver(clientIdentifier, o, observerMap);

                        if(!AT_LEAST_ONCE)
                            replyHistory.putReply(messageID, reply);

                        System.out.println("Client " + clientIdentifier + " is now monitoring server...");
                        break;
                    }
                    case REMOVE_OBSERVERS_FROM_MONITORING_CODE: {
                        System.out.println("Removing client " + clientIdentifier + " from monitoring");
                        reply = removeObserver(clientIdentifier, observerMap);

                        if(!AT_LEAST_ONCE)
                            replyHistory.putReply(messageID, reply);

                        System.out.println("Client " + clientIdentifier + " is now removed from monitoring server...");
                        break;
                    }
                }

                //Simulate server reply failure
                //We assume all messages fail to send when simulating packet loss
                if(!failMessage("server")) {
                    sendReply(request, reply, SERVER_FAILURE_PROB);      // send to client the reply message
                    if (action != ADD_OBSERVERS_FOR_MONITORING_CODE && action != REMOVE_OBSERVERS_FROM_MONITORING_CODE) {
                        for (Map.Entry<String, Observer> entry : observerMap.entrySet()) {
                            entry.getValue().notify(reply);     // notify any monitoring clients
                        }
                    }
                }else{
                    System.out.println("Message was not sent to simulate packet loss.");
                }
            } catch (IllegalArgumentException validationError) {
                if (Objects.equals(validationError.getMessage(), NOT_FOUND)) {
                    assert request != null;
                    sendReply(request, marshall(NOT_FOUND), SERVER_FAILURE_PROB);
                    System.out.println("Error: Account Number not found");
                } else if (Objects.equals(validationError.getMessage(), UNAUTHORIZED)) {
                    assert request != null;
                    sendReply(request, marshall(UNAUTHORIZED), SERVER_FAILURE_PROB);
                    System.out.println("Error: Wrong name/password entered.");
                } else if (Objects.equals(validationError.getMessage(), INSUFFICIENT)) {
                    assert request != null;
                    sendReply(request, marshall(INSUFFICIENT), SERVER_FAILURE_PROB);
                    System.out.println("Error: Insufficient amount in account.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                // reset buffers
                reply = new byte[BUFFER_SIZE];
                buffer = new byte[BUFFER_SIZE];
            }
        }
    }
}
