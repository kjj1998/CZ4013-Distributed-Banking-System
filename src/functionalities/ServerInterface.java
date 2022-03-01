package functionalities;

import objects.Account;
import objects.Currency;
import objects.Pointer;

import java.util.Map;

import static utils.Constants.*;
import static utils.UtilityFunctions.*;
import static utils.MarshallFunctions.*;

public class ServerInterface {
    /**
     * Function to process the account creation, which will unmarshall the data from the client
     *
     * @param request byte array containing data from the client
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @return an Account object containing the account details (excluding password)
     */
    public static byte[] processAccCreation(byte[] request, Map<Integer, Account> accMapping) {
        /*
            I'm basing my design off CORBA's Common Data Representation where it is assumed that sender and recipient have
            common knowledge of the order and types of the data items in a message.

            For e.g. if name: "John Smith", currency: "NZD", password: "P@ssword", initial acc balance: "1000.00"
            the byte array will be:

            00 00 00 01 00 00 00 0A 4A 6F 68 6E 20 53 6D 69 74 68 5F 5F 00 00 00 03 4E 5A 44 5F 00 00 00 08 50 40 73 73 77 6F 72 64 00 00 00 07 31 30 30 30 2E 30 30 5F

            However, take note that the first 16 bytes will be the messageID which is randomly generated on the client side

            00 00 00 01 = 1 (4 bytes to decide what action server will take in the switch statement shown above)

            00 00 00 0A = 10 (4 bytes to show the length of name)
            4A 6F 68 6E 20 53 6D 69 74 68 5F 5F = "John Smith__" (12 bytes to represent the name, with last 2 bytes as padding)

            00 00 00 03 = 3 (4 bytes to show length of currency code)
            4E 5A 44 5F = "NZD_" (4 bytes to represent the currency code with 1 byte as padding)

            00 00 00 08 = 8 (4 bytes to show length of password)
            50 40 73 73 77 6F 72 64 = "P@ssword" (8 bytes to represent the password)

            00 00 00 07 = 7 (4 bytes to show length of initial bank amount)
            31 30 30 30 2E 30 30 5F = "1000.00_" (7 bytes to represent the initial bank amount with 1 byte as padding)

         */
        Pointer pointer = new Pointer(0);

        String name = unmarshall(pointer, request);
        String currency = unmarshall(pointer, request);
        String password = unmarshall(pointer, request);
        String amtString = unmarshall(pointer, request);
        double amt = round(Double.parseDouble(amtString), 2);
        int accNumber = (int) ((Math.random() * (Integer.MAX_VALUE - 1000000000)) + 1000000000);        // Generate random acc number
        Account newAccount = new Account(name, Currency.valueOf(currency), password, amt, accNumber);

        if (accMapping.containsKey(accNumber)) {
            accNumber = (int) ((Math.random() * (Integer.MAX_VALUE - 1000000000)) + 1000000000);        // Generate another account number if not unique
        }
        accMapping.put(accNumber, newAccount);     // create account and add it into the account mapping

        return marshallAccount(newAccount);
    }

    /**
     * Function to query the server for the current account balance
     *
     * @param request byte array containing the account number and password of the account
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @return an Account object containing the account details (excluding password)
     */
    public static byte[] processAccBalanceQuery(byte[] request, Map<Integer, Account> accMapping) {
        Pointer val = new Pointer(0);

        int accNumber = Integer.parseInt(unmarshall(val, request));
        String password = unmarshall(val, request);

        if (!accMapping.containsKey(accNumber))
            throw new IllegalArgumentException(NOT_FOUND);

        Account queriedAccount = accMapping.get(accNumber);

        if (queriedAccount.verifyPassword(password)) {
            return marshallAccount(queriedAccount);
        } else {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }
    }

    /**
     * Function to close an account on the server
     * @param request byte array containing the account number, name and password of account to be closed
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @param <K> Integer
     * @param <V> Account
     * @return an Account object containing the account details (excluding password)
     */
    public static <K,V> byte[] processAccClosure(byte[] request, Map<K,V> accMapping) {
        Pointer val = new Pointer(0);

        int accNumber = Integer.parseInt(unmarshall(val, request));
        String name = unmarshall(val, request);
        String password = unmarshall(val, request);

        if (!accMapping.containsKey(accNumber))
            throw new IllegalArgumentException(NOT_FOUND);

        Account queriedAccount = (Account) accMapping.get(accNumber);

        if (queriedAccount.verifyName(name) && queriedAccount.verifyPassword(password)) {
            accMapping.remove(accNumber);
            return marshallAccount(queriedAccount);
        } else {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }
    }
    public static double depositMoney(byte[] request, Map<Integer, Account> accMapping){
        Pointer val = new Pointer(0);

        String name = unmarshall(val, request);
        int accNumber = Integer.parseInt(unmarshall(val, request));
        String password = unmarshall(val, request);
        String currency = unmarshall(val, request);
        double deposit = round(Double.parseDouble(unmarshall(val, request)), 2);

        //Check for acc number
        if (!accMapping.containsKey(accNumber))
        {
            System.out.println("Invalid account number!");
            throw new IllegalArgumentException();
        }
        Account queriedAccount = accMapping.get(accNumber);
        //Check for password
        if (queriedAccount.verifyPassword(password)==false) {
            System.out.println("Invalid password!");
            throw new IllegalArgumentException();
        }
        //Check for name
        if (queriedAccount.verifyName(name)==false) {
            System.out.println("Wrong name!");
            throw new IllegalArgumentException();
        }
        queriedAccount.deposit(deposit);
        return queriedAccount.getAccBalance();
    }
    public static double withdrawMoney(byte[] request, Map<Integer, Account> accMapping){
        Pointer val = new Pointer(0);

        String name = unmarshall(val, request);
        int accNumber = Integer.parseInt(unmarshall(val, request));
        String password = unmarshall(val, request);
        String currency = unmarshall(val, request);
        double withdraw = round(Double.parseDouble(unmarshall(val, request)), 2);

        //Check for acc number
        if (!accMapping.containsKey(accNumber))
        {
            System.out.println("Invalid account number!");
            throw new IllegalArgumentException();
        }
        Account queriedAccount = accMapping.get(accNumber);
        //Check for password
        if (queriedAccount.verifyPassword(password)==false) {
            System.out.println("Invalid password!");
            throw new IllegalArgumentException();
        }
        //Check for name
        if (queriedAccount.verifyName(name)==false) {
            System.out.println("Wrong name!");
            throw new IllegalArgumentException();
        }
        queriedAccount.withdraw(withdraw);
        return queriedAccount.getAccBalance();
    }

    public static double transferMoney(byte[] request, Map<Integer, Account> accMapping){
        Pointer val = new Pointer(0);

        String name = unmarshall(val, request);
        int accNumber = Integer.parseInt(unmarshall(val, request));
        String password = unmarshall(val, request);
        int toAccNumber = Integer.parseInt(unmarshall(val, request));
        String currency = unmarshall(val, request);
        double transfer = round(Double.parseDouble(unmarshall(val, request)), 2);

        //Check for acc number
        if (!accMapping.containsKey(accNumber))
        {
            System.out.println("Invalid account number!");
            throw new IllegalArgumentException();
        }
        Account queriedAccount = accMapping.get(accNumber);
        //Check for password
        if (queriedAccount.verifyPassword(password)==false) {
            System.out.println("Invalid password!");
            throw new IllegalArgumentException();
        }
        //Check for name
        if (queriedAccount.verifyName(name)==false) {
            System.out.println("Wrong name!");
            throw new IllegalArgumentException();
        }

        if (!accMapping.containsKey(toAccNumber))
        {
            System.out.println("Invalid receipient account number!");
            throw new IllegalArgumentException();
        }
        Account receipientAccount = accMapping.get(toAccNumber);

        receipientAccount.deposit(transfer);
        queriedAccount.withdraw(transfer);
        return queriedAccount.getAccBalance();
    }
}
