package com.alessandragodoy.accountms.service;

import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.exception.CustomerNotFoundException;
import com.alessandragodoy.accountms.exception.InsufficientFundsException;
import com.alessandragodoy.accountms.model.Account;

import java.util.List;

/**
 * Service interface for managing accounts.
 */
public interface IAccountService {
	/**
	 * Retrieves all active accounts.
	 *
	 * @return {@code List<AccountDTO>} a list of all account DTOs
	 */
	List<Account> getAllActiveAccounts() throws Exception;

	/**
	 * Retrieves an account by its ID.
	 *
	 * @param accountId the ID of the account
	 * @return the account DTO if found
	 * @throws AccountNotFoundException if the account is not found
	 */
	Account getAccountById(Integer accountId) throws Exception;

	/**
	 * Creates a new account.
	 *
	 * @param createAccount the DTO containing account creation details
	 * @return the created account DTO
	 * @throws CustomerNotFoundException if the customer is not found
	 */
	Account createAccount(Account createAccount) throws Exception;

	/**
	 * Deposits an amount into an account.
	 *
	 * @param accountId the ID of the account
	 * @param amount    the amount to deposit
	 * @return the updated account DTO
	 * @throws AccountNotFoundException   if the account is not found
	 * @throws AccountValidationException if the amount is invalid
	 */
	Account deposit(Integer accountId, Double amount);

	/**
	 * Withdraws an amount from an account.
	 *
	 * @param accountId the ID of the account
	 * @param amount    the amount to withdraw
	 * @return the updated account DTO
	 * @throws AccountNotFoundException   if the account is not found
	 * @throws AccountValidationException if the amount is invalid
	 * @throws InsufficientFundsException if there are insufficient funds for the withdrawal
	 */
	Account withdraw(Integer accountId, Double amount);

	/**
	 * Deletes an account by its ID.
	 *
	 * @param accountId the ID of the account
	 * @throws AccountNotFoundException if the account is not found
	 */
	void deleteAccountById(Integer accountId) throws Exception;

	/**
	 * Checks if active account exists for a given customer ID.
	 *
	 * @param customerId the ID of the customer
	 * @return true if an account exists, false otherwise
	 */
	boolean accountIsActiveByCustomerId(Integer customerId) throws Exception;

	/**
	 * Retrieves the balance of an account by its account number.
	 *
	 * @param accountNumber the account number
	 * @return the balance of the account
	 * @throws AccountNotFoundException if the account is not found
	 */
	double getAccountBalance(String accountNumber) throws Exception;

	/**
	 * Checks if an account exists by its account number.
	 *
	 * @param accountNumber the account number
	 * @return true if an account exists, false otherwise
	 */
	boolean accountExistsByAccountNumber(String accountNumber) throws Exception;

	/**
	 * Updates the balance of an account by its account number.
	 *
	 * @param accountNumber the account number
	 * @param amount        the amount to update the balance by
	 * @throws AccountNotFoundException if the account is not found
	 */
	void updateBalanceByAccountNumber(String accountNumber, Double amount);

	Account activateAccount(Integer accountId) throws Exception;

	Account deactivateAccount(Integer accountId) throws Exception;

	List<Account> getAccountsByCustomerId(Integer customerId) throws Exception;

	double getBalanceByAccountId(Integer accountId) throws Exception;

	boolean accountIsActiveByAccountId(Integer accountId) throws Exception;

	void updateBalanceByAccountId(Integer accountId, Double amount) throws Exception;
}
