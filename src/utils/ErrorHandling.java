package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorHandling {

    public static boolean verifyName(String name) {
        String regex = "^[A-Za-z\\s]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

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

    public static boolean verifyMoney(String amt) {
        Pattern pattern = Pattern.compile("^[1-9]{1}[0-9]*.[0-9]{2}$");
        Matcher matcher = pattern.matcher(amt);

        return matcher.matches();
    }
}
