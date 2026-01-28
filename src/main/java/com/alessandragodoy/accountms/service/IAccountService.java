package com.alessandragodoy.accountms.service;

import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
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
	 */
	List<Account> getAllActiveAccounts();

	/**
	 * Retrieves an account by its ID.
	 *
	 * @param accountId the ID of the account
	 * @return {@code Account} the account if found
	 */
	Account getAccountById(Integer accountId);

	/**
	 * Creates a new account.
	 *
	 * @param createAccountDTO containing creation details
	 * @return {@code Account} created
	 */
	Account createAccount(CreateAccountDTO createAccountDTO);

	/**
	 * Activates an account by its ID.
	 *
	 * @param accountId the ID of the account to activate
	 * @return {@code Account} activated
	 */
	Account activateAccount(Integer accountId);

	/**
	 * Deactivates an account by its ID.
	 *
	 * @param accountId the ID of the account to deactivate
	 * @return {@code Account} deactivated
	 */
	Account deactivateAccount(Integer accountId);

	/**
	 * Deletes an account by its ID.
	 *
	 * @param accountId the ID of the account
	 */
	void deleteAccountById(Integer accountId);

	/**
	 * Retrieves all accounts associated with a specific customer ID.
	 *
	 * @param customerId the ID of the customer
	 * @return {@code List<Account>} a list of customer's accounts
	 */
	List<Account> getAccountsByCustomerId(Integer customerId);

}
