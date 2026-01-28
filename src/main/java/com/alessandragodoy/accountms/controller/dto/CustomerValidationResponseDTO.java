package com.alessandragodoy.accountms.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for customer validation response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerValidationResponseDTO {

	@Schema(description = "Indicates if the customer exists", example = "true")
	private Boolean exists;

	@Schema(description = "Indicates if the customer is active", example = "true")
	private Boolean isActive;

	@Schema(description = "Message providing additional information about the customer validation"
			, example = "Customer is inactive")
	private String message;
}
