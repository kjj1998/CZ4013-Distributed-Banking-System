package objects;
import java.text.DecimalFormat;

import java.util.Objects;
import static utils.Constants.sgdExchangeRate;
import static utils.Constants.nzdExchangeRate;
import static utils.Constants.usdExchangeRate;
import static utils.Constants.INSUFFICIENT;

public class Account {
    private final String name;
    private final Currency cur;
    private String password;
    private double accBalance;
    private final int accNumber;
    private String action;

    public Account(String name, Currency cur, String password, double accBalance, int accNumber) {
        this.name = name;
        this.cur = cur;
        this.password = password;
        this.accBalance = accBalance;
        this.accNumber = accNumber;
    }

    public Account(String name, Currency cur, double accBalance, int accNumber, String action) {
        this.name = name;
        this.cur = cur;
        this.accBalance = accBalance;
        this.accNumber = accNumber;
        this.action = action;
    }

    public Account(String name, Currency cur, String password, double accBalance, int accNumber, String action) {
        this.name = name;
        this.cur = cur;
        this.password = password;
        this.accBalance = accBalance;
        this.accNumber = accNumber;
        this.action = action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public String getAccNumber() {
        return String.valueOf(accNumber);
    }

    public String getName() {
        return name;
    }

    public Currency getCurrency() {
        return cur;
    }

    public void withdraw(double amt,String currency) {
        double accCurrencyRate=getCurrencyRate(cur.toString());
        double depositCurrencyRate=getCurrencyRate(currency);

        amt=(amt/depositCurrencyRate)*accCurrencyRate;
        amt=Math.round(amt*100.0)/100.0;

        if (accBalance < amt)
            throw new IllegalArgumentException(INSUFFICIENT);
        this.accBalance -= amt;
    }

    public void deposit(double amt,String currency) {
        double accCurrencyRate=getCurrencyRate(cur.toString());
        double depositCurrencyRate=getCurrencyRate(currency);

        amt=(amt/depositCurrencyRate)*accCurrencyRate;
        amt=Math.round(amt*100.0)/100.0;
        this.accBalance += amt;
    }

    public boolean verifyPassword(String text) {
        return Objects.equals(password, text);
    }
    public boolean verifyName(String text) {
        return Objects.equals(name, text);
    }

    public double getAccBalance() {
        return this.accBalance;
    }

    public double getCurrencyRate(String cur){
        if (cur.equals("SGD"))
            return sgdExchangeRate;
        if (cur.equals("NZD"))
            return nzdExchangeRate;
        if (cur.equals("USD"))
            return usdExchangeRate;
        return 0.00;
    }
    
}
