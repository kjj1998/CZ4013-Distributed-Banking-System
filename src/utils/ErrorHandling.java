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
}
