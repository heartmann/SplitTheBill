package com.gmail.dev.hartmann.valentin.splitthebill.payment;

public class DuplicatePayerException extends Exception {

	public DuplicatePayerException() {
	}

	public DuplicatePayerException(String message) {
		super(message);
	}

	public DuplicatePayerException(Throwable cause) {
		super(cause);
	}

	public DuplicatePayerException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
