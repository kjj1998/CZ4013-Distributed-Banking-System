package utils;

import objects.Currency;

public class ClientMessage {
    /**
     * A function to print to the console the account number, account holder, currency and account balance of an account
     * @param accNumber the account number
     * @param name the name of the account holder
     * @param currency the currency of the account
     * @param balance the current balance in the account
     */
    public static void DisplayAccountDetails(String accNumber, String name, Currency currency, double balance) {
        System.out.println("Account number: " + accNumber);
        System.out.println("Account holder: " + name);
        System.out.println("Currency: " + currency.toString());
        System.out.printf("Account balance: $%.2f\n", balance);
    }

    public static void DisplayAccountDetailsMonitoring(String accNumber, String name, Currency currency, double balance, String action) {
        System.out.println("Action taken: " + action);
        System.out.println("Account number: " + accNumber);
        System.out.println("Account holder: " + name);
        System.out.println("Currency: " + currency.toString());
        System.out.printf("Account balance: $%.2f\n", balance);
    }

    public static void DisplayBalance(double balance) {
        System.out.printf("Account balance: $%.2f\n", balance);
    }
}
