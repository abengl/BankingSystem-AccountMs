package com.alessandragodoy.accountms.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

	private boolean success;
	private String errorCode;
	private String errorMessage;

	private Integer sourceAccountId;
	private Integer destinationAccountId;
	private Double finalSourceBalance;
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
