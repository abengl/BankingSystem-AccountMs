package com.alessandragodoy.accountms.controller;

import com.alessandragodoy.accountms.controller.dto.AccountDTO;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.controller.dto.TransactionRequestDTO;
import com.alessandragodoy.accountms.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing accounts.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Controller for Account")
public class AccountController {
	private final AccountService accountService;

	/**
	 * Retrieves all active accounts.
	 *
	 * @return {@code ResponseEntity<List<AccountDTO>>} containing a list of accounts.
	 * @throws Exception if an error occurs while retrieving accounts.
	 */
	@Operation(summary = "Retrieve all active accounts", description = "Returns a list of " +
			"AccountDTO")
	@GetMapping
	public ResponseEntity<List<AccountDTO>> getAllAccounts() throws Exception {

		List<AccountDTO> accounts = accountService.getAllAccounts();

		return ResponseEntity.ok(accounts);
	}

	/**
	 * Retrieves an account by its ID.
	 *
	 * @param accountId the ID of the account to retrieve.
	 * @return a {@code ResponseEntity<AccountDTO>} containing the account.
	 * @throws Exception if an error occurs while retrieving the account.
	 */
	@Operation(summary = "Retrieve account by its id", description = "Returns the found account as" +
			" AccountDTO")
	@GetMapping("/{accountId}")
	public ResponseEntity<AccountDTO> getAccountById(@PathVariable Integer accountId)
			throws Exception {

		AccountDTO account = accountService.getAccountById(accountId);

		return ResponseEntity.ok(account);
	}

	/**
	 * Creates a new account.
	 *
	 * @param createAccountDTO the data transfer object containing the account details.
	 * @return {@code ResponseEntity<AccountDTO>} containing the created account.
	 * @throws Exception if an error occurs while creating the account.
	 */
	@Operation(summary = "Creates an account with specific data", description = "Returns the " +
			"account created as AccountDTO")
	@PostMapping
	public ResponseEntity<AccountDTO> createAccount(@RequestBody CreateAccountDTO createAccountDTO)
			throws Exception {

		AccountDTO account = accountService.createAccount(createAccountDTO);

		return ResponseEntity.ok(account);
	}

	/**
	 * Activates an account by its ID.
	 *
	 * @param accountId the ID of the account to activate.
	 * @return a {@code ResponseEntity<AccountDTO>} containing the activated account.
	 * @throws Exception if an error occurs while activating the account.
	 */
	@Operation(summary = "Activate an account by its id", description = "Returns the activated " +
			"account as AccountDTO")
	@PatchMapping("/activate/{accountId}")
	public ResponseEntity<AccountDTO> activateAccount(@PathVariable Integer accountId)
			throws Exception {

		AccountDTO activatedAccount = accountService.activateAccount(accountId);

		return ResponseEntity.ok(activatedAccount);
	}

	/**
	 * Deactivates an account by its ID.
	 *
	 * @param accountId the ID of the account to deactivate.
	 * @return a {@code ResponseEntity<AccountDTO>} containing the deactivated account.
	 * @throws Exception if an error occurs while deactivating the account.
	 */
	@Operation(summary = "Deactivate an account by its id", description = "Returns the deactivated" +
			" account as AccountDTO")
	@PatchMapping("/deactivate/{accountId}")
	public ResponseEntity<AccountDTO> deactivateAccount(@PathVariable Integer accountId)
			throws Exception {

		AccountDTO deactivatedAccount = accountService.deactivateAccount(accountId);

		return ResponseEntity.ok(deactivatedAccount);
	}

	/**
	 * Retrieves all accounts associated with a specific customer ID.
	 *
	 * @param customerId the ID of the customer whose accounts are to be retrieved.
	 * @return a {@code ResponseEntity<List<AccountDTO>>} containing a list of accounts.
	 * @throws Exception if an error occurs while retrieving the accounts.
	 */
	@Operation(summary = "Retrieve accounts by customer id", description = "Returns a list of " +
			"AccountDTO")
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<AccountDTO>> getAccountsByCustomerId(
			@PathVariable Integer customerId) throws
			Exception {

		List<AccountDTO> accounts = accountService.getAccountsByCustomerId(customerId);

		return ResponseEntity.ok(accounts);
	}

	/**
	 * Deletes an account by its ID.
	 *
	 * @param accountId the ID of the account to delete.
	 * @return {@code ResponseEntity<Void>} an empty response after deleting the account.
	 * @throws Exception if an error occurs while deleting the account.
	 */
	@Operation(summary = "Deletes an account", description = "Returns the account deleted as " +
			"AccountDTO")
	@DeleteMapping("/{accountId}")
	public ResponseEntity<AccountDTO> deleteAccountById(@PathVariable Integer accountId) throws
			Exception {

		AccountDTO deletedAccount = accountService.deleteAccountById(accountId);

		return ResponseEntity.ok(deletedAccount);
	}

	/**
	 * Retrieves the balance of an account by its ID.
	 *
	 * @param accountId the ID of the account whose balance is to be retrieved.
	 * @return a {@code ResponseEntity<Double>} containing the account balance.
	 * @throws Exception if an error occurs while retrieving the balance.
	 */
	@Operation(summary = "Retrieve account balance by its id", description = "Returns a double")
	@GetMapping("balance/{accountId}")
	public ResponseEntity<Double> getBalanceByAccountId(@PathVariable Integer accountId)
			throws Exception {

		Double balance = accountService.getBalanceByAccountId(accountId);

		return ResponseEntity.ok(balance);
	}

	/**
	 * Checks if an account is active by its ID.
	 *
	 * @param accountId the ID of the account to check.
	 * @return a {@code ResponseEntity<Boolean>} indicating whether the account is active.
	 * @throws Exception if an error occurs while checking the account status.
	 */
	@Operation(summary = "Check if an account is active by its id", description = "Returns " +
			"boolean")
	@GetMapping("/active/{accountId}")
	public ResponseEntity<Boolean> accountIsActive(@PathVariable Integer accountId)
			throws Exception {

		Boolean status = accountService.accountIsActive(accountId);

		return ResponseEntity.ok(status);
	}

	@PatchMapping("/update-balance/{accountId}")
	public ResponseEntity<String> updateBalance(@PathVariable Integer accountId,
												@RequestParam Double amount) {
		accountService.updateBalanceByAccountId(accountId, amount);
		return ResponseEntity.ok("Balance updated successfully");
	}

	/**
	 * Checks if active accounts exist for a given customer ID.
	 *
	 * @param customerId the ID of the customer
	 * @return true if active account exist, false otherwise
	 * @throws Exception if an error occurs while checking for active accounts
	 */
	@Operation(summary = "Verify if a customer has active accounts by its id", description =
			"Returns boolean")
	@GetMapping("/active/{customerId}")
	public boolean getAccountByCustomerId(@PathVariable Integer customerId) throws Exception {
		return accountService.activeAccountExists(customerId);
	}


	/* todo: remove methods below if not necessary */
	/**
	 * Updates the balance of an account by its account number.
	 *
	 * @param accountNumber the account number of the account to update the balance for.
	 * @param amount        the amount to update the balance by.
	 * @return a ResponseEntity indicating the success of the operation.
	 */
	@Operation(summary = "Updates the account balance by its account number", description =
			"Returns a String of " +
					"completion")
	@PatchMapping("/update/{accountNumber}")
	public ResponseEntity<String> updateBalanceByAccountNumber(@PathVariable String accountNumber,
															   @RequestParam Double amount) {
		accountService.updateBalanceByAccountNumber(accountNumber, amount);
		return ResponseEntity.ok("Balance updated successfully");
	}

	/**
	 * Deposits an amount into the account with the given ID.
	 *
	 * @param accountId             the ID of the account to deposit into.
	 * @param transactionRequestDTO the data transfer object containing the deposit amount.
	 * @return a ResponseEntity containing the updated AccountDTO object.
	 */
	@Operation(summary = "Deposit into an account", description = "Returns the account updated as " +
			"AccountDTO")
	@PutMapping("/deposit/{accountId}")
	public ResponseEntity<AccountDTO> deposit(@PathVariable Integer accountId,
											  @RequestBody TransactionRequestDTO transactionRequestDTO) {
		AccountDTO updatedAccount =
				accountService.deposit(accountId, transactionRequestDTO.amount());
		return ResponseEntity.ok(updatedAccount);
	}

	/**
	 * Withdraws an amount from the account with the given ID.
	 *
	 * @param accountId             the ID of the account to withdraw from.
	 * @param transactionRequestDTO the data transfer object containing the withdrawal amount.
	 * @return a ResponseEntity containing the updated AccountDTO object.
	 */
	@Operation(summary = "Withdraw into an account", description = "Returns the account updated as" +
			" AccountDTO")
	@PutMapping("/withdraw/{accountId}")
	public ResponseEntity<AccountDTO> withdraw(@PathVariable Integer accountId,
											   @RequestBody TransactionRequestDTO transactionRequestDTO) {
		AccountDTO updatedAccount =
				accountService.withdraw(accountId, transactionRequestDTO.amount());
		return ResponseEntity.ok(updatedAccount);
	}

	/**
	 * Retrieves the balance of an account by its account number.
	 *
	 * @param accountNumber the account number of the account to retrieve the balance for.
	 * @return a ResponseEntity containing the account balance.
	 */
	@Operation(summary = "Obtains the account balance by its account number", description =
			"Returns a double")
	@GetMapping("/balance/{accountNumber}")
	public ResponseEntity<Double> getAccountBalance(@PathVariable String accountNumber) {
		Double balance = accountService.getAccountBalance(accountNumber);
		return ResponseEntity.ok(balance);
	}

	/**
	 * Verifies if an account exists by its account number.
	 *
	 * @param accountNumber the account number to verify.
	 * @return a ResponseEntity containing a boolean value indicating if the account exists.
	 */
	@Operation(summary = "Verify if an account exists by its account number", description =
			"Returns boolean")
	@GetMapping("/verify/{accountNumber}")
	public ResponseEntity<Boolean> verifyAccountByAccountNumber(
			@PathVariable String accountNumber) {
		boolean exists = accountService.accountExistsByAccountNumber(accountNumber);
		return ResponseEntity.ok(exists);
	}

}
