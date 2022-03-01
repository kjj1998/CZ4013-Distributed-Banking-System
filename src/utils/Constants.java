package utils;

public class Constants {
    /* Configurations */
    public static final String HOST_NAME = "localhost";
    public static final int SERVER_PORT_NUMBER = 6789;
    public static final int BUFFER_SIZE = 1000;
    public static final int LRU_CACHE_SIZE = 100;

    public static final int atLeastOnceTimeout = 1000;

    public static final int MESSAGE_ID_LENGTH = 16;             // number of alphanumeric characters in each message id
    public static final int BYTE_BLOCK_SIZE = 4;                // number of bytes in each block of bytes
    public static final int BYTE_BLOCK_SIZE_FOR_INT = 4;        // Number of bytes for an int value
    public static final int BYTE_BLOCK_SIZE_FOR_DOUBLE = 8;     // Number of bytes for an int value
    public static final int ASCII_CODE_FOR_PADDING = 95;        // ASCII Code for '_'
    /*
      Set the index of the byte array sent from the client where the information actually begin
      After the message id, and the code for the action to be taken
    */
    public static final int MESSAGE_INFO_START_INDEX = 20;
    public static final int MONETARY_DECIMAL_PLACES = 2;
    public static final int ONE_BILLION = 1000000000;

    /* Option codes for each action to be taken at the server */
    public static final int CACHED_REPLY = 0;

    public static final int ACC_CREATION_CODE = 1;                   // integer code for opening an account
    public static final String ACC_CREATION_CODE_STRING = "1";       // string code for opening an account

    public static final int DEPOSIT_MONEY_CODE = 2;                   // integer code for opening an account
    public static final String DEPOSIT_MONEY_CODE_STRING = "2";       // string code for opening an account

    public static final int WITHDRAW_MONEY_CODE = 3;                   // integer code for opening an account
    public static final String WITHDRAW_MONEY_CODE_STRING = "3";       // string code for opening an account

    public static final int ACC_CLOSING_CODE = 4;                     // integer code to close an account
    
    public static final int TRANSFER_MONEY_CODE = 5;                   // integer code for opening an account
    public static final String TRANSFER_MONEY_CODE_STRING = "5";       // string code for opening an account

    public static final int ACC_BALANCE_CODE = 8;                   // integer code for account balance query
    public static final String ACC_BALANCE_CODE_STRING = "8";       // string code for account balance query

    /* Constants for reading in inputs */
    public static final char NEW = 'n';
    public static final char EXISTING = 'e';
    
    /* Status codes from server */
    public static final String OK = "200";
    public static final String UNAUTHORIZED = "401";
    public static final String INSUFFICIENT = "402";
    public static final String NOT_FOUND = "404";
}
