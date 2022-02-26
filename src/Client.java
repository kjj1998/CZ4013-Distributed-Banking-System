import objects.Account;
import objects.Currency;

import java.util.Scanner;

import static functionalities.ClientInterface.*;
import static utils.ClientMessage.DisplayAccountDetails;
import static utils.Constants.*;
import static utils.ReadingInputs.*;

public class Client {
    public static void main(String[] args) {
        boolean end = false;
        String name, password, initialBalance, accNumber;
        double balance;
        Currency currency;

        System.out.printf("%20s\n","Welcome to CZ4013 Bank!");
        while (!end) {
            try {
                System.out.printf("%s\n", "What would you like to do? (Key in the number for your choice)");
                System.out.printf("%s\n", "1. Open a new account");
                System.out.printf("%s\n", "4. Close an existing account");
                System.out.printf("%s\n", "8. Show current account initialBalance");
                System.out.printf("%s\n", "0. Exit");

                Scanner scanner = new Scanner(System.in);
                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case ACC_CREATION_CODE: {
                        System.out.println("Opening a new account...");
                        name = readNameInput();
                        currency = readCurrencyInput();
                        password = readPassword(NEW);
                        initialBalance = readMoney(NEW);

                        Account temp = createAccount(name, currency, password, initialBalance);
                        System.out.println("Account created with the following details");
                        System.out.println("------------------------------------------");
                        DisplayAccountDetails(temp.getAccNumber(), temp.getName(), temp.getCurrency(), temp.getAccBalance());
                        break;
                    }
                    case ACC_CLOSING_CODE:
                        System.out.println("Closing account...");
                        accNumber = readAccountNumber();
                        name = readNameInput();
                        password = readPassword(EXISTING);
                        accNumber = closeAccount(name, password, accNumber);

                        System.out.println("The following account is closed");
                        System.out.println("Account Number: " + accNumber);
                        System.out.println("Account Holder: " + name);
                        break;
                    case ACC_BALANCE_CODE:
                        System.out.println("Querying account initialBalance...");
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);

                        balance = queryAccBalance(accNumber, password);
                        System.out.println("Account number: " + accNumber);
                        System.out.printf("Account initialBalance: $%.2f\n", balance);
                        break;
                    case 0:
                        System.out.println("Thank you for banking with us, goodbye!");
                        end = true;
                        break;
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


        // int bankAcc = createAccount("John Smith", Currency.NZD, "P@ssword123", "1000.00");
        // double val = queryAccBalance(Integer.toString(bankAcc), "P@ssword123");
    }
}
