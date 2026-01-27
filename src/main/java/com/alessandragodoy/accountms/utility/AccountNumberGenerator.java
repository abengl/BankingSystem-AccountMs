package com.alessandragodoy.accountms.utility;

import java.util.UUID;

/**
 * Utility class responsible for generating unique account numbers.
 */
public class AccountNumberGenerator {

    private AccountNumberGenerator() {
    }

    /**
     * Generates a unique account number using UUID.
     *
     * @return a unique account number as a String
     */
    public static String generateAccountNumber() {
        return "ACC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
}
