package com.alessandragodoy.accountms.service.impl;

import com.alessandragodoy.accountms.controller.dto.AccountDTO;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.AccountService;
import com.alessandragodoy.accountms.utility.AccountMapper;
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
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final AccountValidation accountValidation;
	private final AccountNumberGenerator numberGenerator;

	@Override
	public List<AccountDTO> getAllAccounts() {
		return accountRepository.findAll().stream().map(AccountMapper::toDTO).toList();
	}

	@Override
	public AccountDTO getAccountById(Integer accountId) {
		return accountRepository.findById(accountId).map(AccountMapper::toDTO)
				.orElseThrow(() -> new AccountNotFoundException("The account with ID " + accountId + " does not " +
						"exist."));
	}

	@Override
	public AccountDTO createAccount(CreateAccountDTO createAccountDTO) {
		accountValidation.validateAccountData(createAccountDTO);
		accountValidation.validateCustomerExists(createAccountDTO.customerId());

		Account newAccount = AccountMapper.toCreateEntity(createAccountDTO);
		newAccount.setAccountNumber(numberGenerator.generate());
		accountRepository.save(newAccount);

		return AccountMapper.toDTO(newAccount);
	}

	@Transactional
	@Override
	public AccountDTO deposit(Integer accountId, Double amount) {
		AccountValidation.validateAmount(amount);

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException(
						"Deposit can not continue. Account not found for ID: " + accountId));
		accountRepository.updateBalanceDeposit(accountId, amount);
		account.setBalance(accountRepository.getBalanceByAccountId(accountId));

		return AccountMapper.toDTO(account);
	}

	@Transactional
	@Override
	public AccountDTO withdraw(Integer accountId, Double amount) {
		AccountValidation.validateAmount(amount);

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException("Withdraw can not continue. Account not found for " +
						"ID:" +
						" " + accountId));
		Double currentBalance = account.getBalance();

		AccountValidation.validateSufficientFunds(account, currentBalance, amount);

		accountRepository.updateBalanceWithdraw(accountId, amount);
		account.setBalance(accountRepository.getBalanceByAccountId(accountId));

		return AccountMapper.toDTO(account);
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
