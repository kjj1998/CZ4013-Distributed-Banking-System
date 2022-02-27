package utils;

import objects.Account;
import objects.Currency;
import objects.Pointer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static utils.Constants.*;
import static utils.Constants.BYTE_BLOCK_SIZE;
import static utils.UtilityFunctions.*;

public class MarshallFunctions {
    /**
     * Function to marshall a piece of String data
     * @param val the string to be marshalled
     * @return a byte array which is a concatenation of the length of the string and the characters in the string
     */
    public static byte[] marshall(String val) {
        byte[] lengthByteArray = ByteBuffer.allocate(BYTE_BLOCK_SIZE_FOR_INT).putInt(val.length()).array();
        byte[] valByteArray = convertStringToByteArray(val);

        return concatWithCopy(lengthByteArray, valByteArray);
    }

    /**
     * Function to unmarshall data
     *
     * @param point   Pointer class to keep track of pointer value so that the correct range can be selected from the request byte array
     * @param request byte array
     * @return String value
     */
    public static String unmarshall(Pointer point, byte[] request) {
        int start = point.val;
        int end = point.val + BYTE_BLOCK_SIZE;

        int dataLength = byteArrayToInt(Arrays.copyOfRange(request, start, end));
        start = end;
        end += dataLength;

        String data = new String(Arrays.copyOfRange(request, start, end), StandardCharsets.UTF_8);
        if (dataLength % BYTE_BLOCK_SIZE != 0)
            end += (BYTE_BLOCK_SIZE - (dataLength % BYTE_BLOCK_SIZE));
        point.val = end;

        return data;
    }

    /**
     * Function to unmarshall account details on the client side
     * @param pointer the Pointer object to keep track of the byte position
     * @param reply the byte[] array containing the marshalled account details from the server
     * @return Account object containing details from the server
     */
    public static Account unmarshallAccount(Pointer pointer, byte[] reply) {
        int accNumber = Integer.parseInt(unmarshall(pointer, reply));
        String name = unmarshall(pointer, reply);
        Currency currency = Currency.valueOf(unmarshall(pointer, reply));
        double accBalance = Double.parseDouble(unmarshall(pointer, reply));
        return new Account(name, currency, accBalance, accNumber);
    }

    /**
     * Function to marshall account details on the server side
     * @param account the Account object containing the account details
     * @return byte[] array of the account details
     */
    public static byte[] marshallAccount(Account account) {
        byte[] statusCodeByteArray = marshall(OK);
        byte[] accountNumberByteArray = marshall(String.valueOf(account.getAccNumber()));
        byte[] nameByteArray = marshall(account.getName());
        byte[] currencyByteArray = marshall(account.getCurrency().name());
        byte[] accBalanceByteArray = marshall(String.valueOf(account.getAccBalance()));

        return concatWithCopy(statusCodeByteArray, accountNumberByteArray, nameByteArray, currencyByteArray, accBalanceByteArray);
    }
}
