package com.alessandragodoy.accountms.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

	@NotBlank(message = "Source account ID is a required field")
	@Positive(message = "Source account ID must be a positive integer")
	@Schema(description = "Unique identifier for the source account", example = "1")
	Integer sourceAccountId;

	@NotBlank(message = "Destination account ID is a required field")
	@Positive(message = "Destination account ID must be a positive integer")
	@Schema(description = "Unique identifier for the destination account", example = "2")
	Integer destinationAccountId;

	@NotNull(message = "Amount is a required field")
	@Positive(message = "Amount must be positive")
	@Digits(integer = 10, fraction = 2, message = "The amount must be a valid monetary amount with" +
			" up to 12 digits and 2 decimal places")
	@Schema(description = "Amount to deposit", example = "100.0")
	Double amount;

}
