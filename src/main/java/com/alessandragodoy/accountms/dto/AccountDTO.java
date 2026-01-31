package com.alessandragodoy.accountms.dto;

import com.alessandragodoy.accountms.model.AccountType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Account responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {

	@Schema(description = "Unique identifier for the account", example = "1")
	Integer accountId;

	@Schema(description = "Account number", example = "A000001")
	String accountNumber;

	@PositiveOrZero(message = "Balance must be zero or positive")
	@Schema(description = "Current balance of the account", example = "100.0")
	Double balance;

	@Schema(description = "Type of the account", example = "SAVINGS")
	AccountType accountType;

	@Schema(description = "Unique identifier for the customer", example = "1")
	Integer customerId;
}
