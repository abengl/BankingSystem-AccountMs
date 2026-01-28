package com.alessandragodoy.accountms.service.impl;

import com.alessandragodoy.accountms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.accountms.controller.dto.TransferResponseDTO;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.repository.AccountRepository;
import com.alessandragodoy.accountms.service.IInternalAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the IInternalAccountService interface.
 */
@Service
@RequiredArgsConstructor
public class InternalAccountServiceImpl implements IInternalAccountService {

	private final AccountRepository accountRepository;

	@Transactional
	@Override
	public TransferResponseDTO executeTransfer(TransferRequestDTO transferRequestDTO) {

		Optional<Account> sourceAccountOpt =
				accountRepository.findById(transferRequestDTO.getSourceAccountId());

		if (sourceAccountOpt.isEmpty()) {
			return TransferResponseDTO.failed("SOURCE_ACCOUNT_NOT_FOUND",
					"Source account not found for ID: " + transferRequestDTO.getSourceAccountId());
		}

		Account sourceAccount = sourceAccountOpt.get();

		if (!sourceAccount.isActive()) {
			return TransferResponseDTO.failed("SOURCE_ACCOUNT_INACTIVE",
					"Source account is not active for ID: " + transferRequestDTO.getSourceAccountId());
		}
		if (sourceAccount.getBalance() < transferRequestDTO.getAmount()) {
			return TransferResponseDTO.failed("INSUFFICIENT_FUNDS",
					"Insufficient balance in source account: " + sourceAccount.getBalance());
		}

		Optional<Account> destinationAccountOpt =
				accountRepository.findById(transferRequestDTO.getDestinationAccountId());

		if (destinationAccountOpt.isEmpty()) {
			return TransferResponseDTO.failed("DESTINATION_ACCOUNT_NOT_FOUND",
					"Destination account not found for ID: " + transferRequestDTO.getDestinationAccountId());
		}

		Account destinationAccount = destinationAccountOpt.get();

		if (!destinationAccount.isActive()) {
			return TransferResponseDTO.failed("DESTINATION_ACCOUNT_INACTIVE",
					"Destination account is not active for ID: " + transferRequestDTO.getDestinationAccountId());
		}

		sourceAccount.setBalance(sourceAccount.getBalance() - transferRequestDTO.getAmount());
		destinationAccount.setBalance(
				destinationAccount.getBalance() + transferRequestDTO.getAmount());

		accountRepository.save(sourceAccount);
		accountRepository.save(destinationAccount);

		return TransferResponseDTO.success(
				sourceAccount.getAccountId(),
				destinationAccount.getAccountId(),
				sourceAccount.getBalance(),
				destinationAccount.getBalance()
		);

	}

	@Override
	public boolean accountIsActiveByCustomerId(Integer customerId) {

		return accountRepository.existsByCustomerIdAndActiveTrue(customerId);
	}
}
