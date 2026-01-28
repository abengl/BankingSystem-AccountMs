package com.alessandragodoy.accountms.service.impl;

import com.alessandragodoy.accountms.adapter.CustomerServiceClient;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.controller.dto.CustomerValidationResponseDTO;
import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.model.AccountType;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.IAccountService;
import com.alessandragodoy.accountms.utility.AccountNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the AccountService interface.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {

	private final AccountRepository accountRepository;
	private final CustomerServiceClient customerServiceClient;

	@Override
	public List<Account> getAllActiveAccounts() {

		return accountRepository.findAllByActiveTrue();
	}

	@Override
	public Account getAccountById(Integer accountId) {

		return accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException(
						"The account with ID " + accountId + " does not exist."));
	}

	@Override
	public Account createAccount(CreateAccountDTO createAccountDTO) {

		CustomerValidationResponseDTO response =
				customerServiceClient.validateCustomer(createAccountDTO.getCustomerId());

		if (!response.getExists() || !response.getIsActive()) {
			throw new AccountValidationException(response.getMessage());
		}

		return accountRepository.save(new Account(null,
				AccountNumberGenerator.generateAccountNumber(),
				createAccountDTO.getBalance(),
				AccountType.valueOf(createAccountDTO.getAccountType()),
				createAccountDTO.getCustomerId(),
				null,
				null,
				true));

	}

	@Transactional
	@Override
	public Account activateAccount(Integer accountId) {

		Account activatedAccount = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException(
						"Account not found for ID: " + accountId));

		activatedAccount.setActive(true);

		return accountRepository.save(activatedAccount);
	}

	@Transactional
	@Override
	public Account deactivateAccount(Integer accountId) {

		Account deactivatedAccount = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException(
						"Account not found for ID: " + accountId));

		if (deactivatedAccount.getBalance() > 0) {
			throw new AccountValidationException("Account with ID: " + accountId +
					" cannot be deactivated because it has a positive balance.");
		}

		deactivatedAccount.setActive(false);

		return accountRepository.save(deactivatedAccount);
	}

	@Transactional
	@Override
	public void deleteAccountById(Integer accountId) {

		Account deletedAccount = accountRepository.findById(accountId).orElseThrow(
				() -> new AccountNotFoundException(
						"Delete stopped. Account not found for ID: " + accountId));

		if (deletedAccount.getBalance() > 0) {
			throw new AccountValidationException("Account with ID: " + accountId +
					" cannot be deleted because it has a positive balance.");
		}

		accountRepository.delete(deletedAccount);
	}

	@Override
	public List<Account> getAccountsByCustomerId(Integer customerId) {
		Optional<List<Account>> accounts = accountRepository.findAllByCustomerId(customerId);

		if (accounts.isEmpty() || accounts.get().isEmpty()) {
			throw new AccountNotFoundException("No accounts found with customer ID: " + customerId);
		}

		return accounts.get();
	}

}
