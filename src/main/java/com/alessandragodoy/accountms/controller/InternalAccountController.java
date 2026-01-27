package com.alessandragodoy.accountms.controller;

import com.alessandragodoy.accountms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.controller.dto.TransferValidationResponseDTO;
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
	 * Validates a balance transfer between two accounts.
	 *
	 * @param transferRequestDTO the data for the transfer request.
	 * @return {@code ResponseEntity<TransferValidationResponseDTO>} containing validation result.
	 */
	@PostMapping("/validate-transfer")
	public ResponseEntity<TransferValidationResponseDTO> validateTransfer(
			@Valid @RequestBody TransferRequestDTO transferRequestDTO) {

		TransferValidationResponseDTO validation =
				internalAccountService.validateTransfer(transferRequestDTO);

		return ResponseEntity.ok(validation);
	}

	/**
	 * Executes a balance transfer between two accounts.
	 *
	 * @param transferRequestDTO the data for the transfer request.
	 * @return {@code ResponseEntity<String>} indicating the success of the operation.
	 */
	@Operation(summary = "Executes a balance transfer between two accounts", description =
			"Returns a String of completion")
	@PatchMapping("/execute-transfer")
	public ResponseEntity<String> updateBalance(
			@Valid @RequestBody TransferRequestDTO transferRequestDTO) throws Exception {

		internalAccountService.transferBalanceBetweenAccounts(transferRequestDTO);

		return ResponseEntity.ok("Balance updated successfully");
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
