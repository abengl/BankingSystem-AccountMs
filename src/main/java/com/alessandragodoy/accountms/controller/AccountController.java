package com.alessandragodoy.accountms.controller;

import com.alessandragodoy.accountms.api.AccountApi;
import com.alessandragodoy.accountms.dto.AccountDTO;
import com.alessandragodoy.accountms.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.service.IAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.alessandragodoy.accountms.utility.DTOMapper.convertToDTO;

/**
 * Controller for managing accounts.
 */
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {

	private final IAccountService accountService;

	/**
	 * Activates an account by its ID.
	 *
	 * @param accountId the ID of the account to activate.
	 * @return a {@code ResponseEntity<AccountDTO>} containing the activated account.
	 */
	@Override
	public ResponseEntity<AccountDTO> activateAccount(@PathVariable Integer accountId) {

		Account activatedAccount = accountService.activateAccount(accountId);

		return ResponseEntity.ok(convertToDTO(activatedAccount, AccountDTO.class));
	}

	/**
	 * Creates a new account.
	 *
	 * @param createAccountDTO the data transfer object containing the account details.
	 * @return {@code ResponseEntity<AccountDTO>} containing the created account.
	 */
	@Override
	public ResponseEntity<AccountDTO> createAccount(
			@Valid @RequestBody CreateAccountDTO createAccountDTO) {

		Account account =
				accountService.createAccount(createAccountDTO);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(convertToDTO(account, AccountDTO.class));
	}

	/**
	 * Deactivates an account by its ID.
	 *
	 * @param accountId the ID of the account to deactivate.
	 * @return a {@code ResponseEntity<AccountDTO>} containing the deactivated account.
	 */
	@Override
	public ResponseEntity<AccountDTO> deactivateAccount(@PathVariable Integer accountId) {

		Account deactivatedAccount = accountService.deactivateAccount(accountId);

		return ResponseEntity.ok(convertToDTO(deactivatedAccount, AccountDTO.class));
	}

	/**
	 * Retrieves an account by its ID.
	 *
	 * @param accountId the ID of the account to retrieve.
	 * @return a {@code ResponseEntity<AccountDTO>} containing the account.
	 */
	@Override
	public ResponseEntity<AccountDTO> getAccountById(@PathVariable Integer accountId) {

		Account account = accountService.getAccountById(accountId);

		return ResponseEntity.ok(convertToDTO(account, AccountDTO.class));
	}

	/**
	 * Retrieves all accounts associated with a specific customer ID.
	 *
	 * @param customerId the ID of the customer whose accounts are to be retrieved.
	 * @return a {@code ResponseEntity<List<AccountDTO>>} containing a list of accounts.
	 */
	@Override
	public ResponseEntity<List<AccountDTO>> getAccountsByCustomerId(
			@PathVariable Integer customerId) {

		List<AccountDTO> accounts = accountService.getAccountsByCustomerId(customerId)
				.stream()
				.map(account -> convertToDTO(account, AccountDTO.class)).toList();

		return ResponseEntity.ok(accounts);
	}

	/**
	 * Retrieves all active accounts.
	 *
	 * @return {@code ResponseEntity<List<AccountDTO>>} containing a list of accounts.
	 */
	@Override
	public ResponseEntity<List<AccountDTO>> getAllAccounts() {

		List<AccountDTO> accounts = accountService.getAllActiveAccounts()
				.stream()
				.map(account -> convertToDTO(account, AccountDTO.class)).toList();

		return ResponseEntity.ok(accounts);
	}

	/*@Override
	public ResponseEntity<AccountDTO> deleteAccountById(@PathVariable Integer accountId) {

		accountService.deleteAccountById(accountId);

		return ResponseEntity.noContent().build();
	}*/

}
