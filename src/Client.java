import objects.Account;
import objects.Currency;

import java.util.Objects;
import java.util.Scanner;

import static functionalities.ClientInterface.*;
import static utils.ClientMessage.DisplayAccountDetails;
import static utils.ClientMessage.DisplayBalance;
import static utils.Constants.*;
import static utils.ReadingInputs.*;

public class Client {
    public static void main(String[] args) {
        boolean end = false;
        String name, password, initialBalance, accNumber,toAccNumber;
        Currency currency;
        Account temp;
        double deposit, withdraw, transfer;
        int monitorDuration;

        System.out.printf("%20s\n","Welcome to CZ4013 Bank!");
        while (!end) {
            try {
                System.out.printf("\n%s\n", "What would you like to do? (Key in the number for your choice)");
                System.out.printf("%s\n", "1. Open a new account");
                System.out.printf("%s\n", "2. Deposit money");
                System.out.printf("%s\n", "3. Withdraw money");
                System.out.printf("%s\n", "4. Close an existing account");
                System.out.printf("%s\n", "5. Transfer money");
                System.out.printf("%s\n", "6. Monitor updates");
                System.out.printf("%s\n", "7. Show current account balance");
                System.out.printf("%s\n", "0. Exit");

                Scanner scanner = new Scanner(System.in);
                int service = Integer.parseInt(scanner.nextLine());

                switch (service) {
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
                    case DEPOSIT_MONEY_CODE:{
                        System.out.println("Depositing money...");
                        name = readNameInput();
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);
                        currency = readCurrencyInput();
                        deposit = readDeposit();

                        temp = depositMoney(name, accNumber, password, currency, deposit);
                        System.out.println("Deposit done!");
                        DisplayBalance(temp.getAccBalance());
                        break;
                    }
                    case WITHDRAW_MONEY_CODE:{
                        System.out.println("Withdrawing money...");
                        name = readNameInput();
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);
                        currency = readCurrencyInput();
                        withdraw = readWithdraw();

                        temp = withdrawMoney(name,accNumber,password,currency,withdraw);
                        System.out.println("Withdraw done!");
                        DisplayBalance(temp.getAccBalance());
                        break;
                    }
                    case ACC_CLOSING_CODE: {
                        System.out.println("Closing account...");

                        accNumber = readAccountNumber();
                        name = readNameInput();
                        password = readPassword(EXISTING);
                        temp = closeAccount(name, password, accNumber);

                        System.out.println("The following account is closed");
                        DisplayAccountDetails(temp.getAccNumber(), temp.getName(), temp.getCurrency(), temp.getAccBalance());
                        break;
                    }
                    case TRANSFER_MONEY_CODE:{
                        System.out.println("Transferring money...");
                        name = readNameInput();
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);
                        toAccNumber=readRecipientAccountNumber();
                        currency = readCurrencyInput();
                        transfer=readTransfer();

                        temp = transferMoney(name,accNumber,password, toAccNumber, currency,transfer);
                        System.out.println("Transfer done!");
                        DisplayBalance(temp.getAccBalance());
                        break;
                    }
                    case ACC_BALANCE_CODE: {
                        System.out.println("Querying account balance...");
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);

                        temp = queryAccBalance(accNumber, password);
                        //DisplayAccountDetails(temp.getAccNumber(), temp.getName(), temp.getCurrency(), temp.getAccBalance());
                        DisplayBalance(temp.getAccBalance());
                        break;
                    }
                    case ADD_OBSERVERS_FOR_MONITORING_CODE: {
                        System.out.println("Monitoring updates...");
                        monitorDuration = readMonitorDuration();
                        monitorUpdates(monitorDuration);
                        System.out.println("Monitoring ended");
                        break;
                    }
                    case 0: {
                        System.out.println("Thank you for banking with us, goodbye!");
                        end = true;
                        break;
                    }
                    default: {
                        System.out.printf("%s\n", "Unavailable choice entered");
                        break;
                    }
                }
            } catch (NumberFormatException invalidFormat) {
                System.out.println("Error: Invalid input entered");
            } catch (IllegalArgumentException serverError) {
                if (Objects.equals(serverError.getMessage(), NOT_FOUND)) {
                    System.out.println("Error: Account Number not found");
                } else if (Objects.equals(serverError.getMessage(), UNAUTHORIZED)) {
                    System.out.println("Error: Wrong name/password entered");
                } else if (Objects.equals(serverError.getMessage(), INSUFFICIENT)){
                    System.out.println("Error: Insufficient amount in account");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Unknown error\n");
            }
        }
    }
}
