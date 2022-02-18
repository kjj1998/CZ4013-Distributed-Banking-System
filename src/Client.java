import objects.Currency;
import utils.MessageIDGenerator;

import static functionalities.ClientInterface.createAccount;
import static functionalities.ClientInterface.queryAccBalance;
import static utils.Constants.MESSAGE_ID_LENGTH;

public class Client {
    public static MessageIDGenerator gen = new MessageIDGenerator(MESSAGE_ID_LENGTH);   // create a new MessageIDGenerator

    public static void main(String[] args) {

        int bankAcc = createAccount("John Smith", Currency.NZD, "P@ssword123", "1000.00");
        double val = queryAccBalance(Integer.toString(bankAcc), "P@ssword123");
    }
}
