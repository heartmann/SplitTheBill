package com.gmail.dev.hartmann.valentin.splitthebill.payment;

/*
 * Amounts of money are always in the smallest currency unit, e.g. cent.
 * Two 'Payer''s are regarded equal if their names are equal.
 * The HashCode of a 'Payer' doesn't change throughout its lifetime.
 */
public class Payer implements Comparable<Payer>{
	
	private String name; // the 'Payer''s name
	private long individualBill;
	private long paid; // amount of money the 'Payer' already paid
	
	
	public Payer(String name, long individualBill, long paid) {
		this.name = requireNonNull(name);
		this.individualBill = individualBill;
		this.paid = paid;
	}
	
	
	public String getName() {
		return name;
	}

    protected void setName(String name) {
        this.name = name;
    }
	
	public long getIndividualBill() {
		return individualBill;
	}

	protected void setIndividualBill(long individualBill) {
		this.individualBill = individualBill;
	}

	public long getPaid() {
		return paid;
	}

	protected void setPaid(long paid) {
		this.paid = paid;
	}
	
	
	@Override
	public int hashCode() {
		return name.hashCode();
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
		
		return ((Payer) o).name.equals(name);
	}
	
	@Override
	public int compareTo(Payer that) {
		return this.name.compareTo(that.name);
	}

    // same method as Objects.requireNonNull(), but that is only supported from API 19 upwards
    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
}
