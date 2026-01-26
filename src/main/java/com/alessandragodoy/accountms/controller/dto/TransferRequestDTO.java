package com.alessandragodoy.accountms.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transactions requests.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {

	@NotBlank(message = "Origin Account ID is a required field")
	@Schema(description = "Unique identifier for the origin account", example = "1")
	Integer accountId;

	@NotBlank(message = "Destination Account ID is a required field")
	@Schema(description = "Unique identifier for the destination account", example = "2")
	Integer relatedAccountId;

	@NotNull(message = "Amount is a required field")
	@Positive(message = "The amount must be positive")
	@Digits(integer = 10, fraction = 2, message = "The amount must be a valid monetary amount " +
			"with " +
			"up" +
			" to 12 digits and 2 decimal places")
	@Schema(description = "Amount to deposit", example = "100.0")
	double amount;

	@NotNull
	@Pattern(regexp = "TRANSFER_OWN_ACCOUNT|TRANSFER_INTER_ACCOUNT", message = "Transaction type must be either TRANSFER_OWN_ACCOUNT or " +
			"TRANSFER_INTER_ACCOUNT")
	@Schema(description = "Description of the transaction type", example = "TRANSFER_INTER_ACCOUNT")
	String transactionType;

}
