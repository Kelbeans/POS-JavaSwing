package common.helperMethods;

import java.util.Random;

public class TransactionIdGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();
    private static final int TRANSACTION_ID_LENGTH = 12;

    public static String generateTransactionId() {
        StringBuilder transactionId = new StringBuilder(TRANSACTION_ID_LENGTH);
        for(int i = 0; i < TRANSACTION_ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            transactionId.append(CHARACTERS.charAt(index));
        }
        System.out.println("Generated Transaction ID: " + transactionId);
        return transactionId.toString();
    }
}
