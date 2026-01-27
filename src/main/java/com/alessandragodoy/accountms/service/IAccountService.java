package com.alessandragodoy.accountms.service;

import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.exception.CustomerNotFoundException;
import com.alessandragodoy.accountms.model.Account;

import java.util.List;

/**
 * Service interface for managing accounts.
 */
public interface IAccountService {
	/**
	 * Retrieves all active accounts.
	 *
	 * @return {@code List<Account>} a list of all account DTOs
	 * @throws Exception if an error occurs during retrieval
	 */
	List<Account> getAllActiveAccounts() throws Exception;

	/**
	 * Retrieves an account by its ID.
	 *
	 * @param accountId the ID of the account
	 * @return {@code Account} the account if found
	 * @throws AccountNotFoundException if the account is not found
	 */
	Account getAccountById(Integer accountId) throws Exception;

	/**
	 * Creates a new account.
	 *
	 * @param account containing creation details
	 * @return {@code Account} created
	 * @throws CustomerNotFoundException if the customer is not found
	 */
	Account createAccount(Account account) throws Exception;

	/**
	 * Activates an account by its ID.
	 *
	 * @param accountId the ID of the account to activate
	 * @return {@code Account} activated
	 * @throws AccountNotFoundException if the account is not found
	 */
	Account activateAccount(Integer accountId) throws Exception;

	/**
	 * Deactivates an account by its ID.
	 *
	 * @param accountId the ID of the account to deactivate
	 * @return {@code Account} deactivated
	 * @throws AccountNotFoundException   if the account is not found
	 * @throws AccountValidationException if the account cannot be deactivated
	 */
	Account deactivateAccount(Integer accountId) throws Exception;

	/**
	 * Deletes an account by its ID.
	 *
	 * @param accountId the ID of the account
	 * @throws AccountNotFoundException   if the account is not found
	 * @throws AccountValidationException if the account cannot be deleted
	 */
	void deleteAccountById(Integer accountId) throws Exception;

	/**
	 * Retrieves all accounts associated with a specific customer ID.
	 *
	 * @param customerId the ID of the customer
	 * @return {@code List<Account>} a list of customer's accounts
	 * @throws CustomerNotFoundException if the customer is not found
	 */
	List<Account> getAccountsByCustomerId(Integer customerId) throws Exception;

}
