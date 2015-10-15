package com.gmail.dev.hartmann.valentin.splitthebill.payment;

/*
 * Could also be a checked exception but for easier use it is declared as a RuntimeException.
 * As a compensation, every method throwing a NoSuchPayerException mentions it in its throws clause.
 */
public class NoSuchPayerException extends RuntimeException {

	public NoSuchPayerException() {
	}

	public NoSuchPayerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NoSuchPayerException(String arg0) {
		super(arg0);
	}

	public NoSuchPayerException(Throwable arg0) {
		super(arg0);
	}

}
