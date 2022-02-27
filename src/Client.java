import objects.Account;
import objects.Currency;

import java.util.Objects;
import java.util.Scanner;

import static functionalities.ClientInterface.*;
import static utils.ClientMessage.DisplayAccountDetails;
import static utils.Constants.*;
import static utils.ReadingInputs.*;

public class Client {
    public static void main(String[] args) {
        boolean end = false;
        String name, password, initialBalance, accNumber;
        Currency currency;
        Account temp;

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

                        temp = createAccount(name, currency, password, initialBalance);
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

                        temp = closeAccount(name, password, accNumber);
                        System.out.println("The following account is closed");
                        DisplayAccountDetails(temp.getAccNumber(), temp.getName(), temp.getCurrency(), temp.getAccBalance());
                        break;
                    case ACC_BALANCE_CODE:
                        System.out.println("Querying account initialBalance...");
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);

                        temp = queryAccBalance(accNumber, password);
                        DisplayAccountDetails(temp.getAccNumber(), temp.getName(), temp.getCurrency(), temp.getAccBalance());
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
            } catch (IllegalArgumentException serverError) {
                if (Objects.equals(serverError.getMessage(), NOT_FOUND)) {
                    System.out.println("Error: Account Number not found");
                } else if (Objects.equals(serverError.getMessage(), UNAUTHORIZED)) {
                    System.out.println("Error: Wrong name/password entered.");
                }
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Unknown error\n");
            }
        }
    }
}
