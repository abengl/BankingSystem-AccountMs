package com.alessandragodoy.accountms.service;

import com.alessandragodoy.accountms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.controller.dto.TransferValidationResponseDTO;

/**
 * Service interface for internal account operations.
 */
public interface IInternalAccountService {

	/**
	 * Validates a transfer request between two accounts.
	 *
	 * @param transferRequestDTO the transfer request DTO containing account IDs and amount
	 * @return {@code TransferValidationResponseDTO} indicating if the transfer is valid or not
	 */
	TransferValidationResponseDTO validateTransfer(TransferRequestDTO transferRequestDTO);

	/**
	 * Updates the balance of an account by its ID.
	 *
	 * @param transfer the transfer request DTO containing account IDs and amount
	 */
	void transferBalanceBetweenAccounts(TransferRequestDTO transfer);

	/**
	 * Checks if active account exists for a given customer ID.
	 *
	 * @param customerId the ID of the customer
	 * @return true if an account exists, false otherwise
	 */
	boolean accountIsActiveByCustomerId(Integer customerId);
}
