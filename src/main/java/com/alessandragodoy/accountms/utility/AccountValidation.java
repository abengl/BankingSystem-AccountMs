package com.alessandragodoy.accountms.utility;

import com.alessandragodoy.accountms.adapter.AccountAdapter;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.exception.CustomerNotFoundException;
import com.alessandragodoy.accountms.exception.InsufficientFundsException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.model.AccountType;
import org.springframework.stereotype.Component;

/**
 * Utility class for validating account-related operations.
 * <p>
 * This class provides static methods for validating amounts and sufficient funds in accounts.
 * It follows the <b>Utility design pattern</b>, which provides a collection of static methods
 * that can be used without instantiating the class.
 * </p>
 */
@Component
public class AccountValidation {
	private final AccountAdapter accountAdapter;

	public AccountValidation(AccountAdapter accountAdapter) {
		this.accountAdapter = accountAdapter;
	}

	/**
	 * Validates that the given amount is greater than zero.
	 *
	 * @param amount the amount to validate.
	 * @throws AccountValidationException if the amount is null or less than or equal to zero.
	 */
	public static void validateAmount(Double amount) {
		if (amount == null || amount <= 0) {
			throw new AccountValidationException("Amount must be greater than zero.");
		}
	}

	/**
	 * Validates that the account has sufficient funds for the specified amount.
	 * <p>
	 * For savings accounts, the current balance must be greater than or equal to the amount.
	 * For checking accounts, the current balance minus the amount must be greater than or equal to -500.
	 * </p>
	 *
	 * @param account        the account to validate.
	 * @param currentBalance the current balance of the account.
	 * @param amount         the amount to validate.
	 * @throws InsufficientFundsException if the account does not have sufficient funds.
	 */
	public static void validateSufficientFunds(Account account, Double currentBalance, Double amount) {
		if (account.getAccountType() == AccountType.SAVINGS && currentBalance < amount) {
			throw new InsufficientFundsException("Insufficient funds for withdrawal.");
		} else if (account.getAccountType() == AccountType.CHECKING && currentBalance - amount < -500) {
			throw new InsufficientFundsException("Insufficient funds for withdrawal. Account overdraft limit reached" +
					".");
		}
	}

	/**
	 * Validates that the customer exists for the given customer ID.
	 *
	 * @param customerId the ID of the customer to validate.
	 * @throws CustomerNotFoundException if the customer does not exist.
	 */
	public void validateCustomerExists(Integer customerId) {
		if (!accountAdapter.customerExists(customerId)) {
			throw new CustomerNotFoundException("Customer not found for ID: " + customerId);
		}
	}

}
