package com.alessandragodoy.accountms.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponseDTO {

	@Schema(description = "Indicates if the transfer was successful", example = "true")
	private boolean success;

	@Schema(description = "Error code in case of a failed transfer", example =
			"INSUFFICIENT_FUNDS")
	private String errorCode;

	@Schema(description = "Error message in case of a failed transfer",
			example = "The source account has insufficient funds for this transfer")
	private String errorMessage;

	@Schema(description = "Unique identifier for the source account", example = "1")
	private Integer sourceAccountId;

	@Schema(description = "Unique identifier for the destination account", example = "2")
	private Integer destinationAccountId;

	@Schema(description = "Final balance of the source account after the transfer", example = "500" +
			".0")
	private Double finalSourceBalance;

	@Schema(description = "Final balance of the destination account after the transfer",
			example = "800.0")
	private Double finalDestinationBalance;

	public static TransferResponseDTO success(
			Integer sourceAccountId,
			Integer destinationAccountId,
			Double sourceBalance,
			Double destBalance) {

		return TransferResponseDTO.builder()
				.success(true)
				.sourceAccountId(sourceAccountId)
				.destinationAccountId(destinationAccountId)
				.finalSourceBalance(sourceBalance)
				.finalDestinationBalance(destBalance)
				.build();
	}

	public static TransferResponseDTO failed(String errorCode, String errorMessage) {
		return TransferResponseDTO.builder()
				.success(false)
				.errorCode(errorCode)
				.errorMessage(errorMessage)
				.build();
	}
}
