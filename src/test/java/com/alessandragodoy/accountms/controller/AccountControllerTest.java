package com.alessandragodoy.accountms.controller;

import com.alessandragodoy.accountms.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.AccountValidationException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.model.AccountType;
import com.alessandragodoy.accountms.service.IAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AccountController.
 * Tests REST endpoints for account management operations.
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private IAccountService accountService;

	@Test
	@DisplayName("GET /api/v1/accounts - returns list of active accounts")
	void getAllAccounts_WithActiveAccounts_ReturnsAccountDTOList() throws Exception {

		List<Account> accounts = Arrays.asList(
				createAccount(1, "A000001", 1000.0, AccountType.SAVINGS, 1, true),
				createAccount(2, "A000002", 500.0, AccountType.CHECKING, 1, true),
				createAccount(3, "A000003", 2000.0, AccountType.SAVINGS, 2, true)
		);
		when(accountService.getAllActiveAccounts()).thenReturn(accounts);

		mockMvc.perform(get("/api/v1/accounts")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[0].accountNumber").value("A000001"))
				.andExpect(jsonPath("$[0].balance").value(1000.0))
				.andExpect(jsonPath("$[0].accountType").value("SAVINGS"))
				.andExpect(jsonPath("$[1].accountNumber").value("A000002"))
				.andExpect(jsonPath("$[2].balance").value(2000.0))
				.andDo(print());

		verify(accountService).getAllActiveAccounts();
	}

	@Test
	@DisplayName("GET /api/v1/accounts - returns empty list when no active accounts")
	void getAllAccounts_WithNoActiveAccounts_ReturnsEmptyList() throws Exception {

		when(accountService.getAllActiveAccounts()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/v1/accounts")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(0))
				.andDo(print());

		verify(accountService).getAllActiveAccounts();
	}

	@Test
	@DisplayName("GET /api/v1/accounts/{accountId} - returns account when found")
	void getAccountById_WithValidId_ReturnsAccountDTO() throws Exception {

		Integer accountId = 1;
		Account account = createAccount(accountId, "A000001", 1500.0,
				AccountType.SAVINGS, 1, true);
		when(accountService.getAccountById(accountId)).thenReturn(account);

		mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.accountId").value(accountId))
				.andExpect(jsonPath("$.accountNumber").value("A000001"))
				.andExpect(jsonPath("$.balance").value(1500.0))
				.andExpect(jsonPath("$.accountType").value("SAVINGS"))
				.andExpect(jsonPath("$.customerId").value(1))
				.andDo(print());

		verify(accountService).getAccountById(accountId);
	}

	@Test
	@DisplayName("GET /api/v1/accounts/{accountId} - returns 404 when account not found")
	void getAccountById_WithNonExistentId_ReturnsNotFound() throws Exception {

		Integer nonExistentId = 999;
		when(accountService.getAccountById(nonExistentId))
				.thenThrow(new AccountNotFoundException(
						"The account with ID " + nonExistentId + " does not exist."));

		mockMvc.perform(get("/api/v1/accounts/{accountId}", nonExistentId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message")
						.value("The account with ID " + nonExistentId + " does not exist."))
				.andDo(print());

		verify(accountService).getAccountById(nonExistentId);
	}

	@Test
	@DisplayName("POST /api/v1/accounts - creates account successfully")
	void createAccount_WithValidData_ReturnsCreatedAccount() throws Exception {

		CreateAccountDTO requestDTO = createAccountDTO(1000.0, "SAVINGS", 1);
		Account createdAccount = createAccount(1, "A000001", 1000.0,
				AccountType.SAVINGS, 1, true);

		when(accountService.createAccount(any(CreateAccountDTO.class))).thenReturn(createdAccount);

		mockMvc.perform(post("/api/v1/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.accountId").value(1))
				.andExpect(jsonPath("$.accountNumber").value("A000001"))
				.andExpect(jsonPath("$.balance").value(1000.0))
				.andExpect(jsonPath("$.accountType").value("SAVINGS"))
				.andExpect(jsonPath("$.customerId").value(1))
				.andDo(print());

		verify(accountService).createAccount(any(CreateAccountDTO.class));
	}

	@Test
	@DisplayName("POST /api/v1/accounts - creates checking account successfully")
	void createAccount_WithCheckingType_ReturnsCreatedAccount() throws Exception {
		CreateAccountDTO requestDTO = createAccountDTO(500.0, "CHECKING", 1);
		Account createdAccount = createAccount(2, "A000002", 500.0,
				AccountType.CHECKING, 1, true);

		when(accountService.createAccount(any(CreateAccountDTO.class))).thenReturn(createdAccount);

		mockMvc.perform(post("/api/v1/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accountType").value("CHECKING"))
				.andExpect(jsonPath("$.balance").value(500.0))
				.andDo(print());

		verify(accountService).createAccount(any(CreateAccountDTO.class));
	}

	@Test
	@DisplayName("POST /api/v1/accounts - creates account with zero initial balance")
	void createAccount_WithZeroBalance_ReturnsCreatedAccount() throws Exception {

		CreateAccountDTO requestDTO = createAccountDTO(0.0, "SAVINGS", 1);
		Account createdAccount = createAccount(3, "A000003", 0.0,
				AccountType.SAVINGS, 1, true);

		when(accountService.createAccount(any(CreateAccountDTO.class))).thenReturn(createdAccount);

		mockMvc.perform(post("/api/v1/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.balance").value(0.0))
				.andDo(print());

		verify(accountService).createAccount(any(CreateAccountDTO.class));
	}

	@Test
	@DisplayName("POST /api/v1/accounts - returns 400 when customer does not exist")
	void createAccount_WithNonExistentCustomer_ReturnsBadRequest() throws Exception {

		CreateAccountDTO requestDTO = createAccountDTO(1000.0, "SAVINGS", 999);

		when(accountService.createAccount(any(CreateAccountDTO.class)))
				.thenThrow(new AccountValidationException("Customer not found for ID: 999"));

		mockMvc.perform(post("/api/v1/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Customer not found for ID: 999"))
				.andDo(print());

		verify(accountService).createAccount(any(CreateAccountDTO.class));
	}

	@Test
	@DisplayName("POST /api/v1/accounts - returns 400 when customer is inactive")
	void createAccount_WithInactiveCustomer_ReturnsBadRequest() throws Exception {

		CreateAccountDTO requestDTO = createAccountDTO(1000.0, "CHECKING", 5);

		when(accountService.createAccount(any(CreateAccountDTO.class)))
				.thenThrow(new AccountValidationException("Customer is not active for ID: 5"));

		mockMvc.perform(post("/api/v1/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Customer is not active for ID: 5"))
				.andDo(print());

		verify(accountService).createAccount(any(CreateAccountDTO.class));
	}

	@ParameterizedTest
	@MethodSource("invalidCreateAccountDTOs")
	@DisplayName("POST /api/v1/accounts - returns 400 for invalid input data")
	void createAccount_WithInvalidData_ReturnsBadRequest(CreateAccountDTO invalidDTO)
			throws Exception {

		mockMvc.perform(post("/api/v1/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(invalidDTO))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(print());

		verifyNoInteractions(accountService);
	}

	static Stream<CreateAccountDTO> invalidCreateAccountDTOs() {
		return Stream.of(
				// Negative balance
				new CreateAccountDTO(-100.0, "SAVINGS", 1),
				// Null balance
				new CreateAccountDTO(null, "SAVINGS", 1),
				// Invalid account type
				new CreateAccountDTO(100.0, "INVALID", 1),
				// Null account type
				new CreateAccountDTO(100.0, null, 1),
				// Null customer ID
				new CreateAccountDTO(100.0, "SAVINGS", null)
		);
	}

	@Test
	@DisplayName("PATCH /api/v1/accounts/activate/{accountId} - activates account successfully")
	void activateAccount_WithValidId_ReturnsActivatedAccount() throws Exception {

		Integer accountId = 1;
		Account activatedAccount = createAccount(accountId, "A000001", 500.0,
				AccountType.SAVINGS, 1, true);

		when(accountService.activateAccount(accountId)).thenReturn(activatedAccount);

		mockMvc.perform(patch("/api/v1/accounts/activate/{accountId}", accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.accountId").value(accountId))
				.andExpect(jsonPath("$.accountNumber").value("A000001"))
				.andDo(print());

		verify(accountService).activateAccount(accountId);
	}

	@Test
	@DisplayName("PATCH /api/v1/accounts/activate/{accountId} - returns 404 when account not " +
			"found")
	void activateAccount_WithNonExistentId_ReturnsNotFound() throws Exception {

		Integer nonExistentId = 999;

		when(accountService.activateAccount(nonExistentId))
				.thenThrow(
						new AccountNotFoundException("Account not found for ID: " + nonExistentId));

		mockMvc.perform(patch("/api/v1/accounts/activate/{accountId}", nonExistentId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(
						jsonPath("$.message").value("Account not found for ID: " + nonExistentId))
				.andDo(print());

		verify(accountService).activateAccount(nonExistentId);
	}

	@Test
	@DisplayName("PATCH /api/v1/accounts/deactivate/{accountId} - deactivates account with zero " +
			"balance")
	void deactivateAccount_WithZeroBalance_ReturnsDeactivatedAccount() throws Exception {

		Integer accountId = 1;
		Account deactivatedAccount = createAccount(accountId, "A000001", 0.0,
				AccountType.SAVINGS, 1, false);

		when(accountService.deactivateAccount(accountId)).thenReturn(deactivatedAccount);

		mockMvc.perform(patch("/api/v1/accounts/deactivate/{accountId}", accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.accountId").value(accountId))
				.andExpect(jsonPath("$.balance").value(0.0))
				.andDo(print());

		verify(accountService).deactivateAccount(accountId);
	}

	@Test
	@DisplayName("PATCH /api/v1/accounts/deactivate/{accountId} - returns 400 when balance is " +
			"positive")
	void deactivateAccount_WithPositiveBalance_ReturnsBadRequest() throws Exception {

		Integer accountId = 1;

		when(accountService.deactivateAccount(accountId))
				.thenThrow(new AccountValidationException(
						"Account with ID: " + accountId +
								" cannot be deactivated because it has a positive balance."));

		mockMvc.perform(patch("/api/v1/accounts/deactivate/{accountId}", accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("Account with ID: " + accountId +
								" cannot be deactivated because it has a positive balance."))
				.andDo(print());

		verify(accountService).deactivateAccount(accountId);
	}

	@Test
	@DisplayName("PATCH /api/v1/accounts/deactivate/{accountId} - returns 404 when account not " +
			"found")
	void deactivateAccount_WithNonExistentId_ReturnsNotFound() throws Exception {

		Integer nonExistentId = 999;

		when(accountService.deactivateAccount(nonExistentId))
				.thenThrow(
						new AccountNotFoundException("Account not found for ID: " + nonExistentId));

		mockMvc.perform(patch("/api/v1/accounts/deactivate/{accountId}", nonExistentId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(
						jsonPath("$.message").value("Account not found for ID: " + nonExistentId))
				.andDo(print());

		verify(accountService).deactivateAccount(nonExistentId);
	}

	@Test
	@DisplayName("GET /api/v1/accounts/customer/{customerId} - returns accounts for customer")
	void getAccountsByCustomerId_WithExistingCustomer_ReturnsAccountList() throws Exception {

		Integer customerId = 1;
		List<Account> customerAccounts = Arrays.asList(
				createAccount(1, "A000001", 1000.0, AccountType.SAVINGS, customerId, true),
				createAccount(2, "A000002", 500.0, AccountType.CHECKING, customerId, true)
		);

		when(accountService.getAccountsByCustomerId(customerId)).thenReturn(customerAccounts);

		mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", customerId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].customerId").value(customerId))
				.andExpect(jsonPath("$[1].customerId").value(customerId))
				.andExpect(jsonPath("$[0].accountNumber").value("A000001"))
				.andExpect(jsonPath("$[1].accountNumber").value("A000002"))
				.andDo(print());

		verify(accountService).getAccountsByCustomerId(customerId);
	}

	@Test
	@DisplayName("GET /api/v1/accounts/customer/{customerId} - returns single account for " +
			"customer")
	void getAccountsByCustomerId_WithSingleAccount_ReturnsAccountList() throws Exception {

		Integer customerId = 2;
		List<Account> customerAccounts = Collections.singletonList(
				createAccount(5, "A000005", 3000.0, AccountType.SAVINGS, customerId, true)
		);

		when(accountService.getAccountsByCustomerId(customerId)).thenReturn(customerAccounts);

		mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", customerId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].customerId").value(customerId))
				.andDo(print());

		verify(accountService).getAccountsByCustomerId(customerId);
	}

	@Test
	@DisplayName("GET /api/v1/accounts/customer/{customerId} - returns 404 when no accounts found")
	void getAccountsByCustomerId_WithNoAccounts_ReturnsNotFound() throws Exception {

		Integer customerId = 999;

		when(accountService.getAccountsByCustomerId(customerId))
				.thenThrow(new AccountNotFoundException(
						"No accounts found with customer ID: " + customerId));

		mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", customerId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message")
						.value("No accounts found with customer ID: " + customerId))
				.andDo(print());

		verify(accountService).getAccountsByCustomerId(customerId);
	}

	/*@Test
	@DisplayName("DELETE /api/v1/accounts/{accountId} - deletes account with zero balance")
	void deleteAccountById_WithZeroBalance_ReturnsNoContent() throws Exception {

		Integer accountId = 1;
		doNothing().when(accountService).deleteAccountById(accountId);

		mockMvc.perform(delete("/api/v1/accounts/{accountId}", accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent())
				.andDo(print());

		verify(accountService).deleteAccountById(accountId);
	}

	@Test
	@DisplayName("DELETE /api/v1/accounts/{accountId} - returns 400 when balance is positive")
	void deleteAccountById_WithPositiveBalance_ReturnsBadRequest() throws Exception {

		Integer accountId = 1;

		doThrow(new AccountValidationException(
				"Account with ID: " + accountId +
						" cannot be deleted because it has a positive balance."))
				.when(accountService).deleteAccountById(accountId);

		mockMvc.perform(delete("/api/v1/accounts/{accountId}", accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message")
						.value("Account with ID: " + accountId +
								" cannot be deleted because it has a positive balance."))
				.andDo(print());

		verify(accountService).deleteAccountById(accountId);
	}

	@Test
	@DisplayName("DELETE /api/v1/accounts/{accountId} - returns 404 when account not found")
	void deleteAccountById_WithNonExistentId_ReturnsNotFound() throws Exception {

		Integer nonExistentId = 999;

		doThrow(new AccountNotFoundException(
				"Delete stopped. Account not found for ID: " + nonExistentId))
				.when(accountService).deleteAccountById(nonExistentId);

		mockMvc.perform(delete("/api/v1/accounts/{accountId}", nonExistentId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message")
						.value("Delete stopped. Account not found for ID: " + nonExistentId))
				.andDo(print());

		verify(accountService).deleteAccountById(nonExistentId);
	}*/

	private Account createAccount(Integer id, String accountNumber,
								  double balance, AccountType type,
								  Integer customerId, boolean active) {
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

	private String toJson(Object object) throws Exception {
		return objectMapper.writeValueAsString(object);
	}
}