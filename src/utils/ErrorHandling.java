package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorHandling {

    /**
     * A function to check that input for name is well-formed
     * @param name the text string to be checked
     * @return whether the name is formed correctly
     */
    public static boolean verifyName(String name) {
        String regex = "^[A-Za-z\\s]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    /**
     * A function to check that a new password is well-formed
     * @param password the text string to be checked
     * @return whether the password is formed correctly
     */
    public static boolean verifyNewPassword(String password) {
        if (password.length() < 8)
            return false;

        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern digits = Pattern.compile("[0-9]");
        Pattern specialChar = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        Matcher matchUpperCase = upperCase.matcher(password);
        Matcher matchLowerCase = lowerCase.matcher(password);
        Matcher matchDigits = digits.matcher(password);
        Matcher matchSpecialChar = specialChar.matcher(password);

        return matchUpperCase.find() && matchLowerCase.find() && matchDigits.find() && matchSpecialChar.find();
    }

    /**
     * A function to verified that monetary amounts are entered correctly in dollars and cents
     * @param amt the text string to be checked
     * @return whether the monetary amount is formed correctly
     */
    public static boolean verifyMoney(String amt) {
        Pattern pattern = Pattern.compile("^[1-9]{1}[0-9]*.[0-9]{2}$");
        Matcher matcher = pattern.matcher(amt);

        return matcher.matches();
    }

    /**
     * A function to verify that the account number entered is well-formed
     * @param accNumber the text string to be checked
     * @return whether the account number is formed correctly
     */
    public static boolean verifyAccNumber(String accNumber) {
        Pattern pattern = Pattern.compile("^[0-9]{10}$");
        Matcher matcher = pattern.matcher(accNumber);

        return matcher.matches();
    }
}
