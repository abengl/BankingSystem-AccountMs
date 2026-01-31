package com.alessandragodoy.accountms.configuration;

import com.alessandragodoy.accountms.model.Account;
import com.alessandragodoy.accountms.model.AccountType;
import com.alessandragodoy.accountms.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DataLoader is a component that initializes the database with some initial account data
 * if no accounts are found in the repository.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
	private final AccountRepository accountRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

	@Override
	public void run(String... args) {
		if (accountRepository.count() == 0) {
			LOGGER.info("STARTING - No accounts found, creating initial accounts...");
			List<Account> initialAccounts =List.of(
					Account.builder().accountNumber("A00000000001").balance(1000.0).accountType(AccountType.SAVINGS).customerId(1).build(),
					Account.builder().accountNumber("A00000000002").balance(1000.0).accountType(AccountType.CHECKING).customerId(1).build(),
					Account.builder().accountNumber("A00000000003").balance(1000.0).accountType(AccountType.SAVINGS).customerId(2).build(),
					Account.builder().accountNumber("A00000000004").balance(1000.0).accountType(AccountType.CHECKING).customerId(3).build(),
					Account.builder().accountNumber("A00000000005").balance(1000.0).accountType(AccountType.SAVINGS).customerId(4).build(),
					Account.builder().accountNumber("A00000000006").balance(1000.0).accountType(AccountType.CHECKING).customerId(5).build(),
					Account.builder().accountNumber("A00000000007").balance(1000.0).accountType(AccountType.CHECKING).customerId(6).build(),
					Account.builder().accountNumber("A00000000008").balance(1000.0).accountType(AccountType.SAVINGS).customerId(6).build()
			);
			accountRepository.saveAll(initialAccounts);
			LOGGER.info("COMPLETED - Initial accounts added to the database.");
		} else {
			LOGGER.info("SKIPPED - Accounts already exist, skipping initialization.");
		}
	}
}
