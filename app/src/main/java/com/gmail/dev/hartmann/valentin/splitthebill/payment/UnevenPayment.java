package com.gmail.dev.hartmann.valentin.splitthebill.payment;

import java.util.Map;

public class UnevenPayment extends Payment {

	@Override
	public Map<Payer, Map<Payer, Long>> getTransfers() throws CumulativeDebtNotFittingException {
		requireDebtZero();
		Transfers transfers = new Transfers(payers);
		return transfers.getTransfers();
	}
	
	@Override
	public Payer addPayer(String name, long paid) {
		throw new UnsupportedOperationException("Please specify the payer's debt.");
	}
}
