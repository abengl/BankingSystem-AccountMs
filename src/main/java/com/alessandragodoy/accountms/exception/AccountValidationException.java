package com.alessandragodoy.accountms.exception;

/**
 * Exception thrown when account validation fails.
 */
public class AccountValidationException extends RuntimeException {
	public AccountValidationException(String message) {
		super(message);
	}
}
