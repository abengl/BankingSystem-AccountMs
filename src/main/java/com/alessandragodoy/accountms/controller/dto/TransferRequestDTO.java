package com.alessandragodoy.accountms.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transactions requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {

	@NotNull
	@Pattern(regexp = "^(TRANSFER_OWN_ACCOUNT|TRANSFER_THIRD_PARTY_ACCOUNT)$", message =
			"Transaction type must be either " +
					"'TRANSFER_OWN_ACCOUNT' or 'TRANSFER_THIRD_PARTY_ACCOUNT'")
	@Schema(description = "Type of the transaction", example = "TRANSFER_OWN_ACCOUNT")
	String transactionType;

	@NotNull
	@Positive
	@Schema(description = "Unique identifier for the source account", example = "1")
	Integer sourceAccountId;

	@NotNull
	@Positive
	@Schema(description = "Unique identifier for the destination account", example = "2")
	Integer destinationAccountId;

	@NotNull
	@Positive
	@Digits(integer = 10, fraction = 2, message = "The amount must be a valid monetary amount with" +
			" up to 12 digits and 2 decimal places")
	@Schema(description = "Amount to deposit", example = "100.0")
	Double amount;

}
