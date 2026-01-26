package com.alessandragodoy.accountms;

import com.alessandragodoy.accountms.controller.AccountController;
import com.alessandragodoy.accountms.controller.dto.AccountDTO;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.controller.dto.TransactionRequestDTO;
import com.alessandragodoy.accountms.model.AccountType;
import com.alessandragodoy.accountms.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTests {

	@MockBean
	AccountService accountService;

	@Autowired
	MockMvc mockMvc;

	List<AccountDTO> accounts = new ArrayList<>();

	@BeforeEach
	void setUp() {
		accounts.add(new AccountDTO(1, "A0001", 100.0, AccountType.SAVINGS, 1));
		accounts.add(new AccountDTO(1, "A0002", 100.0, AccountType.CHECKING, 2));
		accounts.add(new AccountDTO(1, "A0003", 100.0, AccountType.SAVINGS, 3));
	}

	@Test
	@DisplayName("Test getAllAccounts - Returns a AccountDTO list with all accounts")
	void getAllAccounts_ReturnsAccountDTOList() throws Exception {
		when(accountService.getAllAccounts()).thenReturn(accounts);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(accounts.size()))
				.andExpect(jsonPath("$[0].accountNumber").value(accounts.get(0).accountNumber()))
				.andDo(print());
	}

	@Test
	@DisplayName("Test getAccountById - Returns a AccountDTO with matching account")
	void getAccountById_ReturnsAccountDTO() throws Exception {
		int accountId = 1;
		when(accountService.getAccountById(accountId)).thenReturn(accounts.get(0));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/" + accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber").value(accounts.get(0).accountNumber()))
				.andDo(print());
	}

	@Test
	@DisplayName("Test createAccount - Returns a AccountDTO with created account")
	void createAccount_ReturnsAccountDTO() throws Exception {
		CreateAccountDTO accountRequest = new CreateAccountDTO(100.0, "CHECKING", 4);
		accounts.add(new AccountDTO(4, "A0004", 100.0, AccountType.CHECKING, 4));

		when(accountService.createAccount(accountRequest)).thenReturn(accounts.get(3));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(accountRequest))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(accountRequest.customerId()))
				.andDo(print());

	}

	@Test
	@DisplayName("Test deposit - Returns a AccountDTO with deposit data")
	void deposit_ReturnsAccountDTO() throws Exception {
		int accountId = 2;
		TransactionRequestDTO transactionRequest = new TransactionRequestDTO(100.0);

		when(accountService.deposit(accountId, transactionRequest.amount())).thenReturn(accounts.get(1));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/accounts/deposit/" + accountId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(transactionRequest))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(accountId))
				.andDo(print());

	}

	@Test
	@DisplayName("Test withdraw - Returns a AccountDTO with withdraw data")
	void withdraw_ReturnsAccountDTO() throws Exception {
		int accountId = 3;
		TransactionRequestDTO transactionRequest = new TransactionRequestDTO(100.0);

		when(accountService.withdraw(accountId, transactionRequest.amount())).thenReturn(accounts.get(2));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/accounts/withdraw/" + accountId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(transactionRequest))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(accountId))
				.andDo(print());

	}

	@Test
	@DisplayName("Test deleteAccountById - Returns a AccountDTO with deleted account data")
	void deleteAccountById_ReturnsAccountDTO() throws Exception {
		int accountId = 3;

		when(accountService.deleteAccountById(accountId)).thenReturn(accounts.get(2));

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/" + accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(accountId))
				.andDo(print());

	}

	@Test
	@DisplayName("Test getAccountByCustomerId - Returns boolean")
	void getAccountByCustomerId_ReturnsBoolean() throws Exception {
		int accountId = 3;

		when(accountService.activeAccountExists(accountId)).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/customer/" + accountId)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(true))
				.andDo(print());

	}

	@Test
	@DisplayName("Test getAccountBalance - Returns double")
	void getAccountBalance_ReturnsDouble() throws Exception {
		String accountNumber = "A0003";
		Double amount = 500.0;
		when(accountService.getAccountBalance(accountNumber)).thenReturn(amount);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/balance/" + accountNumber)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(amount))
				.andDo(print());

	}

	@Test
	@DisplayName("Test verifyAccountByAccountNumber - Returns boolean")
	void verifyAccountByAccountNumber_ReturnsBoolean() throws Exception {
		String accountNumber = "A0003";

		when(accountService.accountExistsByAccountNumber(accountNumber)).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/verify/" + accountNumber)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(true))
				.andDo(print());

	}

	@Test
	@DisplayName("Test updateBalanceByAccountNumber - Returns String")
	void updateBalanceByAccountNumber_ReturnsString() throws Exception {
		String accountNumber = "A0003";
		Double amount = 500.0;

		doNothing().when(accountService).updateBalanceByAccountNumber(accountNumber, amount);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/accounts/update/" + accountNumber + "?amount=" + amount)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("Balance updated successfully"))
				.andDo(print());

	}


}
