package com.alessandragodoy.accountms.service.impl;

import com.alessandragodoy.accountms.adapter.AccountAdapter;
import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.exception.CustomerNotFoundException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.IAccountService;
import com.alessandragodoy.accountms.utility.DTOMapper;
import com.alessandragodoy.accountms.utility.AccountNumberGenerator;
import com.alessandragodoy.accountms.utility.AccountValidation;
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
	private final AccountAdapter accountAdapter;
	private final AccountNumberGenerator numberGenerator;

	@Override
	public List<Account> getAllActiveAccounts() throws Exception {

		return accountRepository.findAllByActiveTrue();
	}

	@Override
	public Account getAccountById(Integer accountId) throws Exception {

		return accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException("The account with ID " + accountId + " does not exist."));
	}

	@Override
	public Account createAccount(Account account) throws Exception {

		if (!accountAdapter.customerExists(account.getCustomerId())) {
			throw new CustomerNotFoundException("Customer not found for ID: " + account.getCustomerId());
		}

		account.setAccountNumber(numberGenerator.generate());

		return accountRepository.save(account);

	}

	@Override
	public Account activateAccount(Integer accountId) throws Exception {

		Account activatedAccount = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException("Account not found for ID: " + accountId));

		activatedAccount.setActive(true);

		return accountRepository.save(activatedAccount);
	}

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException(
						"Deposit can not continue. Account not found for ID: " + accountId));
		accountRepository.updateBalanceDeposit(accountId, amount);
		account.setBalance(accountRepository.getBalanceByAccountId(accountId));

		return AccountMapper.toDTO(account);
	}

	@Override
	public boolean accountIsActiveByAccountId(Integer accountId) throws Exception {

		return accountRepository.existsByAccountIdAndActiveTrue(accountId);
	}

	@Override
	public AccountDTO deleteAccountById(Integer accountId) {
		return accountRepository.findById(accountId).map(existingAccount -> {
			accountRepository.delete(existingAccount);
			return AccountMapper.toDTO(existingAccount);
		}).orElseThrow(() -> new AccountNotFoundException("Delete stopped. Account not found for ID: " + accountId));
	}

	@Override
	public boolean activeAccountExists(Integer customerId) {
		return accountRepository.existsByCustomerIdAndActiveTrue(customerId);
	}

	@Override
	public Double getAccountBalance(String accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber)
				.map(Account::getBalance)
				.orElseThrow(() ->
						new AccountNotFoundException("Account " + accountNumber + " not found, can not get balance."));
	}

	@Override
	public boolean accountExistsByAccountNumber(String accountNumber) {
		return accountRepository.existsByAccountNumber(accountNumber);
	}

	@Override
	public void updateBalanceByAccountNumber(String accountNumber, Double amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountNotFoundException("Account not found for number: " + accountNumber +
						"can not update balance."));
		account.setBalance(account.getBalance() + amount);
		accountRepository.save(account);
	}

}
