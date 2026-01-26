package com.alessandragodoy.accountms.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating a new account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDTO {

	@NotNull
	@PositiveOrZero(message = "Balance must be zero or positive")
	@Digits(integer = 10, fraction = 2, message = "Balance must be a valid monetary amount with up" +
			" to 12 digits and 2 decimal places")
	@Schema(description = "Initial balance of the account", example = "100.0")
	Double balance;

	@NotNull
	@Pattern(regexp = "SAVINGS|CHECKING", message = "Account type must be either SAVINGS or " +
			"CHECKING")
	@Schema(description = "Type of the account", example = "SAVINGS")
	String accountType;

	@NotNull
	@Pattern(regexp = "\\d+", message = "Customer ID must contain only digits")
	@Schema(description = "Unique identifier for the customer", example = "1")
	Integer customerId;
}
