package utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * A utility class to generate a random alphanumeric string of length n, where n >= 1
 * Adapted from https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 */
public class MessageIDGenerator {
    /* Declaration of constants and character arrays */
    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lower = upper.toLowerCase(Locale.ROOT);
    public static final String digits = "0123456789";
    public static final String alphanum = upper + lower + digits;

    /* Declaration of variables */
    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    /**
     * Main Constructor to initialize the Random object and the character arrays
     *
     * @param length  the length of the random alphanumeric string to be generated
     * @param random  the Java Random object
     * @param symbols the String containing the symbols that will be used to form the random String
     */
    public MessageIDGenerator(int length, Random random, String symbols) {
        if (length < 1)
            throw new IllegalArgumentException();
        if (symbols.length() < 2)
            throw new IllegalArgumentException();

        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Simplified constructor to set up the random string generator
     *
     * @param length an integer value containing the length of the random string to be generated
     */
    public MessageIDGenerator(int length) {
        this(length, new SecureRandom(), alphanum);
    }


    /**
     * The method to actually generate a random string
     *
     * @return the randomly generated alphanumeric String
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}
