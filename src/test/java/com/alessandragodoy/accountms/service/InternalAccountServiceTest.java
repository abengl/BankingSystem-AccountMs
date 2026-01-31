package com.alessandragodoy.accountms.service;

import com.alessandragodoy.accountms.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.dto.TransferResponseDTO;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.model.AccountType;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.impl.InternalAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InternalAccountServiceImpl.
 * Tests transfer operations and account validation business logic.
 */
@ExtendWith(MockitoExtension.class)
class InternalAccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private InternalAccountServiceImpl internalAccountService;

	private Account sourceAccount;
	private Account destinationAccount;
	private Account inactiveAccount;
	private Account insufficientBalanceAccount;


	@BeforeEach
	void setUp() {
		sourceAccount = createAccount(1, "A000001", 1000.0, AccountType.SAVINGS, 1, true);
		destinationAccount = createAccount(2, "A000002", 500.0, AccountType.CHECKING, 2, true);
		inactiveAccount = createAccount(3, "A000003", 2000.0, AccountType.SAVINGS, 3, false);
		insufficientBalanceAccount =
				createAccount(4, "A000004", 50.0, AccountType.CHECKING, 4, true);
	}

	@Test
	@DisplayName("executeTransfer - successfully transfers funds between accounts")
	void executeTransfer_WithValidData_TransfersSuccessfully() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 300.0);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.findById(2)).thenReturn(Optional.of(destinationAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result).isNotNull();
		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getSourceAccountId()).isEqualTo(1);
		assertThat(result.getDestinationAccountId()).isEqualTo(2);
		assertThat(result.getFinalSourceBalance()).isEqualTo(700.0); // 1000 - 300
		assertThat(result.getFinalDestinationBalance()).isEqualTo(800.0); // 500 + 300
		assertThat(result.getErrorCode()).isNull();
		assertThat(result.getErrorMessage()).isNull();

		assertThat(sourceAccount.getBalance()).isEqualTo(700.0);
		assertThat(destinationAccount.getBalance()).isEqualTo(800.0);

		verify(accountRepository).save(sourceAccount);
		verify(accountRepository).save(destinationAccount);
		verify(accountRepository, times(2)).save(any(Account.class));
	}

	private TransferRequestDTO createTransferRequest(String transactionType, Integer sourceId,
													 Integer destId, Double amount) {
		return new TransferRequestDTO(transactionType, sourceId, destId, amount);
	}

	@Test
	@DisplayName("executeTransfer - successfully transfers with own account transfer type")
	void executeTransfer_WithOwnAccountType_TransfersSuccessfully() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 200.0);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.findById(2)).thenReturn(Optional.of(destinationAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getFinalSourceBalance()).isEqualTo(800.0);
		assertThat(result.getFinalDestinationBalance()).isEqualTo(700.0);
	}

	@Test
	@DisplayName("executeTransfer - successfully transfers exact balance amount")
	void executeTransfer_WithExactBalance_TransfersSuccessfully() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 1000.0);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.findById(2)).thenReturn(Optional.of(destinationAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getFinalSourceBalance()).isEqualTo(0.0);
		assertThat(result.getFinalDestinationBalance()).isEqualTo(1500.0);
	}

	@Test
	@DisplayName("executeTransfer - successfully transfers small amount")
	void executeTransfer_WithSmallAmount_TransfersSuccessfully() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 0.01);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.findById(2)).thenReturn(Optional.of(destinationAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getFinalSourceBalance()).isEqualTo(999.99);
		assertThat(result.getFinalDestinationBalance()).isEqualTo(500.01);
	}

	@Test
	@DisplayName("executeTransfer - fails when source account not found")
	void executeTransfer_WithNonExistentSourceAccount_ReturnsFailedResponse() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 999, 2, 100.0);

		when(accountRepository.findById(999)).thenReturn(Optional.empty());

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result).isNotNull();
		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getErrorCode()).isEqualTo("SOURCE_ACCOUNT_NOT_FOUND");
		assertThat(result.getErrorMessage()).isEqualTo("Source account not found for ID: 999");
		assertThat(result.getSourceAccountId()).isNull();
		assertThat(result.getDestinationAccountId()).isNull();
		assertThat(result.getFinalSourceBalance()).isNull();
		assertThat(result.getFinalDestinationBalance()).isNull();

		verify(accountRepository, never()).save(any(Account.class));
		verify(accountRepository, never()).findById(2); // Should fail before checking destination
	}

	@Test
	@DisplayName("executeTransfer - fails when source account is inactive")
	void executeTransfer_WithInactiveSourceAccount_ReturnsFailedResponse() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 3, 2, 100.0);

		when(accountRepository.findById(3)).thenReturn(Optional.of(inactiveAccount));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getErrorCode()).isEqualTo("SOURCE_ACCOUNT_INACTIVE");
		assertThat(result.getErrorMessage()).isEqualTo("Source account is not active for ID: 3");

		verify(accountRepository, never()).save(any(Account.class));
		verify(accountRepository, never()).findById(2);
	}

	@Test
	@DisplayName("executeTransfer - fails when source account has insufficient funds")
	void executeTransfer_WithInsufficientFunds_ReturnsFailedResponse() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 4, 2, 100.0);

		when(accountRepository.findById(4)).thenReturn(Optional.of(insufficientBalanceAccount));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getErrorCode()).isEqualTo("INSUFFICIENT_FUNDS");
		assertThat(result.getErrorMessage()).isEqualTo(
				"Insufficient balance in source account: 50.0");

		verify(accountRepository, never()).save(any(Account.class));
		verify(accountRepository, never()).findById(2);
	}

	@Test
	@DisplayName("executeTransfer - fails when amount exceeds balance by small margin")
	void executeTransfer_WithSlightlyInsufficientFunds_ReturnsFailedResponse() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 1000.01);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getErrorCode()).isEqualTo("INSUFFICIENT_FUNDS");
		assertThat(result.getErrorMessage()).contains("1000.0");

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("executeTransfer - fails when destination account not found")
	void executeTransfer_WithNonExistentDestinationAccount_ReturnsFailedResponse() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 999, 100.0);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.findById(999)).thenReturn(Optional.empty());

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getErrorCode()).isEqualTo("DESTINATION_ACCOUNT_NOT_FOUND");
		assertThat(result.getErrorMessage()).isEqualTo("Destination account not found for ID: " +
				"999");

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("executeTransfer - fails when destination account is inactive")
	void executeTransfer_WithInactiveDestinationAccount_ReturnsFailedResponse() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 3, 100.0);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.findById(3)).thenReturn(Optional.of(inactiveAccount));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isFalse();
		assertThat(result.getErrorCode()).isEqualTo("DESTINATION_ACCOUNT_INACTIVE");
		assertThat(result.getErrorMessage()).isEqualTo(
				"Destination account is not active for ID: 3");

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("executeTransfer - saves both accounts when successful")
	void executeTransfer_SavesBothAccounts() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 250.0);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.findById(2)).thenReturn(Optional.of(destinationAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		internalAccountService.executeTransfer(transferRequest);

		// Verify save was called for both accounts
		ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
		verify(accountRepository, times(2)).save(accountCaptor.capture());

		var savedAccounts = accountCaptor.getAllValues();
		assertThat(savedAccounts).hasSize(2);

		// Verify source account was saved with reduced balance
		Account savedSource = savedAccounts.stream()
				.filter(acc -> acc.getAccountId().equals(1))
				.findFirst()
				.orElseThrow();
		assertThat(savedSource.getBalance()).isEqualTo(750.0);

		// Verify destination account was saved with increased balance
		Account savedDest = savedAccounts.stream()
				.filter(acc -> acc.getAccountId().equals(2))
				.findFirst()
				.orElseThrow();
		assertThat(savedDest.getBalance()).isEqualTo(750.0);
	}

	@Test
	@DisplayName("executeTransfer - does not save accounts when validation fails")
	void executeTransfer_DoesNotSaveOnValidationFailure() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 999, 2, 100.0);

		when(accountRepository.findById(999)).thenReturn(Optional.empty());

		internalAccountService.executeTransfer(transferRequest);

		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	@DisplayName("accountIsActiveByCustomerId - returns true when customer has active accounts")
	void accountIsActiveByCustomerId_WithActiveAccounts_ReturnsTrue() {

		Integer customerId = 1;
		when(accountRepository.existsByCustomerIdAndActiveTrue(customerId)).thenReturn(true);

		boolean result = internalAccountService.accountIsActiveByCustomerId(customerId);

		assertThat(result).isTrue();
		verify(accountRepository).existsByCustomerIdAndActiveTrue(customerId);
	}

	@Test
	@DisplayName("accountIsActiveByCustomerId - returns false when customer has no active " +
			"accounts")
	void accountIsActiveByCustomerId_WithNoActiveAccounts_ReturnsFalse() {

		Integer customerId = 2;
		when(accountRepository.existsByCustomerIdAndActiveTrue(customerId)).thenReturn(false);

		boolean result = internalAccountService.accountIsActiveByCustomerId(customerId);

		assertThat(result).isFalse();
		verify(accountRepository).existsByCustomerIdAndActiveTrue(customerId);
	}

	@Test
	@DisplayName("accountIsActiveByCustomerId - returns false for non-existent customer")
	void accountIsActiveByCustomerId_WithNonExistentCustomer_ReturnsFalse() {

		Integer nonExistentCustomerId = 999;
		when(accountRepository.existsByCustomerIdAndActiveTrue(nonExistentCustomerId))
				.thenReturn(false);

		boolean result = internalAccountService.accountIsActiveByCustomerId(nonExistentCustomerId);

		assertThat(result).isFalse();
		verify(accountRepository).existsByCustomerIdAndActiveTrue(nonExistentCustomerId);
	}

	@Test
	@DisplayName("Business Rule - transfer between same customer's accounts works")
	void executeTransfer_BetweenSameCustomerAccounts_Succeeds() {
		// Arrange
		Account customerAccount1 = createAccount(10, "A000010", 1000.0,
				AccountType.SAVINGS, 100, true);
		Account customerAccount2 = createAccount(11, "A000011", 500.0,
				AccountType.CHECKING, 100, true); // Same customer ID

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 10, 11, 300.0);

		when(accountRepository.findById(10)).thenReturn(Optional.of(customerAccount1));
		when(accountRepository.findById(11)).thenReturn(Optional.of(customerAccount2));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getFinalSourceBalance()).isEqualTo(700.0);
		assertThat(result.getFinalDestinationBalance()).isEqualTo(800.0);
	}

	@Test
	@DisplayName("Business Rule - transfer between different account types works")
	void executeTransfer_BetweenDifferentAccountTypes_Succeeds() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 400.0);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount)); // SAVINGS
		when(accountRepository.findById(2)).thenReturn(Optional.of(destinationAccount)); //
		// CHECKING
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isTrue();
		assertThat(sourceAccount.getAccountType()).isEqualTo(AccountType.SAVINGS);
		assertThat(destinationAccount.getAccountType()).isEqualTo(AccountType.CHECKING);
	}

	@Test
	@DisplayName("Integration - multiple transfers reduce source balance correctly")
	void integration_MultipleTransfers_UpdatesBalancesCorrectly() {

		Account testAccount = createAccount(20, "A000020", 1000.0,
				AccountType.SAVINGS, 20, true);
		Account testDest = createAccount(21, "A000021", 0.0,
				AccountType.CHECKING, 21, true);

		when(accountRepository.findById(20)).thenReturn(Optional.of(testAccount));
		when(accountRepository.findById(21)).thenReturn(Optional.of(testDest));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		// Multiple transfers
		TransferRequestDTO transfer1 = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 20, 21, 300.0);
		TransferResponseDTO result1 = internalAccountService.executeTransfer(transfer1);

		TransferRequestDTO transfer2 = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 20, 21, 200.0);
		TransferResponseDTO result2 = internalAccountService.executeTransfer(transfer2);

		assertThat(result1.isSuccess()).isTrue();
		assertThat(result1.getFinalSourceBalance()).isEqualTo(700.0);

		assertThat(result2.isSuccess()).isTrue();
		assertThat(result2.getFinalSourceBalance()).isEqualTo(500.0);
		assertThat(result2.getFinalDestinationBalance()).isEqualTo(500.0);

		verify(accountRepository, times(4)).save(any(Account.class));
	}

	@Test
	@DisplayName("Edge Case - transfer of very large amount")
	void executeTransfer_WithLargeAmount_Succeeds() {

		Account richAccount = createAccount(30, "A000030", 999999.99,
				AccountType.SAVINGS, 30, true);

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 30, 2, 999999.99);

		when(accountRepository.findById(30)).thenReturn(Optional.of(richAccount));
		when(accountRepository.findById(2)).thenReturn(Optional.of(destinationAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getFinalSourceBalance()).isEqualTo(0.0);
		assertThat(result.getFinalDestinationBalance()).isEqualTo(1000499.99);
	}

	@Test
	@DisplayName("Edge Case - precision is maintained for decimal amounts")
	void executeTransfer_MaintainsPrecision() {

		TransferRequestDTO transferRequest = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 123.45);

		when(accountRepository.findById(1)).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.findById(2)).thenReturn(Optional.of(destinationAccount));
		when(accountRepository.save(any(Account.class))).thenAnswer(
				invocation -> invocation.getArgument(0));

		TransferResponseDTO result = internalAccountService.executeTransfer(transferRequest);

		assertThat(result.isSuccess()).isTrue();
		assertThat(result.getFinalSourceBalance()).isEqualTo(876.55);
		assertThat(result.getFinalDestinationBalance()).isEqualTo(623.45);
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
}