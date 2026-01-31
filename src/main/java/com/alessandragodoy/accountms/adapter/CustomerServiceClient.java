package com.alessandragodoy.accountms.adapter;

import com.alessandragodoy.accountms.dto.CustomerValidationResponseDTO;
import com.alessandragodoy.accountms.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client for interacting with the Customer Microservice.
 * <p>
 * This class uses the <b>Singleton design pattern</b> to ensure that only one instance of the
 * client exists.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class CustomerServiceClient {

	private final RestTemplate restTemplate;

	@Value("${customer.ms.url}")
	private String customerMsBaseUrl;

	/**
	 * Validates if a customer exists and is active by their ID.
	 *
	 * @param customerId the ID of the customer to validate
	 * @return a {@link CustomerValidationResponseDTO} containing the customer's existence,
	 * active status, and additional information.
	 * @throws ExternalServiceException if unable to connect to the customer service or if the
	 *                                  response is invalid.
	 */
	public CustomerValidationResponseDTO validateCustomer(Integer customerId) {

		String url = UriComponentsBuilder
				.fromHttpUrl(customerMsBaseUrl)
				.pathSegment(customerId.toString())
				.toUriString();

		try {
			ResponseEntity<CustomerValidationResponseDTO> response = restTemplate.getForEntity(url,
					CustomerValidationResponseDTO.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				return response.getBody();
			} else {
				throw new ExternalServiceException(
						"Customer service returned an error or empty body.");
			}
		} catch (RestClientException e) {
			throw new ExternalServiceException(
					"Unable to connect to the customer service. " + e.getMessage());
		}
	}
}
