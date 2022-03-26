package utils;

import objects.Currency;

import java.util.Scanner;

import static utils.Constants.EXISTING;
import static utils.ErrorHandling.*;

public class ReadingInputs {
    static Scanner scanner = new Scanner(System.in);

    /**
     * A function to read in name inputs
     * @return the String containing the name that was read in
     */
    public static String readNameInput() {
        System.out.println("Enter name of the account holder: ");
        while (true) {
            String name = scanner.nextLine();
            if (verifyName(name))
                return name;
            System.out.println("Invalid input, please re-enter:");
        }
    }

    /**
     * A function to select the currency from a list of currencies
     * @return the currency chosen
     */
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

    /**
     * A function to read in password
     * @param status a char to denote whether a new or an existing password is being read
     * @return the password string
     */
    public static String readPassword(char status) {
        if (status == EXISTING) {
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

    /**
     * A function to read in money
     * @param status a char to denote whether an initial account balance is being entered or not
     * @return the money string
     */
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

    /**
     * A function to read in money for deposits
     * @return the monetary to be deposited
     */
    public static Double readDeposit() {
        while (true) {
            System.out.println("Enter the amount to deposit: ");
            String amt = scanner.nextLine();
            if (verifyMoney(amt))
                return Double.parseDouble(amt);
            System.out.println("Invalid input, not in dollars and cents");
        }
    }

    /**
     * A function to read in money for withdrawal
     * @return the monetary to be withdrawn
     */
    public static Double readWithdraw() {
        while (true) {
            System.out.println("Enter the amount to withdraw: ");
            String amt = scanner.nextLine();
            if (verifyMoney(amt))
                return Double.parseDouble(amt);
            System.out.println("Invalid input, not in dollars and cents");
        }
    }

    /**
     * A function to read in money for transfer
     * @return the monetary to be withdrawn for transfer
     */
    public static Double readTransfer() {
        while (true) {
            System.out.println("Enter the amount to transfer: ");
            String amt = scanner.nextLine();
            if (verifyMoney(amt))
                return Double.parseDouble(amt);
            System.out.println("Invalid input, not in dollars and cents");
        }
    }
    /**
     * A function to read in the account number
     * @return the account number string
     */
    public static String readAccountNumber() {
        while (true) {
            System.out.println("Enter account number: ");
            String accNumber = scanner.nextLine();
            if (verifyAccNumber(accNumber))
                return accNumber;
            System.out.println("Invalid account number entered");
        }
    }

    /**
     * A function to read in the recipient account number
     * @return the recipient account number string
     */
    public static String readRecipientAccountNumber() {
        while (true) {
            System.out.println("Enter recipient account number: ");
            String accNumber = scanner.nextLine();
            if (verifyAccNumber(accNumber))
                return accNumber;
            System.out.println("Invalid account number entered");
        }
    }

    /**
     * A function to read in duration for monitoring of server in seconds
     * @return the recipient account number string
     */
    public static int readMonitorDuration() {
        while (true) {
            try {
                System.out.println("Entering monitoring duration (in seconds): ");
                String duration = scanner.nextLine();
                return Integer.parseInt(duration);
            } catch (NumberFormatException invalidFormat) {
                System.out.println("Invalid input entered");
            }
        }
    }
}
