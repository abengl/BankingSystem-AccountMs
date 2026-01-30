package com.alessandragodoy.accountms.service;

import com.alessandragodoy.accountms.adapter.CustomerServiceClient;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.controller.dto.CustomerValidationResponseDTO;
import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.model.AccountType;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.impl.AccountServiceImpl;
import com.alessandragodoy.accountms.utility.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountServiceImpl.
 * Tests business logic, repository interactions, and external service communications.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private CustomerServiceClient customerServiceClient;

	@InjectMocks
	private AccountServiceImpl accountService;

	private Account testAccount;
	private Account testAccount2;
	private Account inactiveAccount;

	@BeforeEach
	void setUp() {
		testAccount = createAccount(1, "A000001", 1000.0, AccountType.SAVINGS, 1, true);
		testAccount2 = createAccount(2, "A000002", 500.0, AccountType.CHECKING, 1, true);
		inactiveAccount = createAccount(3, "A000003", 0.0, AccountType.SAVINGS, 2, false);
	}

	@Test
	@DisplayName("getAllActiveAccounts - returns list of active accounts")
	void getAllActiveAccounts_WithActiveAccounts_ReturnsAccountList() {

		List<Account> activeAccounts = Arrays.asList(testAccount, testAccount2);
		when(accountRepository.findAllByActiveTrue()).thenReturn(activeAccounts);

		List<Account> result = accountService.getAllActiveAccounts();

		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(testAccount, testAccount2);
		assertThat(result).allMatch(Account::isActive);

		verify(accountRepository).findAllByActiveTrue();
	}

	@Test
	@DisplayName("getAllActiveAccounts - returns empty list when no active accounts")
	void getAllActiveAccounts_WithNoActiveAccounts_ReturnsEmptyList() {

		when(accountRepository.findAllByActiveTrue()).thenReturn(Collections.emptyList());

		List<Account> result = accountService.getAllActiveAccounts();

		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(accountRepository).findAllByActiveTrue();
	}

	@Test
	@DisplayName("getAccountById - returns account when found")
	void getAccountById_WithExistingId_ReturnsAccount() {

		Integer accountId = 1;
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

		Account result = accountService.getAccountById(accountId);

		assertThat(result).isNotNull();
		assertThat(result.getAccountId()).isEqualTo(accountId);
		assertThat(result.getAccountNumber()).isEqualTo("A000001");
		assertThat(result.getBalance()).isEqualTo(1000.0);
		assertThat(result.getAccountType()).isEqualTo(AccountType.SAVINGS);

		verify(accountRepository).findById(accountId);
	}

	@Test
	@DisplayName("getAccountById - throws AccountNotFoundException when not found")
	void getAccountById_WithNonExistentId_ThrowsAccountNotFoundException() {

		Integer nonExistentId = 999;
		when(accountRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> accountService.getAccountById(nonExistentId))
				.isInstanceOf(AccountNotFoundException.class)
				.hasMessage("The account with ID " + nonExistentId + " does not exist.");

		verify(accountRepository).findById(nonExistentId);
	}

	@Test
	@DisplayName("createAccount - creates SAVINGS account successfully")
	void createAccount_WithValidSavingsData_ReturnsCreatedAccount() {

		CreateAccountDTO createDTO = createAccountDTO(1000.0, "SAVINGS", 1);
		Account savedAccount = createAccount(4, "A000004", 1000.0, AccountType.SAVINGS, 1, true);

		CustomerValidationResponseDTO validCustomer = createValidCustomerResponse();

		when(customerServiceClient.validateCustomer(1)).thenReturn(validCustomer);
		when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

		try (MockedStatic<AccountNumberGenerator> mockedGenerator =
					 mockStatic(AccountNumberGenerator.class)) {
			mockedGenerator.when(AccountNumberGenerator::generateAccountNumber)
					.thenReturn("A000004");

			Account result = accountService.createAccount(createDTO);

			assertThat(result).isNotNull();
			assertThat(result.getAccountId()).isEqualTo(4);
			assertThat(result.getAccountNumber()).isEqualTo("A000004");
			assertThat(result.getBalance()).isEqualTo(1000.0);
			assertThat(result.getAccountType()).isEqualTo(AccountType.SAVINGS);
			assertThat(result.getCustomerId()).isEqualTo(1);
			assertThat(result.isActive()).isTrue();

			verify(customerServiceClient).validateCustomer(1);
			verify(accountRepository).save(any(Account.class));
			mockedGenerator.verify(AccountNumberGenerator::generateAccountNumber);
		}
	}

	@Test
	@DisplayName("createAccount - creates CHECKING account successfully")
	void createAccount_WithValidCheckingData_ReturnsCreatedAccount() {

		CreateAccountDTO createDTO = createAccountDTO(500.0, "CHECKING", 1);
		Account savedAccount = createAccount(5, "A000005", 500.0, AccountType.CHECKING, 1, true);

		CustomerValidationResponseDTO validCustomer = createValidCustomerResponse();

		when(customerServiceClient.validateCustomer(1)).thenReturn(validCustomer);
		when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

		try (MockedStatic<AccountNumberGenerator> mockedGenerator =
					 mockStatic(AccountNumberGenerator.class)) {
			mockedGenerator.when(AccountNumberGenerator::generateAccountNumber)
					.thenReturn("A000005");

			Account result = accountService.createAccount(createDTO);

			assertThat(result.getAccountType()).isEqualTo(AccountType.CHECKING);
			assertThat(result.getBalance()).isEqualTo(500.0);

			verify(customerServiceClient).validateCustomer(1);
			verify(accountRepository).save(any(Account.class));
		}
	}

	@Test
	@DisplayName("createAccount - creates account with zero balance")
	void createAccount_WithZeroBalance_ReturnsCreatedAccount() {

		CreateAccountDTO createDTO = createAccountDTO(0.0, "SAVINGS", 1);
		Account savedAccount = createAccount(6, "A000006", 0.0, AccountType.SAVINGS, 1, true);

		CustomerValidationResponseDTO validCustomer = createValidCustomerResponse();

		when(customerServiceClient.validateCustomer(1)).thenReturn(validCustomer);
		when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

		try (MockedStatic<AccountNumberGenerator> mockedGenerator =
					 mockStatic(AccountNumberGenerator.class)) {
			mockedGenerator.when(AccountNumberGenerator::generateAccountNumber)
					.thenReturn("A000006");

			Account result = accountService.createAccount(createDTO);

			assertThat(result.getBalance()).isEqualTo(0.0);

			verify(customerServiceClient).validateCustomer(1);
			verify(accountRepository).save(any(Account.class));
		}
	}

	@Test
	@DisplayName("createAccount - throws exception when customer does not exist")
	void createAccount_WithNonExistentCustomer_ThrowsAccountValidationException() {

		CreateAccountDTO createDTO = createAccountDTO(1000.0, "SAVINGS", 999);
		CustomerValidationResponseDTO invalidCustomer =
				createInvalidCustomerResponse("Customer not found for ID: 999");

		when(customerServiceClient.validateCustomer(999)).thenReturn(invalidCustomer);

		assertThatThrownBy(() -> accountService.createAccount(createDTO))
				.isInstanceOf(AccountValidationException.class)
				.hasMessage("Customer not found for ID: 999");

		verify(customerServiceClient).validateCustomer(999);
		verify(accountRepository, never()).save(any(Account.class));
	}

	private CustomerValidationResponseDTO createInvalidCustomerResponse(String message) {
		return new CustomerValidationResponseDTO(false, false, message);
	}

	@Test
	@DisplayName("createAccount - throws exception when customer is inactive")
	void createAccount_WithInactiveCustomer_ThrowsAccountValidationException() {

		CreateAccountDTO createDTO = createAccountDTO(1000.0, "SAVINGS", 5);
		CustomerValidationResponseDTO inactiveCustomer =
				createInactiveCustomerResponse("Customer is not active for ID: 5");

		when(customerServiceClient.validateCustomer(5)).thenReturn(inactiveCustomer);

		assertThatThrownBy(() -> accountService.createAccount(createDTO))
				.isInstanceOf(AccountValidationException.class)
				.hasMessage("Customer is not active for ID: 5");

		verify(customerServiceClient).validateCustomer(5);
		verify(accountRepository, never()).save(any(Account.class));
	}

	private CustomerValidationResponseDTO createInactiveCustomerResponse(String message) {
		return new CustomerValidationResponseDTO(true, false, message);
	}

	@Test
	@DisplayName("createAccount - validates customer before creating account")
	void createAccount_ValidatesCustomerFirst() {

		CreateAccountDTO createDTO = createAccountDTO(1000.0, "SAVINGS", 1);
		CustomerValidationResponseDTO validCustomer = createValidCustomerResponse();
		Account savedAccount = createAccount(7, "A000007", 1000.0, AccountType.SAVINGS, 1, true);

		when(customerServiceClient.validateCustomer(1)).thenReturn(validCustomer);
		when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

		try (MockedStatic<AccountNumberGenerator> mockedGenerator =
					 mockStatic(AccountNumberGenerator.class)) {
			mockedGenerator.when(AccountNumberGenerator::generateAccountNumber)
					.thenReturn("A000007");

			accountService.createAccount(createDTO);

			var inOrder = inOrder(customerServiceClient, accountRepository);
			inOrder.verify(customerServiceClient).validateCustomer(1);
			inOrder.verify(accountRepository).save(any(Account.class));
		}
	}

	@Test
	@DisplayName("activateAccount - activates inactive account successfully")
	void activateAccount_WithInactiveAccount_ReturnsActivatedAccount() {

		Integer accountId = 3;
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(inactiveAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		Account result = accountService.activateAccount(accountId);

		assertThat(result).isNotNull();
		assertThat(result.isActive()).isTrue();
		assertThat(result.getAccountId()).isEqualTo(accountId);

		verify(accountRepository).findById(accountId);
		verify(accountRepository).save(inactiveAccount);
	}

	@Test
	@DisplayName("activateAccount - throws exception when account not found")
	void activateAccount_WithNonExistentId_ThrowsAccountNotFoundException() {

		Integer nonExistentId = 999;
		when(accountRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> accountService.activateAccount(nonExistentId))
				.isInstanceOf(AccountNotFoundException.class)
				.hasMessage("Account not found for ID: " + nonExistentId);

		verify(accountRepository).findById(nonExistentId);
		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("deactivateAccount - deactivates account with zero balance")
	void deactivateAccount_WithZeroBalance_ReturnsDeactivatedAccount() {

		Integer accountId = 3;
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(inactiveAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		Account result = accountService.deactivateAccount(accountId);

		assertThat(result).isNotNull();
		assertThat(result.isActive()).isFalse();
		assertThat(result.getBalance()).isEqualTo(0.0);

		verify(accountRepository).findById(accountId);
		verify(accountRepository).save(inactiveAccount);
	}

	@Test
	@DisplayName("deactivateAccount - throws exception when balance is positive")
	void deactivateAccount_WithPositiveBalance_ThrowsAccountValidationException() {

		Integer accountId = 1;
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

		assertThatThrownBy(() -> accountService.deactivateAccount(accountId))
				.isInstanceOf(AccountValidationException.class)
				.hasMessage("Account with ID: " + accountId +
						" cannot be deactivated because it has a positive balance.");

		verify(accountRepository).findById(accountId);
		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("deactivateAccount - throws exception when account not found")
	void deactivateAccount_WithNonExistentId_ThrowsAccountNotFoundException() {

		Integer nonExistentId = 999;
		when(accountRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> accountService.deactivateAccount(nonExistentId))
				.isInstanceOf(AccountNotFoundException.class)
				.hasMessage("Account not found for ID: " + nonExistentId);

		verify(accountRepository).findById(nonExistentId);
		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("deleteAccountById - deletes account with zero balance")
	void deleteAccountById_WithZeroBalance_DeletesSuccessfully() {

		Integer accountId = 3;
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(inactiveAccount));
		doNothing().when(accountRepository).delete(inactiveAccount);

		accountService.deleteAccountById(accountId);

		verify(accountRepository).findById(accountId);
		verify(accountRepository).delete(inactiveAccount);
	}

	@Test
	@DisplayName("deleteAccountById - throws exception when balance is positive")
	void deleteAccountById_WithPositiveBalance_ThrowsAccountValidationException() {

		Integer accountId = 1;
		when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));

		assertThatThrownBy(() -> accountService.deleteAccountById(accountId))
				.isInstanceOf(AccountValidationException.class)
				.hasMessage("Account with ID: " + accountId +
						" cannot be deleted because it has a positive balance.");

		verify(accountRepository).findById(accountId);
		verify(accountRepository, never()).delete(any(Account.class));
	}

	@Test
	@DisplayName("deleteAccountById - throws exception when account not found")
	void deleteAccountById_WithNonExistentId_ThrowsAccountNotFoundException() {

		Integer nonExistentId = 999;
		when(accountRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> accountService.deleteAccountById(nonExistentId))
				.isInstanceOf(AccountNotFoundException.class)
				.hasMessage("Delete stopped. Account not found for ID: " + nonExistentId);

		verify(accountRepository).findById(nonExistentId);
		verify(accountRepository, never()).delete(any(Account.class));
	}

	@Test
	@DisplayName("getAccountsByCustomerId - returns multiple accounts for customer")
	void getAccountsByCustomerId_WithMultipleAccounts_ReturnsAccountList() {

		Integer customerId = 1;
		List<Account> customerAccounts = Arrays.asList(testAccount, testAccount2);
		when(accountRepository.findAllByCustomerId(customerId))
				.thenReturn(Optional.of(customerAccounts));

		List<Account> result = accountService.getAccountsByCustomerId(customerId);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(testAccount, testAccount2);
		assertThat(result).allMatch(account -> account.getCustomerId().equals(customerId));

		verify(accountRepository).findAllByCustomerId(customerId);
	}

	@Test
	@DisplayName("getAccountsByCustomerId - returns single account for customer")
	void getAccountsByCustomerId_WithSingleAccount_ReturnsAccountList() {
		// Arrange
		Integer customerId = 2;
		List<Account> customerAccounts = Collections.singletonList(inactiveAccount);
		when(accountRepository.findAllByCustomerId(customerId))
				.thenReturn(Optional.of(customerAccounts));

		List<Account> result = accountService.getAccountsByCustomerId(customerId);

		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(inactiveAccount);

		verify(accountRepository).findAllByCustomerId(customerId);
	}

	@Test
	@DisplayName("getAccountsByCustomerId - throws exception when no accounts found")
	void getAccountsByCustomerId_WithNoAccounts_ThrowsAccountNotFoundException() {

		Integer customerId = 999;
		when(accountRepository.findAllByCustomerId(customerId))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> accountService.getAccountsByCustomerId(customerId))
				.isInstanceOf(AccountNotFoundException.class)
				.hasMessage("No accounts found with customer ID: " + customerId);

		verify(accountRepository).findAllByCustomerId(customerId);
	}

	@Test
	@DisplayName("getAccountsByCustomerId - throws exception when empty list returned")
	void getAccountsByCustomerId_WithEmptyList_ThrowsAccountNotFoundException() {

		Integer customerId = 888;
		when(accountRepository.findAllByCustomerId(customerId))
				.thenReturn(Optional.of(Collections.emptyList()));

		assertThatThrownBy(() -> accountService.getAccountsByCustomerId(customerId))
				.isInstanceOf(AccountNotFoundException.class)
				.hasMessage("No accounts found with customer ID: " + customerId);

		verify(accountRepository).findAllByCustomerId(customerId);
	}

	@Test
	@DisplayName("Integration - create, activate, deactivate workflow")
	void integration_AccountLifecycle_WorksCorrectly() {

		CreateAccountDTO createDTO = createAccountDTO(1000.0, "SAVINGS", 1);
		Account savedAccount = createAccount(12, "A000012", 1000.0, AccountType.SAVINGS, 1, true);
		CustomerValidationResponseDTO validCustomer = createValidCustomerResponse();

		when(customerServiceClient.validateCustomer(1)).thenReturn(validCustomer);
		when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
		when(accountRepository.findById(12)).thenReturn(Optional.of(savedAccount));

		try (MockedStatic<AccountNumberGenerator> mockedGenerator =
					 mockStatic(AccountNumberGenerator.class)) {
			mockedGenerator.when(AccountNumberGenerator::generateAccountNumber)
					.thenReturn("A000012");

			// Create
			Account created = accountService.createAccount(createDTO);
			assertThat(created.getAccountId()).isEqualTo(12);
			assertThat(created.isActive()).isTrue();

			// Activate (already active)
			Account activated = accountService.activateAccount(12);
			assertThat(activated.isActive()).isTrue();

			// Deactivate
			savedAccount.setBalance(0.0);
			when(accountRepository.findById(12)).thenReturn(Optional.of(savedAccount));

			Account deactivated = accountService.deactivateAccount(12);
			assertThat(deactivated.isActive()).isFalse();

			// Verify all interactions
			verify(customerServiceClient).validateCustomer(1);
			verify(accountRepository, atLeastOnce()).save(any(Account.class));
		}
	}

	@Test
	@DisplayName("Account Number Generation - generates unique account numbers")
	void createAccount_GeneratesUniqueAccountNumbers() {

		CreateAccountDTO createDTO1 = createAccountDTO(1000.0, "SAVINGS", 1);
		CreateAccountDTO createDTO2 = createAccountDTO(500.0, "CHECKING", 1);

		Account account1 = createAccount(13, "A000013", 1000.0, AccountType.SAVINGS, 1, true);
		Account account2 = createAccount(14, "A000014", 500.0, AccountType.CHECKING, 1, true);

		CustomerValidationResponseDTO validCustomer = createValidCustomerResponse();

		when(customerServiceClient.validateCustomer(1)).thenReturn(validCustomer);
		when(accountRepository.save(any(Account.class)))
				.thenReturn(account1)
				.thenReturn(account2);

		try (MockedStatic<AccountNumberGenerator> mockedGenerator =
					 mockStatic(AccountNumberGenerator.class)) {
			mockedGenerator.when(AccountNumberGenerator::generateAccountNumber)
					.thenReturn("A000013")
					.thenReturn("A000014");

			Account result1 = accountService.createAccount(createDTO1);
			Account result2 = accountService.createAccount(createDTO2);

			assertThat(result1.getAccountNumber()).isEqualTo("A000013");
			assertThat(result2.getAccountNumber()).isEqualTo("A000014");
			assertThat(result1.getAccountNumber()).isNotEqualTo(result2.getAccountNumber());

			mockedGenerator.verify(AccountNumberGenerator::generateAccountNumber, times(2));
		}
	}

	private Account createAccount(Integer id, String accountNumber, double balance,
								  AccountType type, Integer customerId, boolean active) {
		return Account.builder()
				.accountId(id)
				.accountNumber(accountNumber)
				.balance(balance)
				.accountType(type)
				.customerId(customerId)
				.creationDate(LocalDateTime.now())
				.updateDate(LocalDateTime.now())
				.active(active)
				.build();
	}

	private CreateAccountDTO createAccountDTO(Double balance, String accountType,
											  Integer customerId) {
		return new CreateAccountDTO(balance, accountType, customerId);
	}

	private CustomerValidationResponseDTO createValidCustomerResponse() {
		return new CustomerValidationResponseDTO(true, true, "");
	}
}