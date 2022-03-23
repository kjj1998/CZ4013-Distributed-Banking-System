package utils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.Random;

import static utils.Constants.*;

public class UtilityFunctions {
    private static final Random msgFailSim = new Random();

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

    public static boolean failMessage(String side){
        float failProb = 0f + msgFailSim.nextFloat();

        if(side.equals("client")){
//            System.out.println("Client fail prob: " + failProb);
            return failProb <= CLIENT_FAILURE_PROB;
        }else{
//            System.out.println("Server fail prob: " + failProb);
            return failProb <= SERVER_FAILURE_PROB;
        }
    }
}

