package com.gmail.dev.hartmann.valentin.splitthebill.payment;

public class CumulativeDebtNotFittingException extends Exception {
	
	private boolean debtGreaterZero;

	public CumulativeDebtNotFittingException(boolean debtGreaterZero) {
		this.debtGreaterZero = debtGreaterZero;
	}
	
	public CumulativeDebtNotFittingException(boolean debtGreaterZero, String message) {
		super(message);
		this.debtGreaterZero = debtGreaterZero;
	}
	
	public CumulativeDebtNotFittingException(boolean debtGreaterZero, Throwable cause) {
		super(cause);
		this.debtGreaterZero = debtGreaterZero;
	}
	
	public CumulativeDebtNotFittingException(boolean debtGreaterZero, String message, Throwable cause) {
		super(message, cause);
		this.debtGreaterZero = debtGreaterZero;
	}
	
	
	public boolean isGreaterZero() {
		return debtGreaterZero;
	}

}
