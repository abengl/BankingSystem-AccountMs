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

	@Override
	public Account deactivateAccount(Integer accountId) throws Exception {

		Account deactivatedAccount = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException("Account not found for ID: " + accountId));

		if (deactivatedAccount.getBalance() > 0) {
			throw new AccountValidationException("Account with ID: " + accountId +
					" cannot be deactivated because it has a positive balance.");
		}

		deactivatedAccount.setActive(false);

		return accountRepository.save(deactivatedAccount);
	}

	@Override
	public void deleteAccountById(Integer accountId) throws Exception {

		Account deletedAccount = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Delete stopped. Account not found for ID: " + accountId));

		if (deletedAccount.getBalance() > 0) {
			throw new AccountValidationException("Account with ID: " + accountId +
					" cannot be deleted because it has a positive balance.");
		}

		accountRepository.delete(deletedAccount);
	}

	@Override
	public List<Account> getAccountsByCustomerId(Integer customerId) throws Exception {

		return accountRepository.findAllByCustomerId(customerId)
				.orElseThrow(() -> new AccountNotFoundException(
						"No accounts found with ID " + customerId));
	}

	@Override
	public double getBalanceByAccountId(Integer accountId) throws Exception {

		return accountRepository.findById(accountId)
				.map(Account::getBalance)
				.orElseThrow(() ->
						new AccountNotFoundException("Account " + accountId + " not found, can not get balance."));
	}

	@Override
	public boolean accountIsActiveByAccountId(Integer accountId) throws Exception {

		return accountRepository.existsByAccountIdAndActiveTrue(accountId);
	}

	@Override
	public void updateBalanceByAccountId(Integer accountId, Double amount) throws Exception {
		/*
		Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found for ID: " + accountId));

    if (operation == BalanceOperation.DEPOSIT) {
        account.setBalance(account.getBalance() + amount);
    } else if (operation == BalanceOperation.WITHDRAWAL) {
        if (account.getBalance() < amount) {
            throw new AccountValidationException("Insufficient funds for withdrawal.");
        }
        account.setBalance(account.getBalance() - amount);
    } else {
        throw new IllegalArgumentException("Invalid balance operation.");
    }

    accountRepository.save(account);
		 */
		return;
	}

	@Override
	public boolean accountIsActiveByCustomerId(Integer customerId) {
		return accountRepository.existsByCustomerIdAndActiveTrue(customerId);
	}

	@Override
	public double getAccountBalance(String accountNumber) {

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

	@Transactional
	@Override
	public Account deposit(Integer accountId, Double amount) {
		AccountValidation.validateAmount(amount);

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException(
						"Deposit can not continue. Account not found for ID: " + accountId));
		accountRepository.updateBalanceDeposit(accountId, amount);
		account.setBalance(accountRepository.getBalanceByAccountId(accountId));

		return account;
	}

	@Transactional
	@Override
	public Account withdraw(Integer accountId, Double amount) {
		AccountValidation.validateAmount(amount);

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException("Withdraw can not continue. Account not found for " +
						"ID:" +
						" " + accountId));
		Double currentBalance = account.getBalance();

		AccountValidation.validateSufficientFunds(account, currentBalance, amount);

		accountRepository.updateBalanceWithdraw(accountId, amount);
		account.setBalance(accountRepository.getBalanceByAccountId(accountId));

		return account;
	}

}
