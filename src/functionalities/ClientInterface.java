package functionalities;

import objects.Account;
import objects.Currency;
import objects.Pointer;
import utils.MessageIDGenerator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static utils.Constants.*;
import static utils.UtilityFunctions.*;
import static utils.MarshallFunctions.*;
import static utils.SocketFunctions.*;


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
    public static Account createAccount(String name, Currency currency, String password, String initialAccBalance,boolean atLeastOnce) {
        byte[] accCreationByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(ACC_CREATION_CODE).array();
        byte[] nameByteArray = marshall(name);
        byte[] currencyByteArray = marshall(currency.name());
        byte[] passwordByteArray = marshall(password);
        byte[] accBalanceArray = marshall(initialAccBalance);
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, accCreationByteArray, nameByteArray, currencyByteArray, passwordByteArray, accBalanceArray);

        byte[] reply = sendRequest(marshall,atLeastOnce); //send atLeastOnce
        
        Pointer val = new Pointer(0);
        String statusCode = unmarshall(val, reply);
        if (statusCode.equals(OK)) {
            int accNumber = Integer.parseInt(unmarshall(val, reply));
            name = unmarshall(val, reply);
            currency = Currency.valueOf(unmarshall(val, reply));
            double accBalance = Double.parseDouble(unmarshall(val, reply));
            return new Account(name, currency, accBalance, accNumber);
        }
        return null;
    }

    /**
     * Function to query current account balance
     *
     * @param accNumber the account number of the account to be queried
     * @param password  the password of the account to be queried
     * @return the current balance in the account
     */
    public static Account queryAccBalance(String accNumber, String password,boolean atLeastOnce)throws Exception {
        int accNumberLength = accNumber.length();
        int passwordLength = password.length();

        byte[] accBalanceQueryByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(ACC_BALANCE_CODE).array();
        byte[] accNumberByteArray = marshall(accNumber);
        byte[] passwordByteArray = marshall(password);
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, accBalanceQueryByteArray, accNumberByteArray, passwordByteArray);

        byte[] reply = sendRequest(marshall,atLeastOnce);

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

    public static Account closeAccount(String name, String password, String accNumber,boolean atLeastOnce)throws Exception {
        int nameLength = name.length();
        int passwordLength = password.length();
        int accNumberLength = accNumber.length();

        byte[] closeAccByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(ACC_CLOSING_CODE).array();
        byte[] nameByteArray = marshall(name);
        byte[] passwordByteArray = marshall(password);
        byte[] accNumberByteArray = marshall(accNumber);
        byte[] messageIDArray = convertStringToByteArray(gen.nextString());
        byte[] marshall = concatWithCopy(messageIDArray, closeAccByteArray, accNumberByteArray, nameByteArray, passwordByteArray);
        
        String reply = new String(sendRequest(marshall,atLeastOnce), StandardCharsets.UTF_8);

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

    public static double depositMoney(String name, String accNumber,String password,Currency currency, double deposit,boolean atLeastOnce) {
        int nameLength = name.length();
        int accNumberLength = accNumber.length();
        int passwordLength = password.length();
        int currencyLength = currency.toString().length();
        int depositLength = String.valueOf(deposit).length();
        
        byte[] depositMoneyByteArray = ByteBuffer.allocate(4).putInt(2).array();
        byte[] nameLengthByteArray = ByteBuffer.allocate(4).putInt(nameLength).array();
        byte[] nameByteArray = convertStringToByteArray(name);
        byte[] accNumberLengthByteArray = ByteBuffer.allocate(4).putInt(accNumberLength).array();
        byte[] accNumberByteArray = convertStringToByteArray(Integer.toString(accNumber));
        byte[] passwordLengthByteArray = ByteBuffer.allocate(4).putInt(passwordLength).array();
        byte[] passwordByteArray = convertStringToByteArray(password);
        byte[] currencyLengthByteArray = ByteBuffer.allocate(4).putInt(currencyLength).array();
        byte[] currencyByteArray = convertStringToByteArray(currency.toString());
        byte[] depositLengthByteArray = ByteBuffer.allocate(4).putInt(depositLength).array();
        byte[] depositByteArray = convertStringToByteArray(String.valueOf(deposit));
            
        String messageID = gen.nextString();
        System.out.println("MessageId: "+messageID);
        byte[] messageIDArray = convertStringToByteArray(messageID);
    
        byte[] marshall = concatWithCopy(messageIDArray, depositMoneyByteArray, nameLengthByteArray, nameByteArray,accNumberLengthByteArray,accNumberByteArray,passwordLengthByteArray, passwordByteArray,currencyLengthByteArray, currencyByteArray,depositLengthByteArray,depositByteArray);
    
        byte[] reply = sendRequest(marshall,atLeastOnce);
        if (atLeastOnce){
            while(reply==null){
                reply=sendRequest(marshall, atLeastOnce);
                System.out.println("Resending MessageId: "+messageID);
            }
        }
        double currentAccBalance = ByteBuffer.wrap(reply).getDouble();
        System.out.println("Current account balance: " + currentAccBalance);
        return currentAccBalance;
    }
    public static double withdrawMoney(String name, String accNumber,String password,Currency currency, double withdraw,boolean atLeastOnce) {
        int nameLength = name.length();
        int accNumberLength = accNumber.length();
        int passwordLength = password.length();
        int currencyLength = currency.toString().length();
        int withdrawLength = String.valueOf(withdraw).length();
        
        byte[] withdrawMoneyByteArray = ByteBuffer.allocate(4).putInt(3).array();
        byte[] nameLengthByteArray = ByteBuffer.allocate(4).putInt(nameLength).array();
        byte[] nameByteArray = convertStringToByteArray(name);
        byte[] accNumberLengthByteArray = ByteBuffer.allocate(4).putInt(accNumberLength).array();
        byte[] accNumberByteArray = convertStringToByteArray(Integer.toString(accNumber));
        byte[] passwordLengthByteArray = ByteBuffer.allocate(4).putInt(passwordLength).array();
        byte[] passwordByteArray = convertStringToByteArray(password);
        byte[] currencyLengthByteArray = ByteBuffer.allocate(4).putInt(currencyLength).array();
        byte[] currencyByteArray = convertStringToByteArray(currency.toString());
        byte[] withdrawLengthByteArray = ByteBuffer.allocate(4).putInt(withdrawLength).array();
        byte[] withdrawByteArray = convertStringToByteArray(String.valueOf(withdraw));
            
        String messageID = gen.nextString();
        System.out.println("MessageId: "+messageID);
        byte[] messageIDArray = convertStringToByteArray(messageID);
    
        byte[] marshall = concatWithCopy(messageIDArray, withdrawMoneyByteArray, nameLengthByteArray, nameByteArray,accNumberLengthByteArray,accNumberByteArray,passwordLengthByteArray, passwordByteArray,currencyLengthByteArray, currencyByteArray,withdrawLengthByteArray,withdrawByteArray);
    
        byte[] reply = sendRequest(marshall,atLeastOnce);
        if (atLeastOnce){
            while(reply==null){
                reply=sendRequest(marshall, atLeastOnce);
                System.out.println("Resending MessageId: "+messageID);
            }
        }
        double currentAccBalance = ByteBuffer.wrap(reply).getDouble();
        System.out.println("Current account balance: " + currentAccBalance);
        return currentAccBalance;
    }

    public static double transferMoney(String name, String accNumber,String password,int toAccNumber,Currency currency, double withdraw,boolean atLeastOnce) {
        int nameLength = name.length();
        int accNumberLength = accNumber.length();
        int passwordLength = password.length();
        int toAccNumberLength = Integer.toString(toAccNumber).length();
        int currencyLength = currency.toString().length();
        int withdrawLength = String.valueOf(withdraw).length();
        
        byte[] withdrawMoneyByteArray = ByteBuffer.allocate(4).putInt(3).array();
        byte[] nameLengthByteArray = ByteBuffer.allocate(4).putInt(nameLength).array();
        byte[] nameByteArray = convertStringToByteArray(name);
        byte[] accNumberLengthByteArray = ByteBuffer.allocate(4).putInt(accNumberLength).array();
        byte[] accNumberByteArray = convertStringToByteArray(Integer.toString(accNumber));
        byte[] passwordLengthByteArray = ByteBuffer.allocate(4).putInt(passwordLength).array();
        byte[] passwordByteArray = convertStringToByteArray(password);
        byte[] toAccNumberLengthByteArray = ByteBuffer.allocate(4).putInt(toAccNumberLength).array();
        byte[] toAccNumberByteArray = convertStringToByteArray(Integer.toString(toAccNumber));
        byte[] currencyLengthByteArray = ByteBuffer.allocate(4).putInt(currencyLength).array();
        byte[] currencyByteArray = convertStringToByteArray(currency.toString());
        byte[] withdrawLengthByteArray = ByteBuffer.allocate(4).putInt(withdrawLength).array();
        byte[] withdrawByteArray = convertStringToByteArray(String.valueOf(withdraw));
            
        String messageID = gen.nextString();
        System.out.println("MessageId: "+messageID);
        byte[] messageIDArray = convertStringToByteArray(messageID);
    
        byte[] marshall = concatWithCopy(messageIDArray, withdrawMoneyByteArray, nameLengthByteArray, nameByteArray,accNumberLengthByteArray,accNumberByteArray,passwordLengthByteArray, passwordByteArray,toAccNumberLengthByteArray,toAccNumberByteArray,currencyLengthByteArray, currencyByteArray,withdrawLengthByteArray,withdrawByteArray);
    
        byte[] reply = sendRequest(marshall,atLeastOnce);
        if (atLeastOnce){
            while(reply==null){
                reply=sendRequest(marshall, atLeastOnce);
                System.out.println("Resending MessageId: "+messageID);
            }
        }
        double currentAccBalance = ByteBuffer.wrap(reply).getDouble();
        System.out.println("Current account balance: " + currentAccBalance);
        return currentAccBalance;
    }
}
