package org.example.demologin.config.exception;

import java.time.LocalDateTime;

public record ResponseTemplate<T>(
		String message,
		T data,
		LocalDateTime timestamp
) {
	public ResponseTemplate(String message, T data) {
		this(message, data, LocalDateTime.now());
	}

}

