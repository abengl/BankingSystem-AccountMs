package com.alessandragodoy.accountms.service.impl;

import com.alessandragodoy.accountms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.controller.dto.TransferValidationResponseDTO;
import com.alessandragodoy.accountms.exception.AccountNotFoundException;
import com.alessandragodoy.accountms.exception.InsufficientFundsException;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.IInternalAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the IntegrationAccountService interface.
 */
@Service
@RequiredArgsConstructor
public class InternalAccountServiceImpl implements IInternalAccountService {

	private final AccountRepository accountRepository;

	@Override
	public TransferValidationResponseDTO validateTransfer(TransferRequestDTO transferRequestDTO) {

		Optional<Account> sourceAccount =
				accountRepository.findById(transferRequestDTO.getSourceAccountId());
		if (sourceAccount.isEmpty()) {
			return TransferValidationResponseDTO.invalid(
					"Source account not found for ID: " + transferRequestDTO.getSourceAccountId());
		}
		if (!sourceAccount.get().isActive()) {
			return TransferValidationResponseDTO.invalid(
					"Source account is not active for ID: " + transferRequestDTO.getSourceAccountId());
		}
		if (sourceAccount.get().getBalance() < transferRequestDTO.getAmount()) {
			return TransferValidationResponseDTO.invalid(
					"Insufficient balance in source account for ID: " + transferRequestDTO.getSourceAccountId());
		}

		Optional<Account> destinationAccount =
				accountRepository.findById(transferRequestDTO.getDestinationAccountId());
		if (destinationAccount.isEmpty()) {
			return TransferValidationResponseDTO.invalid(
					"Destination account not found for ID: " + transferRequestDTO.getDestinationAccountId());
		}
		if (!destinationAccount.get().isActive()) {
			return TransferValidationResponseDTO.invalid(
					"Destination account is not active for ID: " + transferRequestDTO.getDestinationAccountId());
		}

		return TransferValidationResponseDTO.valid();
	}

	@Transactional
	@Override
	public void transferBalanceBetweenAccounts(TransferRequestDTO transfer) throws Exception {

		Account sourceAccount = accountRepository.findById(transfer.getSourceAccountId())
				.orElseThrow(
						() -> new AccountNotFoundException("Source account not found for ID: " +
								transfer.getSourceAccountId()));

		Account destinationAccount = accountRepository.findById(transfer.getDestinationAccountId())
				.orElseThrow(() -> new AccountNotFoundException(
						"Destination account not found for ID: " +
								transfer.getDestinationAccountId()));

		if (sourceAccount.getBalance() < transfer.getAmount()) {
			throw new InsufficientFundsException("Insufficient balance in source account for ID:" +
					" " + transfer.getSourceAccountId());
		}

		sourceAccount.setBalance(sourceAccount.getBalance() - transfer.getAmount());
		destinationAccount.setBalance(destinationAccount.getBalance() + transfer.getAmount());

		accountRepository.save(sourceAccount);
		accountRepository.save(destinationAccount);

	}

	@Override
	public boolean accountIsActiveByCustomerId(Integer customerId) {

		return accountRepository.existsByCustomerIdAndActiveTrue(customerId);
	}
}
