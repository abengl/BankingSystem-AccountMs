package com.alessandragodoy.accountms.controller;

import com.alessandragodoy.accountms.controller.dto.AccountDTO;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.alessandragodoy.accountms.utility.DTOMapper.convertToDTO;
import static com.alessandragodoy.accountms.utility.DTOMapper.convertToEntity;

/**
 * Controller for managing accounts.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Controller for client-facing Account Operations")
public class AccountController {

	private final IAccountService accountService;

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

		List<AccountDTO> accounts = accountService.getAllActiveAccounts()
				.stream()
				.map(account -> convertToDTO(account, AccountDTO.class)).toList();

		return ResponseEntity.ok(accounts);
	}

	/**
	 * Retrieves an account by its ID.
	 *
	 * @param accountId the ID of the account to retrieve.
	 * @return a {@code ResponseEntity<AccountDTO>} containing the account.
	 * @throws Exception if an error occurs while retrieving the account.
	 */
	@Operation(summary = "Retrieve account by its id", description = "Returns the found account " +
			"as AccountDTO")
	@GetMapping("/{accountId}")
	public ResponseEntity<AccountDTO> getAccountById(@PathVariable Integer accountId)
			throws Exception {

		Account account = accountService.getAccountById(accountId);

		return ResponseEntity.ok(convertToDTO(account, AccountDTO.class));
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
	public ResponseEntity<AccountDTO> createAccount(
			@Valid @RequestBody CreateAccountDTO createAccountDTO)
			throws Exception {

		Account account =
				accountService.createAccount(convertToEntity(createAccountDTO, Account.class));

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(convertToDTO(account, AccountDTO.class));
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

		Account activatedAccount = accountService.activateAccount(accountId);

		return ResponseEntity.ok(convertToDTO(activatedAccount, AccountDTO.class));
	}

	/**
	 * Deactivates an account by its ID.
	 *
	 * @param accountId the ID of the account to deactivate.
	 * @return a {@code ResponseEntity<AccountDTO>} containing the deactivated account.
	 * @throws Exception if an error occurs while deactivating the account.
	 */
	@Operation(summary = "Deactivate an account by its id", description = "Returns the " +
			"deactivated account as AccountDTO")
	@PatchMapping("/deactivate/{accountId}")
	public ResponseEntity<AccountDTO> deactivateAccount(@PathVariable Integer accountId)
			throws Exception {

		Account deactivatedAccount = accountService.deactivateAccount(accountId);

		return ResponseEntity.ok(convertToDTO(deactivatedAccount, AccountDTO.class));
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

		List<AccountDTO> accounts = accountService.getAccountsByCustomerId(customerId)
				.stream()
				.map(account -> convertToDTO(account, AccountDTO.class)).toList();

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

		accountService.deleteAccountById(accountId);

		return ResponseEntity.noContent().build();
	}

}
