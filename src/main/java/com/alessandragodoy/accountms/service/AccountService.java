package com.alessandragodoy.accountms.service;

import com.alessandragodoy.accountms.controller.dto.AccountDTO;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.exception.CustomerNotFoundException;
import com.alessandragodoy.accountms.exception.InsufficientFundsException;

import java.util.List;

/**
 * Service interface for managing accounts.
 */
public interface AccountService {
	/**
	 * Retrieves all accounts.
	 *
	 * @return a list of all account DTOs
	 */
	List<AccountDTO> getAllAccounts();

	/**
	 * Retrieves an account by its ID.
	 *
	 * @param accountId the ID of the account
	 * @return the account DTO if found
	 * @throws AccountNotFoundException if the account is not found
	 */
	AccountDTO getAccountById(Integer accountId);

	/**
	 * Creates a new account.
	 *
	 * @param createAccountDTO the DTO containing account creation details
	 * @return the created account DTO
	 * @throws CustomerNotFoundException if the customer is not found
	 */
	AccountDTO createAccount(CreateAccountDTO createAccountDTO);

	/**
	 * Deposits an amount into an account.
	 *
	 * @param accountId the ID of the account
	 * @param amount    the amount to deposit
	 * @return the updated account DTO
	 * @throws AccountNotFoundException   if the account is not found
	 * @throws AccountValidationException if the amount is invalid
	 */
	AccountDTO deposit(Integer accountId, Double amount);

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
	AccountDTO withdraw(Integer accountId, Double amount);

	/**
	 * Deletes an account by its ID.
	 *
	 * @param accountId the ID of the account
	 * @return the deleted account DTO
	 * @throws AccountNotFoundException if the account is not found
	 */
	AccountDTO deleteAccountById(Integer accountId);

	/**
	 * Checks if active account exists for a given customer ID.
	 *
	 * @param customerId the ID of the customer
	 * @return true if an account exists, false otherwise
	 */
	boolean activeAccountExists(Integer customerId);

	/**
	 * Retrieves the balance of an account by its account number.
	 *
	 * @param accountNumber the account number
	 * @return the balance of the account
	 * @throws AccountNotFoundException if the account is not found
	 */
	Double getAccountBalance(String accountNumber);

	/**
	 * Checks if an account exists by its account number.
	 *
	 * @param accountNumber the account number
	 * @return true if an account exists, false otherwise
	 */
	boolean accountExistsByAccountNumber(String accountNumber);

	/**
	 * Updates the balance of an account by its account number.
	 *
	 * @param accountNumber the account number
	 * @param amount        the amount to update the balance by
	 * @throws AccountNotFoundException if the account is not found
	 */
	void updateBalanceByAccountNumber(String accountNumber, Double amount);
}
