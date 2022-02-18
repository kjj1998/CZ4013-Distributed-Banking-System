package objects;

import java.util.Objects;

public class Account {
    private String name;
    private Currency cur;
    private String password;
    private double accBalance;
    private int accNumber;

    public Account(String name, Currency cur, String password, double accBalance, int accNumber) {
        this.name = name;
        this.cur = cur;
        this.password = password;
        this.accBalance = accBalance;
        this.accNumber = accNumber;
    }

    public int getAccNumber() {
        return accNumber;
    }

    public Currency getCurrency() {
        return cur;
    }

    public void withdraw(double amt) {
        if (accBalance < amt)
            throw new IllegalArgumentException();

        this.accBalance -= amt;
    }

    public void deposit(double amt) {
        this.accBalance += amt;
    }

    public boolean verifyPassword(String text) {
        return Objects.equals(password, text);
    }

    public double getAccBalance() {
        return this.accBalance;
    }
}
