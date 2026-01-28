package com.alessandragodoy.accountms.service;

import com.alessandragodoy.accountms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.controller.dto.TransferResponseDTO;

/**
 * Service interface for internal account operations.
 */
public interface IInternalAccountService {

	/**
	 * Updates the balance of an account by its ID.
	 *
	 * @param transferRequestDTO the transfer request DTO containing account IDs and amount
	 * @return {@code TransferResponseDTO} indicating the result of the transfer operation
	 */
	TransferResponseDTO executeTransfer(TransferRequestDTO transferRequestDTO);

	/**
	 * Checks if active account exists for a given customer ID.
	 *
	 * @param customerId the ID of the customer
	 * @return true if an account exists, false otherwise
	 */
	boolean accountIsActiveByCustomerId(Integer customerId);
}
