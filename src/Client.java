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
        String name, password, initialBalance, accNumber,toAccNumber;
        Currency currency;
        Account temp;
        Double deposit, withdraw, transfer;
        //If atLeastonce schematic is used, set atLeaseOnce as true
        boolean atLeastOnce=true;

        System.out.printf("%20s\n","Welcome to CZ4013 Bank!");
        while (!end) {
            try {
                System.out.printf("%s\n", "What would you like to do? (Key in the number for your choice)");
                System.out.printf("%s\n", "1. Open a new account");
                System.out.printf("%s\n", "2. Deposit money");
                System.out.printf("%s\n", "3. Withdraw money");
                System.out.printf("%s\n", "4. Close an existing account");
                System.out.printf("%s\n", "5. Transfer money");
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
                        temp = createAccount(name, currency, password, initialBalance,atLeastOnce);

                        System.out.println("Account created with the following details");
                        System.out.println("------------------------------------------");
                        assert temp != null : "Account object is null";
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
                        double balance = depositMoney(name,accNumber,password,currency,deposit,atLeastOnce);  
                        //double balance = depositMoney("John", 123, "123", Currency.SGD, 1000, atLeastOnce);                   
                        break;
                    }
                    case WITHDRAW_MONEY_CODE:{
                        System.out.println("Withdrawing money...");                  
                        name = readNameInput();
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);
                        currency = readCurrencyInput();
                        withdraw = readTransfer();
                        double balance = withdrawMoney(name,accNumber,password,currency,withdraw,atLeastOnce);                        
                        break;
                    }
                    case ACC_CLOSING_CODE:
                        System.out.println("Closing account...");

                        accNumber = readAccountNumber();
                        name = readNameInput();
                        password = readPassword(EXISTING);
                        temp = closeAccount(name, password, accNumber,atLeastOnce);

                        System.out.println("The following account is closed");
                        DisplayAccountDetails(temp.getAccNumber(), temp.getName(), temp.getCurrency(), temp.getAccBalance());

                    case TRANSFER_MONEY_CODE:{
                        System.out.println("Transfering money...");                
                        name = readNameInput();
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);
                        toAccNumber=readRecipientAccountNumber();
                        currency = readCurrencyInput();
                        transfer=readTransfer();
                        double balance = transferMoney(name,accNumber,password,toAccNumber,currency,transfer,atLeastOnce);                        
                        break;
                    }
                    case ACC_BALANCE_CODE:
                        System.out.println("Querying account balance...");
                        accNumber = readAccountNumber();
                        password = readPassword(EXISTING);

                        temp = queryAccBalance(accNumber, password,atLeastOnce);
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
                    System.out.println("Error: Wrong name/password entered");
                }
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Unknown error\n");
            }
        }
    }
}
