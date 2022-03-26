package utils;

public class Constants {
    /* Configurations */
    public static final String HOST_NAME = "localhost"; // IP address to be changed based on which PC is acting as the server
    public static final int SERVER_PORT_NUMBER = 6789;
    public static final int BUFFER_SIZE = 1000;
    public static final int LRU_CACHE_SIZE = 100;

    public static final boolean AT_LEAST_ONCE = true; //if at least once is false, use at most once
    public static final int atLeastOnceTimeout = 2000;
    public static final float SERVER_FAILURE_PROB = 0;
    public static final float CLIENT_FAILURE_PROB = 0;

    public static final int MESSAGE_ID_LENGTH = 16;             // number of alphanumeric characters in each message id
    public static final int BYTE_BLOCK_SIZE = 4;                // number of bytes in each block of bytes
    public static final int BYTE_BLOCK_SIZE_FOR_INT = 4;        // Number of bytes for an int value
    public static final int ASCII_CODE_FOR_PADDING = 95;        // ASCII Code for '_'

    /*
      Set the index of the byte array sent from the client where the information actually begin
      After the message id, and the code for the action to be taken
    */
    public static final int MESSAGE_INFO_START_INDEX = 20;

    /* Option codes for each action to be taken at the server */
    public static final int CACHED_REPLY = 0;
    public static final int ACC_CREATION_CODE = 1;                      // integer code for opening an account
    public static final int DEPOSIT_MONEY_CODE = 2;                     // integer code for opening an account
    public static final int WITHDRAW_MONEY_CODE = 3;                    // integer code for opening an account
    public static final int ACC_CLOSING_CODE = 4;                       // integer code to close an account
    public static final int TRANSFER_MONEY_CODE = 5;                    // integer code for opening an account
    public static final int ADD_OBSERVERS_FOR_MONITORING_CODE = 6;      // integer code for account to monitor updates
    public static final int ACC_BALANCE_CODE = 7;                       // integer code for account balance query
    public static final int REMOVE_OBSERVERS_FROM_MONITORING_CODE = 8;  // integer code for account to removed from monitoring updates

    /* Constants for reading in inputs */
    public static final char NEW = 'n';
    public static final char EXISTING = 'e';

    /* Status codes from server */
    public static final String OK = "200";
    public static final String UNAUTHORIZED = "401";
    public static final String INSUFFICIENT = "402";
    public static final String NOT_FOUND = "404";

    /* Constants for actions taken on the accounts */
    public static final String AccountCreation = "Account Creation";
    public static final String AccountClosure = "Account Closure";
    public static final String CheckBalance = "Check Balance";
    public static final String WithdrawFunds = "Withdrawal of Funds";
    public static final String DepositFunds = "Deposit of Funds";
    public static final String TransferFundsIn = "Transfer of Funds In";
    public static final String TransferFundsOut = "Transfer of Funds Out";

    /* EXCHANGE RATE, taking SGD as 1.0 */
    public static final double sgdExchangeRate = 1.00;
    public static final double nzdExchangeRate = 1.07;
    public static final double usdExchangeRate = 0.73;
}
