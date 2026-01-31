package com.alessandragodoy.accountms.exception;

import com.alessandragodoy.accountms.dto.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Global exception handler for the application.
 * This class handles specific exceptions and returns appropriate HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles AccountNotFoundException and returns a 404 Not Found response.
	 *
	 * @param ex      the AccountNotFoundException that was thrown
	 * @param request the web request during which the exception occurred
	 * @return a ResponseEntity containing a 404 status and a custom error response.
	 */
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<CustomErrorResponse> handleAccountNotFoundException(
			AccountNotFoundException ex, WebRequest request) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles AccountValidationException and returns a 400 Bad Request response.
	 *
	 * @param ex      the AccountValidationException that was thrown
	 * @param request the web request during which the exception occurred
	 * @return a ResponseEntity with a 400 status and the exception message
	 */
	@ExceptionHandler(AccountValidationException.class)
	public ResponseEntity<CustomErrorResponse> handleValidationException(
			AccountValidationException ex, WebRequest request) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles MethodArgumentNotValidException and returns a 400 Bad Request response
	 * when method arguments fail validation.
	 *
	 * @param ex      the MethodArgumentNotValidException that was thrown
	 * @param request the web request during which the exception occurred
	 * @return a ResponseEntity containing a 400 status and a custom error response.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex, WebRequest request) {

		CustomErrorResponse err = new CustomErrorResponse(
				LocalDateTime.now(),
				ex.getMessage(),
				request.getDescription(false)
		);

		return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles InsufficientFundsException and returns a 409 Conflict response.
	 *
	 * @param ex      the InsufficientFundsException that was thrown
	 * @param request the web request during which the exception occurred
	 * @return a ResponseEntity containing a 409 status and a custom error response.
	 */
	@ExceptionHandler(InsufficientFundsException.class)
	public ResponseEntity<CustomErrorResponse> handleInsufficientFundsException(
			InsufficientFundsException ex, WebRequest request) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(err, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<CustomErrorResponse> handleCustomerNotFoundException(
			CustomerNotFoundException ex, WebRequest request) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles all uncaught exceptions and returns a 500 Internal Server Error response.
	 *
	 * @param ex      the exception
	 * @param request the web request
	 * @return a ResponseEntity with a 500 status and a custom error response.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CustomErrorResponse> handleDefaultException(Exception ex,
																	  WebRequest request) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handles ExternalServiceException and returns a 503 Service Unavailable response.
	 *
	 * @param e       the ExternalServiceException that was thrown
	 * @param request the web request during which the exception occurred
	 * @return a ResponseEntity containing a 503 status and a custom error response.
	 */
	@ExceptionHandler(ExternalServiceException.class)
	public ResponseEntity<CustomErrorResponse> handleExternalServiceException(
			ExternalServiceException e, WebRequest request) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), e.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(err, HttpStatus.SERVICE_UNAVAILABLE);
	}
}
