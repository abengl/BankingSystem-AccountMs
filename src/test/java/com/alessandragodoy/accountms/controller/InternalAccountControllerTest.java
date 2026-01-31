package com.alessandragodoy.accountms.controller;

import com.alessandragodoy.accountms.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.dto.TransferResponseDTO;
import com.alessandragodoy.accountms.service.IInternalAccountService;
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

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link InternalAccountController}.
 */
@WebMvcTest(InternalAccountController.class)
class InternalAccountControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	IInternalAccountService internalAccountService;

	@Test
	@DisplayName("PATCH /execute-transfer - returns a TransferResponseDTO with the result of the " +
			"operation")
	void executeTransfer_withValidRequest_ReturnsTransferResponseDTO() throws Exception {
		TransferRequestDTO requestDTO =
				createTransferRequestDTO("TRANSFER_OWN_ACCOUNT", 1, 2, 300.0);
		TransferResponseDTO responseDTO = successResponse(1, 2, 500.0, 800.0);

		when(internalAccountService.executeTransfer(any(TransferRequestDTO.class))).thenReturn(
				responseDTO);

		mockMvc.perform(patch("/api/v1/internal/accounts/execute-transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.sourceAccountId").value(1))
				.andExpect(jsonPath("$.destinationAccountId").value(2))
				.andExpect(jsonPath("$.finalSourceBalance").value(500.0))
				.andExpect(jsonPath("$.finalDestinationBalance").value(800.0))
				.andDo(print());

		verify(internalAccountService).executeTransfer(any(TransferRequestDTO.class));
	}

	@Test
	@DisplayName("PATCH /execute-transfer - returns a TransferResponseDTO with error details " +
			"sourceAccount not found")
	void executeTransfer_withInvalidSourceAccount_ReturnsTransferResponseDTO() throws Exception {

		TransferRequestDTO requestDTO = createTransferRequestDTO("TRANSFER_THIRD_PARTY_ACCOUNT",
				30, 1, 100.0);
		TransferResponseDTO responseDTO = failedResponse("SOURCE_ACCOUNT_NOT_FOUND",
				"Source account not found for ID: " + requestDTO.getSourceAccountId());

		when(internalAccountService.executeTransfer(any(TransferRequestDTO.class))).thenReturn(
				responseDTO);

		mockMvc.perform(patch("/api/v1/internal/accounts/execute-transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("SOURCE_ACCOUNT_NOT_FOUND"))
				.andExpect(jsonPath("$.errorMessage").value(
						"Source account not found for ID: " + requestDTO.getSourceAccountId()))
				.andDo(print());

		verify(internalAccountService).executeTransfer(any(TransferRequestDTO.class));
	}

	private TransferResponseDTO failedResponse(String errorCode, String errorMessage) {
		return TransferResponseDTO.failed(errorCode, errorMessage);
	}

	@Test
	@DisplayName("PATCH /execute-transfer - returns a TransferResponseDTO with error details " +
			"sourceAccount inactive")
	void executeTransfer_withInactiveSourceAccount_ReturnsTransferResponseDTO() throws Exception {

		TransferRequestDTO requestDTO = createTransferRequestDTO("TRANSFER_OWN_ACCOUNT",
				5, 1, 1000.0);
		TransferResponseDTO responseDTO = failedResponse("SOURCE_ACCOUNT_INACTIVE",
				"Source account is not active for ID: " + requestDTO.getSourceAccountId());

		when(internalAccountService.executeTransfer(any(TransferRequestDTO.class))).thenReturn(
				responseDTO);

		mockMvc.perform(patch("/api/v1/internal/accounts/execute-transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("SOURCE_ACCOUNT_INACTIVE"))
				.andExpect(jsonPath("$.errorMessage").value(
						"Source account is not active for ID: " + requestDTO.getSourceAccountId()))
				.andDo(print());

		verify(internalAccountService).executeTransfer(any(TransferRequestDTO.class));
	}

	@Test
	@DisplayName("PATCH /execute-transfer - returns a TransferResponseDTO with error details " +
			"insufficient funds")
	void executeTransfer_withInsufficientFunds_ReturnsTransferResponseDTO() throws Exception {

		TransferRequestDTO requestDTO = createTransferRequestDTO("TRANSFER_THIRD_PARTY_ACCOUNT",
				2, 3, 2000.0);
		TransferResponseDTO responseDTO = failedResponse("INSUFFICIENT_FUNDS",
				"Insufficient balance in source account: " + 1500.0);

		when(internalAccountService.executeTransfer(any(TransferRequestDTO.class))).thenReturn(
				responseDTO);

		mockMvc.perform(patch("/api/v1/internal/accounts/execute-transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("INSUFFICIENT_FUNDS"))
				.andExpect(jsonPath("$.errorMessage").value("Insufficient balance in source " +
						"account: " + 1500.0))
				.andDo(print());

		verify(internalAccountService).executeTransfer(any(TransferRequestDTO.class));
	}

	@Test
	@DisplayName("PATCH /execute-transfer - returns a TransferResponseDTO with error details " +
			"destinationAccount not found")
	void executeTransfer_withInvalidDestinationAccount_ReturnsTransferResponseDTO()
			throws Exception {

		TransferRequestDTO requestDTO = createTransferRequestDTO("TRANSFER_THIRD_PARTY_ACCOUNT",
				1, 40, 100.0);
		TransferResponseDTO responseDTO = failedResponse("DESTINATION_ACCOUNT_NOT_FOUND",
				"Destination account not found for ID: " + requestDTO.getDestinationAccountId());

		when(internalAccountService.executeTransfer(any(TransferRequestDTO.class))).thenReturn(
				responseDTO);

		mockMvc.perform(patch("/api/v1/internal/accounts/execute-transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("DESTINATION_ACCOUNT_NOT_FOUND"))
				.andExpect(jsonPath("$.errorMessage").value(
						"Destination account not found for ID: " + requestDTO.getDestinationAccountId()))
				.andDo(print());

		verify(internalAccountService).executeTransfer(any(TransferRequestDTO.class));
	}

	@Test
	@DisplayName("PATCH /execute-transfer - returns a TransferResponseDTO with error details " +
			"destinationAccount inactive")
	void executeTransfer_withInactiveDestinationAccount_ReturnsTransferResponseDTO()
			throws Exception {

		TransferRequestDTO requestDTO = createTransferRequestDTO("TRANSFER_OWN_ACCOUNT",
				1, 6, 1000.0);
		TransferResponseDTO responseDTO = failedResponse("DESTINATION_ACCOUNT_INACTIVE",
				"Destination account is not active for ID: " + requestDTO.getDestinationAccountId());

		when(internalAccountService.executeTransfer(any(TransferRequestDTO.class))).thenReturn(
				responseDTO);

		mockMvc.perform(patch("/api/v1/internal/accounts/execute-transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(requestDTO)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.errorCode").value("DESTINATION_ACCOUNT_INACTIVE"))
				.andExpect(jsonPath("$.errorMessage").value(
						"Destination account is not active for ID: " + requestDTO.getDestinationAccountId()))
				.andDo(print());

		verify(internalAccountService).executeTransfer(any(TransferRequestDTO.class));
	}

	@ParameterizedTest
	@MethodSource("invalidTransferRequestDTOs")
	@DisplayName("PATCH /execute-transfer - returns 400 Bad Request for invalid " +
			"TransferRequestDTO")
	void executeTransfer_withInvalidRequest_ReturnsBadRequest(TransferRequestDTO invalidRequestDTO)
			throws Exception {

		mockMvc.perform(patch("/api/v1/internal/accounts/execute-transfer")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(invalidRequestDTO)))
				.andExpect(status().isBadRequest())
				.andDo(print());

		verifyNoInteractions(internalAccountService);
	}

	static Stream<TransferRequestDTO> invalidTransferRequestDTOs() {
		return Stream.of(
				// Null transactionType
				new TransferRequestDTO(null, 1, 2, 100.0),
				// Empty transactionType
				new TransferRequestDTO("", 1, 2, 100.0),
				// Invalid transactionType
				new TransferRequestDTO("INVALID_TYPE", 1, 2, 100.0),
				// Null sourceAccountId
				new TransferRequestDTO("TRANSFER_OWN_ACCOUNT", null, 2, 100.0),
				// Null destinationAccountId
				new TransferRequestDTO("TRANSFER_OWN_ACCOUNT", 1, null, 100.0),
				// Null amount
				new TransferRequestDTO("TRANSFER_OWN_ACCOUNT", 1, 2, null),
				// Negative amount
				new TransferRequestDTO("TRANSFER_OWN_ACCOUNT", 1, 2, -50.0)
		);
	}

	@Test
	@DisplayName("GET /is-active/customer/{customerId} - returns true if" +
			" customer has active accounts")
	void getAccountByCustomerId_withActiveAccounts_ReturnsTrue() throws Exception {

		Integer customerId = 1;

		when(internalAccountService.accountIsActiveByCustomerId(customerId)).thenReturn(true);

		mockMvc.perform(
						org.springframework.test.web.servlet.request.MockMvcRequestBuilders
								.get("/api/v1/internal/accounts/is-active/customer/{customerId}",
										customerId)
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").value(true))
				.andDo(print());

		verify(internalAccountService).accountIsActiveByCustomerId(customerId);
	}

	@Test
	@DisplayName("GET /is-active/customer/{customerId} - returns false if" +
			" customer has no active accounts")
	void getAccountByCustomerId_withNoActiveAccounts_ReturnsFalse() throws Exception {
		Integer customerId = 2;

		when(internalAccountService.accountIsActiveByCustomerId(customerId)).thenReturn(false);

		mockMvc.perform(
						org.springframework.test.web.servlet.request.MockMvcRequestBuilders
								.get("/api/v1/internal/accounts/is-active/customer/{customerId}",
										customerId)
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").value(false))
				.andDo(print());

		verify(internalAccountService).accountIsActiveByCustomerId(customerId);
	}

	private TransferRequestDTO createTransferRequestDTO(String transactionType,
														Integer sourceAccountId,
														Integer destinationAccountId,
														Double amount) {
		return new TransferRequestDTO(
				transactionType,
				sourceAccountId,
				destinationAccountId,
				amount
		);
	}

	private TransferResponseDTO successResponse(Integer sourceAccountId,
												Integer destinationAccountId, Double sourceBalance,
												Double destinationBalance) {
		return TransferResponseDTO.success(sourceAccountId, destinationAccountId, sourceBalance,
				destinationBalance);
	}

	private String toJson(Object object) throws Exception {
		return objectMapper.writeValueAsString(object);
	}
}