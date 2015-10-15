package com.gmail.dev.hartmann.valentin.splitthebill.payment;

import java.io.Serializable;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Payment implements Serializable {

    private static String decimalSeparator = null;
    private static String currencySymbol = null;
	
	protected List<Payer> payers = new ArrayList<>(); // 'Payer''s who have to pay for the bill
	protected long cumDebt = 0; // the cumulative debt of all 'Payer's in 'payers'

    // converts value to value / 100 without a loss of precision (e.g. cents to dollars)
    public static String longToCurrencyAmount(long value) {
        if (decimalSeparator == null) {
            decimalSeparator = String.valueOf(new DecimalFormatSymbols().getDecimalSeparator());
        }

        long first = value / 100;
        long second = value % 100;
        String secondStr;
        if (second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = String.valueOf(second);
        }

        return first + decimalSeparator + secondStr;
    }

    // converts value to value / 100 <currency symbol> without a loss of precision, e.g.  to
    public static String longToCurrency(long value) {
        if (currencySymbol == null) {
            currencySymbol = String.valueOf(new DecimalFormatSymbols().getCurrencySymbol());
        }

        return longToCurrencyAmount(value) + " " + currencySymbol;
    }
	
	
	public abstract Map<Payer, Map<Payer, Long>> getTransfers() throws CumulativeDebtNotFittingException;

    /*
     * @return a 'Map' containing transfers with the 'Payer's' 'name's instead of the 'Payer' objects sorted by sender's and receiver's name
     */
	public Map<String, Map<String, Long>> getTransfersAsStrings() throws CumulativeDebtNotFittingException {
		Map<String, Map<String, Long>> nameTransfers = new TreeMap<>();
		Map<Payer, Map<Payer, Long>> payerTransfers = getTransfers();
		
		long amountTransferred;
		Map<String, Long> currNameTransfers; // the transfers of a single 'Payer'
		Map<Payer, Long> currPayerTransfers;
		for (Payer outer : payers) {
			currNameTransfers = new TreeMap<>();
			currPayerTransfers = payerTransfers.get(outer);
            if (currPayerTransfers != null) {
                for (Payer inner : currPayerTransfers.keySet()) {
                    amountTransferred = currPayerTransfers.get(inner);
                    currNameTransfers.put(inner.getName(), amountTransferred);
                }
                nameTransfers.put(outer.getName(), currNameTransfers);
            }
		}
		return nameTransfers;
	}
	
	
	public void requireDebtZero() throws CumulativeDebtNotFittingException {
		if (cumDebt > 0) {
			throw new CumulativeDebtNotFittingException(true);
		}
		if (cumDebt < 0) {
			throw new CumulativeDebtNotFittingException(false);
		}
	}

	
	public Payer addPayer(String name, long individualBill, long paid) throws DuplicatePayerException {
        for (Payer p : payers) {
            if (p.getName().equals(name)) {
                throw new DuplicatePayerException("A payer of the name " + name + "exists already.");
            }
        }

        Payer newPayer = new Payer(name, individualBill, paid);
		payers.add(newPayer);
		
		cumDebt += individualBill;
		cumDebt -= paid;
		
		return newPayer;
	}
	
	public Payer addPayer(String name, long paid) throws DuplicatePayerException {
		return addPayer(name, 0, paid);
	}
	
	private boolean containsPayer(Payer p) {
		return payers.contains(p);
	}
	
	public boolean containsPayer(String name) {
		try {
			getPayerByName(name);
			return true;
		} catch (NoSuchPayerException e) {
			return false;
		}
	}
	
	protected void requireContaining(Payer p) throws NoSuchPayerException {
		if (!payers.contains(p)) {
			throw new NoSuchPayerException();
		}
	}
	
	public List<Payer> getPayers() {
		return Collections.unmodifiableList(payers);
	}

    // caller has to make sure that payers.contains(p)
    private void removePayer(Payer p) {
		payers.remove(p);
		cumDebt -= p.getIndividualBill();
		cumDebt += p.getPaid();
	}

    /*
     * @return the position of the removed Payer
     */
	public int removePayer(String name) throws NoSuchPayerException {
        Payer currPayer;
		for (int i = 0; i < payers.size(); i++) {
            currPayer = payers.get(i);
            if (currPayer.getName().equals(name)) {
                payers.remove(i);
                cumDebt -= currPayer.getIndividualBill();
                cumDebt += currPayer.getPaid();
                return i;
            }
        }
        throw new NoSuchPayerException();
	}
	
	public Payer getPayerByName(String name) throws NoSuchPayerException {
		for (Payer p : payers) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		throw new NoSuchPayerException();
	}

    public int getPayerIndex(Payer p) throws NoSuchPayerException {
        int pos = payers.indexOf(p);
        if (pos == -1) {
            throw new NoSuchPayerException();
        }
        return pos;
    }

    public int getPayerIndex(String name) throws NoSuchPayerException {
        for (int i = 0; i < payers.size(); i++) {
            if (payers.get(i).getName().equals(name)) {
                return i;
            }
        }
        throw new NoSuchPayerException();
    }


    // caller has to make sure that payers.contains(p)
    protected void setName(Payer p, String newName) throws DuplicatePayerException {
        // check if a Payer of the new name already exists
        if (containsPayer(newName)) {
            throw new DuplicatePayerException();
        }
        p.setName(newName);
    }

    public void setName(String oldName, String newName)
            throws NoSuchPayerException, DuplicatePayerException {
        Payer payer = getPayerByName(oldName);
        setName(payer, newName);
    }

    // caller has to make sure that payers.contains(p)
	protected void setPaid(Payer p, long paid) {
		cumDebt += p.getPaid();
		cumDebt -= paid;
		p.setPaid(paid);
	}

    public void setPaid(String name, long paid) throws NoSuchPayerException {
        // the Payer instance in the internal representation
        Payer payer = getPayerByName(name);
        setPaid(payer, paid);
    }

    // caller has to make sure that payers.contains(p)
	protected void setIndividualBill(Payer p, long individualBill) {
		cumDebt -= p.getIndividualBill();
		cumDebt += individualBill;
		p.setIndividualBill(individualBill);
	}

    public void setIndividualBill(String name, long individualBill) throws NoSuchPayerException {
        // the Payer instance in the internal representation
        Payer payer = getPayerByName(name);
        setIndividualBill(payer, individualBill);
    }

    // caller has to make sure that payers.contains(p)
    protected void editPayer(Payer oldPayer, Payer newPayer) throws DuplicatePayerException {
        if (!oldPayer.getName().equals(newPayer.getName())) {
            setName(oldPayer, newPayer.getName());
        }
        setPaid(oldPayer, newPayer.getPaid());
        setIndividualBill(oldPayer, newPayer.getIndividualBill());
    }

    /*
     * @return the position of the edited Payer
     */
    public int editPayer(String oldName, String newName, long newIndividualBill, long newPaid)
            throws NoSuchPayerException, DuplicatePayerException {
        return editPayer(oldName, new Payer(newName, newIndividualBill, newPaid));
    }

    /*
     * @return the position of the edited Payer
     */
    public int editPayer(String oldName, String newName, long newPaid)
            throws NoSuchPayerException, DuplicatePayerException {
        return editPayer(oldName, new Payer(newName, 0, newPaid));
    }

    /*
     * @return the position of the edited Payer
     */
    public int editPayer(String oldName, Payer newPayer)
            throws NoSuchPayerException, DuplicatePayerException {
        // the Payer instance in the internal representation
        int pos = getPayerIndex(oldName);
        Payer oldPayer = payers.get(pos);
        editPayer(oldPayer, newPayer);
        return pos;
    }

    // caller has to make sure that payers.contains(receiver)
	protected void giveChange(Payer receiver) {
		setIndividualBill(receiver, receiver.getIndividualBill() + cumDebt);
	}
	
	public void giveChange(String name) throws NoSuchPayerException, CumulativeDebtNotFittingException {
		giveChange(getPayerByName(name));
	}
	
}
