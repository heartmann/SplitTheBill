package com.gmail.dev.hartmann.valentin.splitthebill.payment;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;

public class Transfers {
	
	private SortedSet<DebtPayer> payers = new TreeSet<DebtPayer>();
	private Map<Payer, Map<Payer, Long>> transfers = new TreeMap<Payer, Map<Payer, Long>>();
	
	public Transfers(List<Payer> involvedPayers) {
		for (Payer p : involvedPayers) {
			payers.add(new DebtPayer(p, p.getIndividualBill() - p.getPaid()));
		}
	}

	/*
	 * @return the transfers sorted by 'Payer's' 'name'
	 */
	public Map<Payer, Map<Payer, Long>> getTransfers() {
		computeTransfers();
		return transfers;
	}
	
	// This methods computes transfers until either no more 'Payer' has a positive debt, no more 'Payer' has a negative debt or both.  
	private void computeTransfers() {
		transfers = new TreeMap<Payer, Map<Payer, Long>>();
        if (payers.isEmpty()) {
            return;
        }
		DebtPayer sender, receiver;
		long toSend = 1, toReceive = 1;
		while (toSend > 0 && toReceive > 0) { // Are transfers still possible?
			sender = payers.first();
			receiver = payers.last();
			toSend = sender.getDebt();
			toReceive = -receiver.getDebt(); // Negative debt means the Payer gets money.
			
			if (toSend > toReceive) {
				addTransfer(sender, receiver, toReceive);
				changeDebt(sender, toSend - toReceive);
				changeDebt(receiver, 0); // 'receiver''s debt is evened.
			} else if (toSend < toReceive) {
				addTransfer(sender, receiver, toSend);
				changeDebt(sender, 0); // 'sender''s debt is evened.
				changeDebt(receiver, -toReceive + toSend);
			} else if (toSend != 0){ // -> toSend == toReceive != 0
				addTransfer(sender, receiver, toSend);
				changeDebt(sender, 0); // Both debts are evened.
				changeDebt(receiver, 0);
			}
		}
	}
	
	private void addTransfer(DebtPayer debtSender, DebtPayer debtReceiver, long amount) {
		Payer sender = debtSender.getPayer();
		Payer receiver = debtReceiver.getPayer();
		
		Map<Payer, Long> receiverTransactions = transfers.get(receiver);
		if (receiverTransactions != null && receiverTransactions.containsKey(sender)) { // Did a transfer from 'receiver' to 'sender' happen before?
			long oldTransfer = receiverTransactions.get(sender); 
			if (oldTransfer > amount) {
				receiverTransactions.put(sender, oldTransfer - amount);
				return;
			} else { // oldTransfer <= amount
				receiverTransactions.remove(sender);
				if (receiverTransactions.isEmpty()) { // Has this been 'receiver''s only transaction?
					transfers.remove(receiver);
				}
				if (oldTransfer == amount) {
					return;
				} // Only continue if oldTransfer < amount; then a transfer from 'sender' to 'receiver' has to happen.
				amount = amount - oldTransfer;
			}
		}
		
		
		Map<Payer, Long> senderTransactions = transfers.get(sender);
		
		if (senderTransactions == null) { // If 'sender' didn't have any before, a new Map of receivers has to be created.
			senderTransactions = new TreeMap<Payer, Long>();
			transfers.put(sender, senderTransactions);
		}
		
		if (senderTransactions.containsKey(receiver)) {
			senderTransactions.put(receiver, senderTransactions.get(receiver) + amount);
		} else {
			senderTransactions.put(receiver, amount);
		}
	}
	
	// If the 'debt' attribute was mutable, 'payers' would become inconsistent because it is sorted by debt.
	private void changeDebt(DebtPayer p, long debt) {
		payers.remove(p);
		payers.add(new DebtPayer(p.getPayer(), debt));
	}
}
