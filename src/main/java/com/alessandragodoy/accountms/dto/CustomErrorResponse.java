package com.alessandragodoy.accountms.dto;

import java.time.LocalDateTime;

/**
 * A record representing a custom error response.
 *
 * @param timestamp the timestamp when the error occurred
 * @param message   the error message
 * @param path      the request path where the error occurred
 */
public record CustomErrorResponse (LocalDateTime timestamp, String message, String path) {
}
