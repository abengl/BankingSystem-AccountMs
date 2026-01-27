package com.alessandragodoy.accountms.service.impl;

import com.alessandragodoy.accountms.adapter.CustomerServiceClient;
import com.alessandragodoy.accountms.controller.dto.CustomerValidationResponseDTO;
import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.exception.CustomerNotFoundException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.IAccountService;
import com.alessandragodoy.accountms.utility.AccountNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
	public Account createAccount(Account account) {

		CustomerValidationResponseDTO response =
				customerServiceClient.validateCustomer(account.getCustomerId());

		if (!response.isExists()) {
			throw new CustomerNotFoundException(response.getMessage());
		}
		if (!response.isActive()) {
			throw new AccountValidationException(response.getMessage());
		}

		account.setAccountNumber(AccountNumberGenerator.generateAccountNumber());

		return accountRepository.save(account);

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

		return accountRepository.findAllByCustomerId(customerId)
				.orElseThrow(() -> new AccountNotFoundException(
						"No accounts found with ID " + customerId));
	}

}
