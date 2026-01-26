package com.alessandragodoy.accountms;

import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.model.AccountType;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceMicroserviceTests {
	public List<Account> accounts = new ArrayList<>();
	@InjectMocks
	private AccountServiceImpl accountService;
	@Mock
	private AccountRepository accountRepository;

	@BeforeEach
	public void setUp() {
		accounts.add(Account.builder()
				.accountId(1).accountNumber("A000000001").balance(1000.0)
				.accountType(AccountType.SAVINGS).customerId(1).build());
		accounts.add(Account.builder()
				.accountId(2).accountNumber("A000000002").balance(1000.0)
				.accountType(AccountType.SAVINGS).customerId(2).build());
		accounts.add(Account.builder()
				.accountId(3).accountNumber("A000000003").balance(1000.0)
				.accountType(AccountType.CHECKING).customerId(2).build());
	}

	@Test
	@DisplayName("Test accountExists method - it returns boolean")
	void AccountService_accountExists_ReturnsBoolean() {
		// Arrange
		int customerId = 1;
		when(accountRepository.existsByCustomerIdAndActiveTrue(customerId)).thenReturn(true);

		// Act
		boolean response = accountService.activeAccountExists(customerId);

		// Assert
		assertTrue(response);
	}

	@Test
	@DisplayName("Test getAccountBalance method - it returns double")
	void AccountService_getAccountBalance_ReturnsDouble() {
		// Arrange
		Account account = accounts.get(0);
		when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));

		// Act
		Double response = accountService.getAccountBalance(account.getAccountNumber());

		// Assert
		assertEquals(account.getBalance(), response);
	}

	@Test
	@DisplayName("Test getAccountBalance method - it returns Exception when account is not found")
	void AccountService_getAccountBalance_ReturnsException() {
		// Arrange
		Account account = accounts.get(0);
		when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.empty());

		// Act & Assert
		AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
				() -> accountService.getAccountBalance(account.getAccountNumber()));
		assertEquals("Account " + account.getAccountNumber() + " not found, can not get balance.",
				exception.getMessage());
	}

	@Test
	@DisplayName("Test accountExistsByAccountNumber method - it returns boolean")
	void AccountService_accountExistsByAccountNumber_ReturnsBoolean() {
		// Arrange
		String accountNumber = "A000000001";
		when(accountRepository.existsByAccountNumber(accountNumber)).thenReturn(true);

		// Act
		boolean response = accountService.accountExistsByAccountNumber(accountNumber);

		// Assert
		assertTrue(response);
	}

	@Test
	@DisplayName("Test updateBalanceByAccountNumber method - it returns boolean")
	void AccountService_updateBalanceByAccountNumber_ReturnsBoolean() {
		// Arrange
		String accountNumber = "A000000001";
		Double amount = 500.0;
		Account account = accounts.get(0);
		when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
		when(accountRepository.save(account)).thenReturn(account);

		// Act
		accountService.updateBalanceByAccountNumber(accountNumber, amount);

		// Assert
		verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
		verify(accountRepository, times(1)).save(account);
	}

	@Test
	@DisplayName("Test updateBalanceByAccountNumber method - it returns Exception when account is not found")
	void AccountService_updateBalanceByAccountNumber_ReturnsException() {
		// Arrange
		Account account = accounts.get(0);
		when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.empty());

		// Act & Assert
		AccountNotFoundException exception = assertThrows(AccountNotFoundException.class,
				() -> accountService.getAccountBalance(account.getAccountNumber()));
		assertEquals("Account " + account.getAccountNumber() + " not found, can not get balance.",
				exception.getMessage());
	}

}
