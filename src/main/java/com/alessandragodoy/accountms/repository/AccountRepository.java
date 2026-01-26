package com.alessandragodoy.accountms.repository;

import com.alessandragodoy.accountms.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Account entity.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

	/**
	 * Checks if an account exists by customer ID.
	 *
	 * @param customerId the customer ID
	 * @return true if an account exists, false otherwise
	 */
	boolean existsByCustomerIdAndActiveTrue(Integer customerId);

	/**
	 * Updates the balance of an account by depositing an amount.
	 *
	 * @param accountId the account ID
	 * @param amount    the amount to deposit
	 */
	@Modifying
	@Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.accountId = :accountId")
	void updateBalanceDeposit(@Param("accountId") Integer accountId, @Param("amount") Double amount);

	/**
	 * Updates the balance of an account by withdrawing an amount.
	 *
	 * @param accountId the account ID
	 * @param amount    the amount to withdraw
	 */
	@Modifying
	@Query("UPDATE Account a SET a.balance = a.balance - :amount WHERE a.accountId = :accountId")
	void updateBalanceWithdraw(@Param("accountId") Integer accountId, @Param("amount") Double amount);

	/**
	 * Retrieves the balance of an account by account ID.
	 *
	 * @param accountId the account ID
	 * @return the balance of the account
	 */
	@Query("SELECT a.balance FROM Account a WHERE a.accountId = :accountId")
	Double getBalanceByAccountId(@Param("accountId") Integer accountId);

	/**
	 * Finds an account by its account number.
	 *
	 * @param accountNumber the account number
	 * @return an Optional containing the account if found, or an empty Optional if not found
	 */
	Optional<Account> findByAccountNumber(String accountNumber);

	/**
	 * Checks if an account exists by its account number.
	 *
	 * @param accountNumber the account number
	 * @return true if an account exists, false otherwise
	 */
	boolean existsByAccountNumber(String accountNumber);
}
