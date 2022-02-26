package utils;

public class Constants {
    /* Configurations */
    public static final String HOST_NAME = "localhost";
    public static final int SERVER_PORT_NUMBER = 6789;
    public static final int BUFFER_SIZE = 1000;


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
    public static final int ACC_OPENING_CODE = 1;                   // integer code for opening an account
    public static final String ACC_OPENING_CODE_STRING = "1";       // string code for opening an account

    public static final int ACC_BALANCE_CODE = 8;                   // integer code for account balance query
    public static final String ACC_BALANCE_CODE_STRING = "8";       // string code for account balance query

    public static final int ACC_CLOSING_CODE = 4;                     // integer code to close an account

    /* Constants for reading in inputs */
    public static final char NEW = 'n';
    public static final char EXISTING = 'e';
}
