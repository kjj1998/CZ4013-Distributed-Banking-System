import objects.Currency;

import java.util.Scanner;

import static functionalities.ClientInterface.createAccount;
import static functionalities.ClientInterface.queryAccBalance;
import static utils.ClientMessage.DisplayAccountDetails;
import static utils.Constants.*;
import static utils.ReadingInputs.*;

public class Client {
    public static void main(String[] args) {
        boolean end = false;

        System.out.printf("%20s\n","Welcome to CZ4013 Bank!");
        while (!end) {
            try {
                System.out.printf("%s\n", "What would you like to do? (Key in the number for your choice)");
                System.out.printf("%s\n", "1. Open a new account");
                System.out.printf("%s\n", "4. Close an existing account");
                System.out.printf("%s\n", "8. Show current account balance");
                System.out.printf("%s\n", "0. Exit");

                Scanner scanner = new Scanner(System.in);
                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case ACC_OPENING_CODE: {
                        System.out.println("Opening a new account...");
                        String name = readNameInput();
                        Currency chosenCurrency = readCurrencyInput();
                        String password = readPassword(NEW);
                        String initialBal = readMoney(NEW);

                        int accNumber = createAccount(name, chosenCurrency, password, initialBal);
                        System.out.println("Account created with the following details");
                        System.out.println("------------------------------------------");
                        DisplayAccountDetails(accNumber, name, chosenCurrency, initialBal);
                        break;
                    }
                    case ACC_CLOSING_CODE:
                        System.out.println("close acc");
                        break;
                    case ACC_BALANCE_CODE:
                        System.out.println("Querying account balance...");
                        String accNumber = readAccountNumber();
                        String password = readPassword(EXISTING);

                        double balance = queryAccBalance(accNumber, password);

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
