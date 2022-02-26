package utils;

import objects.Currency;

public class ClientMessage {
    public static void DisplayAccountDetails(int accNumber, String name, Currency currency, String balance) {

        System.out.println("Account number: " + accNumber);
        System.out.println("Account holder: " + name);
        System.out.println("Currency: " + currency.toString());
        System.out.println("Account balance: $" + balance);
    }
}
