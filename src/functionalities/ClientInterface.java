package functionalities;

import objects.Account;
import objects.Currency;
import objects.Pointer;
import utils.MessageIDGenerator;

import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import static utils.Constants.*;
import static utils.MarshallFunctions.*;
import static utils.SocketFunctions.*;
import static utils.UtilityFunctions.*;


public class ClientInterface {
    public static MessageIDGenerator gen = new MessageIDGenerator(MESSAGE_ID_LENGTH);   // create a new MessageIDGenerator

    /**
     * Function to process account creation
     *
     * @param name              String containing name of the customer
     * @param currency          Enum to represent the currency of the bank account
     * @param password          String to represent password of the bank account
     * @param initialAccBalance String to represent initial account balance
     * @return the bank account number generated by the server
     */
    public static Account createAccount(String name, Currency currency, String password, String initialAccBalance) throws Exception {
        byte[] accCreationByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(ACC_CREATION_CODE).array();
        byte[] nameByteArray = marshall(name);
        byte[] currencyByteArray = marshall(currency.name());
        byte[] passwordByteArray = marshall(password);
        byte[] accBalanceArray = marshall(initialAccBalance);
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, accCreationByteArray, nameByteArray, currencyByteArray, passwordByteArray, accBalanceArray);


        byte[] reply = sendRequest(marshall); //send atLeastOnce
//        if (atLeastOnce){
        while(reply==null || failMessage("client")){
            reply=sendRequest(marshall);
            System.out.println("Resending Message");
        }
//        }

        Pointer pointer = new Pointer(0);
        String statusCode = unmarshall(pointer, reply);
        switch (statusCode) {
            case OK:
                return unmarshallAccount(pointer, reply);
            case NOT_FOUND:
                throw new IllegalArgumentException(NOT_FOUND);
            case UNAUTHORIZED:
                throw new IllegalArgumentException(UNAUTHORIZED);
            default:
                throw new Exception();
        }
    }

    /**
     * Function to query current account balance
     *
     * @param accNumber the account number of the account to be queried
     * @param password  the password of the account to be queried
     * @return the current balance in the account
     */
    public static Account queryAccBalance(String accNumber, String password)throws Exception {

        byte[] accBalanceQueryByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(ACC_BALANCE_CODE).array();
        byte[] accNumberByteArray = marshall(accNumber);
        byte[] passwordByteArray = marshall(password);
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, accBalanceQueryByteArray, accNumberByteArray, passwordByteArray);

        byte[] reply = sendRequest(marshall);
//        if (atLeastOnce){
        while(reply==null || failMessage("client")){
            reply=sendRequest(marshall);
            System.out.println("Resending Message");
        }
//        }

        Pointer pointer = new Pointer(0);
        String statusCode = unmarshall(pointer, reply);
        switch (statusCode) {
            case OK:
                return unmarshallAccount(pointer, reply);
            case NOT_FOUND:
                throw new IllegalArgumentException(NOT_FOUND);
            case UNAUTHORIZED:
                throw new IllegalArgumentException(UNAUTHORIZED);
            default:
                throw new Exception();
        }
    }

    /**
     * Function to close an account
     * @param name the name of the account holder
     * @param password the password of the account
     * @param accNumber the account number
     * @return Account object with account details
     * @throws Exception for unknown errors
     */
    public static Account closeAccount(String name, String password, String accNumber)throws Exception {
        byte[] closeAccByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(ACC_CLOSING_CODE).array();
        byte[] nameByteArray = marshall(name);
        byte[] passwordByteArray = marshall(password);
        byte[] accNumberByteArray = marshall(accNumber);
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, closeAccByteArray, accNumberByteArray, nameByteArray, passwordByteArray);

        //byte[] reply = null;
        byte[] reply = sendRequest(marshall);
//        if (atLeastOnce){
        while(reply==null || failMessage("client")){
            System.out.println("Resending Message");
            reply=sendRequest(marshall);
        }
//        }

        Pointer pointer = new Pointer(0);
        String statusCode = unmarshall(pointer, reply);
        switch (statusCode) {
            case OK:
                return unmarshallAccount(pointer, reply);
            case NOT_FOUND:
                throw new IllegalArgumentException(NOT_FOUND);
            case UNAUTHORIZED:
                throw new IllegalArgumentException(UNAUTHORIZED);
            default:
                throw new Exception();}
    }

    /**
     * Function to deposit money into the accounts
     * @param name the name of the account holder
     * @param accNumber the account number
     * @param password the password of the bank account
     * @param currency the currency of the funds being deposited
     * @param deposit the amount of funds being deposited
     * @return the Account object with the updated details
     * @throws Exception for unknown errors
     */
    public static Account depositMoney(String name, String accNumber,String password,Currency currency, double deposit) throws Exception {
        byte[] depositMoneyByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(DEPOSIT_MONEY_CODE).array();
        byte[] nameByteArray = marshall(name);
        byte[] accNumberByteArray = marshall(accNumber);
        byte[] passwordByteArray = marshall(password);
        byte[] currencyByteArray = marshall(currency.name());
        byte[] depositByteArray = marshall(String.valueOf(deposit));
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, depositMoneyByteArray, nameByteArray, accNumberByteArray, passwordByteArray,currencyByteArray,depositByteArray);

        byte[] reply = sendRequest(marshall);
//        byte[] reply = null;

        reply=sendRequest(marshall);
//        System.out.println("Sent the request to deposit once. Trying again to see if At most once works");
//        reply = sendRequest(marshall,atLeastOnce);

//        if (atLeastOnce){
        while(reply==null || failMessage("client")){
            reply=sendRequest(marshall);
            System.out.println("Resending Message");
        }
//        }
        Pointer pointer = new Pointer(0);
        String statusCode = unmarshall(pointer, reply);
        switch (statusCode) {
            case OK:
                return unmarshallAccount(pointer, reply);
            case NOT_FOUND:
                throw new IllegalArgumentException(NOT_FOUND);
            case UNAUTHORIZED:
                throw new IllegalArgumentException(UNAUTHORIZED);
            default:
                throw new Exception();}
    }

    /**
     * Function to withdraw money
     * @param name account holder name
     * @param accNumber account number
     * @param password password of the account
     * @param currency currency to withdraw the money in
     * @param withdraw amount of money to be withdrawn
     * @return Account details with all the updated details
     * @throws Exception unknown error
     */
    public static Account withdrawMoney(String name, String accNumber,String password,Currency currency, double withdraw)throws Exception {
        byte[] withdrawMoneyByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(WITHDRAW_MONEY_CODE).array();
        byte[] nameByteArray = marshall(name);
        byte[] accNumberByteArray = marshall(accNumber);
        byte[] passwordByteArray = marshall(password);
        byte[] currencyByteArray = marshall(currency.name());
        byte[] withdrawByteArray = marshall(String.valueOf(withdraw));
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, withdrawMoneyByteArray, nameByteArray, accNumberByteArray, passwordByteArray,currencyByteArray,withdrawByteArray);

//        byte[] reply = null;
        byte[] reply = sendRequest(marshall);
//        if (atLeastOnce){
        while(reply==null || failMessage("client")){
            reply=sendRequest(marshall);
            System.out.println("Resending Message");
        }
//        }
        Pointer pointer = new Pointer(0);
        String statusCode = unmarshall(pointer, reply);
        switch (statusCode) {
            case OK:
                return unmarshallAccount(pointer, reply);
            case NOT_FOUND:
                throw new IllegalArgumentException(NOT_FOUND);
            case UNAUTHORIZED:
                throw new IllegalArgumentException(UNAUTHORIZED);
            case INSUFFICIENT:
                throw new IllegalArgumentException(INSUFFICIENT);
            default:
                throw new Exception();}
    }

    /**
     * Function to transfer money to another bank account
     * @param name name of the account holder
     * @param accNumber account number
     * @param password password of the account
     * @param toAccNumber account number of receiving account
     * @param currency currency of transfer
     * @param transfer the amount to be transferred
     * @return Account object with the account details
     * @throws Exception unknown exception
     */
    public static Account transferMoney(String name, String accNumber,String password,String toAccNumber,Currency currency, double transfer)throws Exception {
        byte[] transferMoneyByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(TRANSFER_MONEY_CODE).array();
        byte[] nameByteArray = marshall(name);
        byte[] accNumberByteArray = marshall(accNumber);
        byte[] passwordByteArray = marshall(password);
        byte[] toAccNumberByteArray = marshall(toAccNumber);
        byte[] currencyByteArray = marshall(currency.name());
        byte[] transferByteArray = marshall(String.valueOf(transfer));
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, transferMoneyByteArray, nameByteArray, accNumberByteArray, passwordByteArray,toAccNumberByteArray,currencyByteArray,transferByteArray);


//        byte[] reply = null;
//        reply = sendRequest(marshall);
        byte[] reply = sendRequest(marshall);
//        if (atLeastOnce){
        while(reply==null || failMessage("client")){
            System.out.println("Here");
            reply=sendRequest(marshall);
            System.out.println("Resending Message");
        }
//        }
        Pointer pointer = new Pointer(0);
        String statusCode = unmarshall(pointer, reply);
        switch (statusCode) {
            case OK:
                return unmarshallAccount(pointer, reply);
            case NOT_FOUND:
                throw new IllegalArgumentException(NOT_FOUND);
            case UNAUTHORIZED:
                throw new IllegalArgumentException(UNAUTHORIZED);
            default:
                throw new Exception();}
    }

    /**
     * Function to monitor any updates on the server for a set amount time
     * @param duration time in seconds to monitor
     * @throws IOException unknown error
     */
    public static void monitorUpdates(int duration) throws Exception {
        /* Set up the byte array containing instructions for current client to monitor server */
        byte[] startMonitorUpdatesByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(ADD_OBSERVERS_FOR_MONITORING_CODE).array();
        byte[] startMessageIDArray = convertStringToByteArray(gen.nextString());
        byte[] startMonitoringMarshall = concatWithCopy(startMessageIDArray, startMonitorUpdatesByteArray);

        /* Set up the byte array containing instructions for current client to end monitoring of server */
        byte[] endMonitorUpdatesByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(REMOVE_OBSERVERS_FROM_MONITORING_CODE).array();
        byte[] endMessageIDArray = convertStringToByteArray(gen.nextString());
        byte[] endMonitoringMarshall = concatWithCopy(endMessageIDArray, endMonitorUpdatesByteArray);

        DatagramSocket aSocket = new DatagramSocket();
        byte[] reply = sendRequestForMonitoring(startMonitoringMarshall, aSocket);

        while(reply==null || failMessage("client")){
            reply=sendRequestForMonitoring(startMonitoringMarshall, aSocket);
            System.out.println("Resending Message");
        }

        Pointer pointer = new Pointer(0);
        String statusCode = unmarshall(pointer, reply);
        if (!statusCode.equals(OK)) {
            throw new Exception();
        }

        monitorServer(duration, aSocket);

        reply = sendRequestForMonitoring(endMonitoringMarshall, aSocket);
        while(reply==null || failMessage("client")){
            reply=sendRequestForMonitoring(startMonitoringMarshall, aSocket);
            System.out.println("Resending Message");
        }
        aSocket.close();
    }
}
