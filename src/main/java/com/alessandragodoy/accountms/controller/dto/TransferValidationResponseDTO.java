package com.alessandragodoy.accountms.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transfer validation responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferValidationResponseDTO {

	@Schema(description = "Indicates if the transfer is valid", example = "true")
	private boolean valid;

	@Schema(description = "Message providing additional information about the transfer " +
			"validation", example = "Transfer was completed successfully")
	private String message;

	public static TransferValidationResponseDTO valid() {
		return TransferValidationResponseDTO.builder()
				.valid(true)
				.message(null)
				.build();
	}

	public static TransferValidationResponseDTO invalid(String reason) {
		return TransferValidationResponseDTO.builder()
				.valid(false)
				.message(reason)
				.build();
	}
}
