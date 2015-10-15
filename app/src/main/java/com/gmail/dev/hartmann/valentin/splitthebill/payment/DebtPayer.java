package com.gmail.dev.hartmann.valentin.splitthebill.payment;

/*
 * Objects of this type are immutable.
 * To change the debt of a 'DebtPayer' p one must create a new 'DebtPayer' by calling
 * new DebtPayer(p.getPayer, newDebt).
 */
public class DebtPayer implements Comparable<DebtPayer> {
	
	private Payer payer;
	private long debt;
	
	public DebtPayer(Payer payer, long debt) {
		this.payer = payer;
		this.debt = debt;
	}
	
	
	public Payer getPayer() {
		return payer;
	}

	public long getDebt() {
		return debt;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		if (o == this) {
			return true;
		}
		
		if (!o.getClass().equals(getClass())) {
			return false;
		}
		
		return ((DebtPayer) o).payer.equals(payer);
	}
	
	@Override
	public int hashCode() {
		return payer.hashCode();
	}

	@Override
	public int compareTo(DebtPayer that) { // The 'DebtPayer' with the highest debt (== the least money) comes first.
		if (debt > that.debt) {
			return -1;
		}
		if (debt == that.debt) {
            // to make the natural ordering consistent with equals
			return this.payer.compareTo(that.getPayer());
		}
		return 1;
	}
}
