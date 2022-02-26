package utils;

import objects.Currency;

import java.util.Scanner;

import static utils.ErrorHandling.*;

public class ReadingInputs {
    static Scanner scanner = new Scanner(System.in);

    public static String readNameInput() {
        System.out.println("Enter name of the account holder: ");
        while (true) {
            String name = scanner.nextLine();
            if (verifyName(name))
                return name;
            System.out.println("Invalid input, please re-enter:");
        }
    }

    public static Currency readCurrencyInput() {
        while (true) {
            try {
                System.out.println("Select from the list of currencies below (key in the number for your choice): ");
                System.out.printf("%s\n", "1. SGD");
                System.out.printf("%s\n", "2. NZD");
                System.out.printf("%s\n", "3. USD");

                Scanner scanner = new Scanner(System.in);
                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1:
                        return Currency.valueOf("SGD");
                    case 2:
                        return Currency.valueOf("NZD");
                    case 3:
                        return Currency.valueOf("USD");
                    default:
                        System.out.printf("%s\n", "Unavailable choice entered");
                        break;
                }
            } catch (NumberFormatException invalidFormat) {
                System.out.println("Error: Invalid input entered");
            } catch (Exception e) {
                throw new IllegalArgumentException("Unknown error\n");
            }
        }

    }

    public static String readPassword(char status) {
        if (status == 'e') {
            System.out.println("Enter password: ");
            return scanner.nextLine();
        } else {
            System.out.println("Enter new password: ");
            while (true) {
                String password = scanner.nextLine();
                if (verifyNewPassword(password))
                    return password;
                System.out.println("Invalid input.");
                System.out.println("Password requires at least 8 characters");
                System.out.println("Password requires at least 1 uppercase character.");
                System.out.println("Password requires at least 1 lowercase character.");
                System.out.println("Password requires at least 1 digit.");
                System.out.println("Password requires at least one special character.");
            }
        }
    }

    public static String readMoney(char status) {
        while (true) {
            if (status == 'e') {
                System.out.println("Enter amount: ");
            } else {
                System.out.println("Enter initial account balance: ");
            }
            String amt = scanner.nextLine();
            if (verifyMoney(amt))
                return amt;
            System.out.println("Invalid input, not in dollars and cents");
        }
    }

    public static String readAccountNumber() {
        while (true) {
            System.out.println("Enter account number: ");
            String accNumber = scanner.nextLine();
            if (verifyAccNumber(accNumber))
                return accNumber;
            System.out.println("Invalid account number entered");
        }
    }

    public static void main(String[] args) {
        // String name = readNameInput();
        // Currency currency = readCurrencyInput();
        // String password = readPassword('n');
        // String amt = readMoney('n');
        String accNumber = readAccountNumber();
    }
}
