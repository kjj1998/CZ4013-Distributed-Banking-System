package utils;

import objects.Account;
import objects.Currency;
import objects.Pointer;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static utils.Constants.*;

public class UtilityFunctions {
    /**
     * Convert a String into a byte array whose size is a multiple of 4
     * If length of String is not a multiple of 4, '_' (ASCII code 95) is added as padding to make up the numbers
     *
     * @param str the String to be converted into byte array
     * @return the converted byte array
     */
    public static byte[] convertStringToByteArray(String str) {
        byte[] array = str.getBytes();
        if (array.length % BYTE_BLOCK_SIZE != 0) {
            int extra = BYTE_BLOCK_SIZE - (array.length % BYTE_BLOCK_SIZE);
            byte[] newArray = new byte[array.length + extra];

            for (int i = 0; i < newArray.length; i++) {
                if (i < array.length) {
                    newArray[i] = array[i];
                } else {
                    newArray[i] = ASCII_CODE_FOR_PADDING;
                }
            }
            return newArray;
        }
        return array;
    }

    /**
     * Function to concatenate byte arrays
     *
     * @param arrays The byte arrays to be concatenated
     * @return the concatenated byte array
     */
    public static byte[] concatWithCopy(byte[]... arrays) {

        Class<?> compType1 = arrays[0].getClass().getComponentType();
        int totalLength = 0;

        for (byte[] array : arrays) {
            for (int j = 0; j < array.length; j++)
                totalLength++;
        }
        byte[] result = (byte[]) Array.newInstance(compType1, totalLength);

        int startingPosition = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, startingPosition, array.length);
            startingPosition += array.length;
        }

        return result;
    }

    /**
     * Utility function to convert byte array to an integer
     *
     * @param array byte array to be converted
     * @return integer converted from the given byte array
     */
    public static int byteArrayToInt(byte[] array) {
        ByteBuffer choiceBuffer = ByteBuffer.allocate(Integer.BYTES);
        choiceBuffer.put(array);
        choiceBuffer.rewind();
        return choiceBuffer.getInt();
    }

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
     * Function for rounding when dealing with monetary amounts
     *
     * @param value  the double value
     * @param places the number of decimal places to round
     * @return the rounded value
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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

