package com.alessandragodoy.accountms.controller;

import com.alessandragodoy.accountms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.controller.dto.TransferResponseDTO;
import com.alessandragodoy.accountms.service.IInternalAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing accounts.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/accounts")
@Tag(name = "Internal Accounts", description = "Controller for internal microservice Account " +
		"operations")
public class InternalAccountController {

	private final IInternalAccountService internalAccountService;

	/**
	 * Executes a balance transfer between two accounts.
	 *
	 * @param transferRequestDTO the data for the transfer request.
	 * @return {@code ResponseEntity<String>} indicating the success of the operation.
	 */
	@Operation(summary = "Executes a balance transfer between two accounts", description =
			"Returns a TransferResponseDTO with the result of the operation")
	@PatchMapping("/execute-transfer")
	public ResponseEntity<TransferResponseDTO> executeTransfer(
			@Valid @RequestBody TransferRequestDTO transferRequestDTO) {

		TransferResponseDTO response =
				internalAccountService.executeTransfer(transferRequestDTO);

		return ResponseEntity.ok(response);
	}

	/**
	 * Checks if active accounts exist for a given customer ID.
	 *
	 * @param customerId the ID of the customer
	 * @return {@code ResponseEntity<Boolean>} true if active accounts exist, false otherwise
	 */
	@Operation(summary = "Verify if a customer has active accounts by its id", description =
			"Returns boolean")
	@GetMapping("/is-active/customer/{customerId}")
	public ResponseEntity<Boolean> getAccountByCustomerId(@PathVariable Integer customerId) {

		Boolean activeAccounts = internalAccountService.accountIsActiveByCustomerId(customerId);

		return ResponseEntity.ok(activeAccounts);
	}
}
