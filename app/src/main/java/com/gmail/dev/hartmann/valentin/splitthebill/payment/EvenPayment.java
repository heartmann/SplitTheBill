package com.gmail.dev.hartmann.valentin.splitthebill.payment;

import java.util.Map;
import java.util.TreeMap;

public class EvenPayment extends Payment {
	
	private long bill; // in the smallest currency unit, e.g. cent

	public EvenPayment(long bill) {
		this.bill = bill;
	}
	
	@Override
	public Map<Payer, Map<Payer, Long>> getTransfers() throws CumulativeDebtNotFittingException {
        if (payers.isEmpty()) {
            if (bill > 0) {
                throw new CumulativeDebtNotFittingException(true);
            }
            return new TreeMap<>();
        }

		long individualBill = bill / payers.size();
        long rest = bill % payers.size();
		for (Payer p : payers) {
            // Because integer division is used for computing individualBill, it is possible that a
            // part of the bill is cut off and must therefore be split between some of the payers.
            if (rest > 0) {
                setIndividualBill(p, individualBill + 1);
                rest--;
            } else {
                setIndividualBill(p, individualBill);
            }
		}
        requireDebtZero();
		Transfers transfers = new Transfers(payers);
		return transfers.getTransfers();
	}
	
	@Override
	public Payer addPayer(String name, long individualBill, long paid) {
		throw new UnsupportedOperationException("The individual debt is determined by the bill.");
	}

    @Override
    public Payer addPayer(String name, long paid) throws DuplicatePayerException {
        return super.addPayer(name, 0, paid);
    }
}
