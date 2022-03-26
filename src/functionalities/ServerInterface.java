package functionalities;

import objects.Account;
import objects.Currency;
import objects.Observer;
import objects.Pointer;

import java.util.Map;

import static utils.Constants.*;
import static utils.MarshallFunctions.*;
import static utils.UtilityFunctions.round;

public class ServerInterface {
    /**
     * Function to process the account creation
     * Data (byte array form) from the client is first unmarshalled with help of a Pointer object
     * The Pointer object is passed into every unmarshall operation, and it keeps tracks of where we should extract the data from the data given
     * Generates a random account number, creates the account and adds it into the account mapping
     *
     * @param request byte array containing data from the client
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @return the byte array containing the details of the newly created account
     */
    public static byte[] processAccCreation(byte[] request, Map<Integer, Account> accMapping) {
        /*
            I'm basing my design off CORBA's Common Data Representation where it is assumed that sender and recipient have
            common knowledge of the order and types of the data items in a message.

            For e.g. if name: "John Smith", currency: "NZD", password: "P@ssword", initial acc balance: "1000.00"
            the byte array will be:

            00 00 00 01 00 00 00 0A 4A 6F 68 6E 20 53 6D 69 74 68 5F 5F 00 00 00 03 4E 5A 44 5F 00 00 00 08 50 40 73 73 77 6F 72 64 00 00 00 07 31 30 30 30 2E 30 30 5F

            However, take note that the first 16 bytes will be the messageID which is randomly generated on the client side (not shown in the above byte array)

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
        if (accMapping.containsKey(accNumber)) {
            accNumber = (int) ((Math.random() * (Integer.MAX_VALUE - 1000000000)) + 1000000000);        // Generate another account number if not unique (not likely to happen in our use case)
        }
        Account newAccount = new Account(name, Currency.valueOf(currency), password, amt, accNumber, AccountCreation);
        accMapping.put(accNumber, newAccount);     // create account and add it into the account mapping

        return marshallAccount(newAccount);
    }

    /**
     * Function to query the server for the current account balance
     * Data (byte array form) from the client is first unmarshalled with help of a Pointer object
     * The Pointer object is passed into every unmarshall operation, and it keeps tracks of where we should extract the data given by the client
     * Find the account to be queried
     *
     * @param request byte array containing the account number and password of the account
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @return an Account object containing the account details (excluding password)
     * @throws IllegalArgumentException throws exception if account is not found or password given is wrong
     */
    public static byte[] processAccBalanceQuery(byte[] request, Map<Integer, Account> accMapping) throws IllegalArgumentException {
        Pointer val = new Pointer(0);

        int accNumber = Integer.parseInt(unmarshall(val, request));
        String password = unmarshall(val, request);

        if (!accMapping.containsKey(accNumber))
            throw new IllegalArgumentException(NOT_FOUND);

        Account queriedAccount = accMapping.get(accNumber);

        if (queriedAccount.verifyPassword(password)) {
            queriedAccount.setAction(CheckBalance);
            return marshallAccount(queriedAccount);
        } else {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }
    }

    /**
     * Function to close an account on the server
     * Data (byte array form) from the client is first unmarshalled with help of a Pointer object
     * The Pointer object is passed into every unmarshall operation, and it keeps tracks of where we should extract the data given by the client
     * Find the account to be queried
     *
     * @param request byte array containing the account number, name and password of account to be closed
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @param <K> Integer
     * @param <V> Account
     * @return an Account object containing the account details (excluding password)
     * @throws IllegalArgumentException throws exception if account is not found or password given is wrong
     */
    public static <K,V> byte[] processAccClosure(byte[] request, Map<K,V> accMapping) throws IllegalArgumentException {
        Pointer val = new Pointer(0);

        int accNumber = Integer.parseInt(unmarshall(val, request));
        String name = unmarshall(val, request);
        String password = unmarshall(val, request);

        if (!accMapping.containsKey(accNumber))
            throw new IllegalArgumentException(NOT_FOUND);

        Account queriedAccount = (Account) accMapping.get(accNumber);

        if (queriedAccount.verifyName(name) && queriedAccount.verifyPassword(password)) {
            accMapping.remove(accNumber);
            queriedAccount.setAction(AccountClosure);
            return marshallAccount(queriedAccount);
        } else {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }
    }

    /**
     * Function to deposit money into an account on the server
     * Data (byte array form) from the client is first unmarshalled with help of a Pointer object
     * The Pointer object is passed into every unmarshall operation, and it keeps tracks of where we should extract the data given by the client
     * Find the account to be queried and check that account name and password entered is correct
     *
     * @param request byte array containing the account number, name and password of account to be closed
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @return an Account object containing the account details (excluding password)
     * @throws IllegalArgumentException throws exception if account is not found or name/password given is wrong
     */
    public static byte[] depositMoney(byte[] request, Map<Integer, Account> accMapping) throws IllegalArgumentException{
        Pointer val = new Pointer(0);

        String name = unmarshall(val, request);
        int accNumber = Integer.parseInt(unmarshall(val, request));
        String password = unmarshall(val, request);
        String currency = unmarshall(val, request);
        double deposit = round(Double.parseDouble(unmarshall(val, request)), 2);

        if (!accMapping.containsKey(accNumber))
            throw new IllegalArgumentException(NOT_FOUND);
        Account queriedAccount = accMapping.get(accNumber);

        if (queriedAccount.verifyName(name) && queriedAccount.verifyPassword(password)) {
            queriedAccount.deposit(deposit,currency);
            queriedAccount.setAction(DepositFunds);
            return marshallAccount(queriedAccount);
        } else {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }
    }

    /**
     * Function to withdraw money from an account on the server
     * Data (byte array form) from the client is first unmarshalled with help of a Pointer object
     * The Pointer object is passed into every unmarshall operation, and it keeps tracks of where we should extract the data given by the client
     * Find the account to be queried and check that account name and password entered is correct
     * Withdraw money if there is sufficient funds in account
     *
     * @param request byte array containing the account number, name and password of account to be closed
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @return an Account object containing the account details (excluding password)
     * @throws IllegalArgumentException throws exception if account is not found or name/password given is wrong or insufficient funds in account
     */
    public static byte[] withdrawMoney(byte[] request, Map<Integer, Account> accMapping) throws IllegalArgumentException{
        try {
            Pointer val = new Pointer(0);

            String name = unmarshall(val, request);
            int accNumber = Integer.parseInt(unmarshall(val, request));
            String password = unmarshall(val, request);
            String currency = unmarshall(val, request);
            double withdraw = round(Double.parseDouble(unmarshall(val, request)), 2);

            if (!accMapping.containsKey(accNumber))
                throw new IllegalArgumentException(NOT_FOUND);
            Account queriedAccount = accMapping.get(accNumber);

            if (queriedAccount.verifyName(name) && queriedAccount.verifyPassword(password)) {
                queriedAccount.withdraw(withdraw, currency);
                queriedAccount.setAction(WithdrawFunds);
                return marshallAccount(queriedAccount);
            } else {
                throw new IllegalArgumentException(UNAUTHORIZED);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Function to transfer money from an account on the server to another account on the server
     * Data (byte array form) from the client is first unmarshalled with help of a Pointer object
     * The Pointer object is passed into every unmarshall operation, and it keeps tracks of where we should extract the data given by the client
     * Find the account to be queried and check that account name and password entered is correct
     * Withdraw money if there is sufficient funds in account
     * Check that the recipient account exists and then deposit funds into it
     *
     * @param request byte array containing the account number, name and password of account to be closed
     * @param accMapping the HashMap mapping account numbers to their respective accounts
     * @return an Account object containing the account details (excluding password)
     * @throws IllegalArgumentException throws exception if account is not found or name/password given is wrong or insufficient funds in account
     */
    public static byte[] transferMoney(byte[] request, Map<Integer, Account> accMapping){
        Pointer val = new Pointer(0);

        String name = unmarshall(val, request);
        int accNumber = Integer.parseInt(unmarshall(val, request));
        String password = unmarshall(val, request);
        int toAccNumber = Integer.parseInt(unmarshall(val, request));
        String currency = unmarshall(val, request);
        double transfer = round(Double.parseDouble(unmarshall(val, request)), 2);

        if (!accMapping.containsKey(accNumber))
            throw new IllegalArgumentException(NOT_FOUND);
        Account queriedAccount = accMapping.get(accNumber);
        if (queriedAccount.verifyName(name) && queriedAccount.verifyPassword(password)) {
            queriedAccount.withdraw(transfer,currency);
            queriedAccount.setAction(TransferFundsOut);
        } else {
            throw new IllegalArgumentException(UNAUTHORIZED);
        }

        //Recipient acc
        if (!accMapping.containsKey(toAccNumber)) {
            queriedAccount.deposit(transfer,currency);
            throw new IllegalArgumentException(NOT_FOUND);
        }

        Account recipientAccount = accMapping.get(toAccNumber);
        recipientAccount.deposit(transfer,currency);
        recipientAccount.setAction(TransferFundsIn);

        return marshallAccount(queriedAccount);
    }

    /**
     * Adds a client into the list of clients monitoring the server
     * @param clientIdentifier  the string identifying a particular client
     * @param o the Observer object which represents a client observing the server
     * @param observerMap the HashMap that maps the clientIdentifier to an Observer object
     * @return a byte array representing the OK status code
     */
    public static byte[] addObserver(String clientIdentifier, Observer o, Map<String, Observer> observerMap) {
        observerMap.put(clientIdentifier, o);
        return marshall(OK);
    }

    /**
     * Removes a client from the list of clients monitoring the server
     * @param clientIdentifier  the string identifying a particular client
     * @param observerMap the HashMap that maps the clientIdentifier to an Observer object
     * @return a byte array representing the OK status code
     */
    public static byte[] removeObserver(String clientIdentifier, Map<String, Observer> observerMap) {
        observerMap.remove(clientIdentifier);
        return marshall(OK);
    }
}
