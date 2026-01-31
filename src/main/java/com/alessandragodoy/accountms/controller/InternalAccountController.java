package com.alessandragodoy.accountms.controller;

import com.alessandragodoy.accountms.api.internal.InternalAccountApi;
import com.alessandragodoy.accountms.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.dto.TransferResponseDTO;
import com.alessandragodoy.accountms.service.IInternalAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing accounts.
 */
@RestController
@RequiredArgsConstructor
public class InternalAccountController implements InternalAccountApi {

	private final IInternalAccountService internalAccountService;

	/**
	 * Executes a balance transfer between two accounts.
	 *
	 * @param transferRequestDTO the data for the transfer request.
	 * @return {@code ResponseEntity<String>} indicating the success of the operation.
	 */
	@Override
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
	@Override
	public ResponseEntity<Boolean> getAccountByCustomerId(@PathVariable Integer customerId) {

		Boolean activeAccounts = internalAccountService.accountIsActiveByCustomerId(customerId);

		return ResponseEntity.ok(activeAccounts);
	}
}
