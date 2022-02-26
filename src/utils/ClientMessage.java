package utils;

import objects.Currency;

public class ClientMessage {
    public static void DisplayAccountDetails(String accNumber, String name, Currency currency, double balance) {

        System.out.println("Account number: " + accNumber);
        System.out.println("Account holder: " + name);
        System.out.println("Currency: " + currency.toString());
        System.out.printf("Account balance: $%.2f\n", balance);
    }
}
