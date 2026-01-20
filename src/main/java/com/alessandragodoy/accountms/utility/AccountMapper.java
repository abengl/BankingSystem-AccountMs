package com.alessandragodoy.accountms.utility;

import com.alessandragodoy.accountms.controller.dto.AccountDTO;
import com.alessandragodoy.accountms.controller.dto.CreateAccountDTO;
import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.model.AccountType;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Account entities and DTOs.
 */
@Component
public class AccountMapper {
	public static AccountDTO toDTO(Account account) {
		return new AccountDTO(
				account.getAccountId(),
				account.getAccountNumber(),
				account.getBalance(),
				account.getAccountType(),
				account.getCustomerId()
		);
	}

	public static Account toCreateEntity(CreateAccountDTO createAccountDTO) {
		return new Account(
				null,
				null,
				createAccountDTO.balance(),
				AccountType.valueOf(createAccountDTO.accountType().toUpperCase()),
				createAccountDTO.customerId(),
				null,
				null,
				true
		);
	}
}
