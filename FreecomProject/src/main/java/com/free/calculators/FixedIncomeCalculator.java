package com.free.calculators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FixedIncomeCalculator {
	public static enum FixedIncomeType {
		PPF ("Public Provident Fund"),
		EPF ("Employee Provident Fund"),
		NSC ("National Savings Certificate"),
		RD ("Recurring Deposit"),
		FD ("Fixed Deposit");

		FixedIncomeType(String name) {
			this.name = name;
		}

		private String name;

		public String getName() {
			return name;
		}
	}

	public static void calculateReturns(String frequency, Date startDate, int amount, int duration, FixedIncomeType type) {
		List<Transaction> transactions = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		if ("Monthly".equals(frequency)) {
			int totalTransactions = 12 * duration;
			while (totalTransactions > 0) {
				Transaction t = new Transaction();
				t.setDate(cal.getTime());
				t.setAmount(amount);
				t.setTransactionType(Transaction.TransactionType.PRINCIPLE);

				cal.add(Calendar.MONTH, 1);

				transactions.add(t);
				totalTransactions--;
			}
		} else if ("Yearly".equals(frequency)) {
			int totalTransactions = duration;
			while (totalTransactions > 0) {
				Transaction t = new Transaction();
				t.setDate(cal.getTime());
				t.setAmount(amount);
				t.setTransactionType(Transaction.TransactionType.PRINCIPLE);

				cal.add(Calendar.YEAR, 1);

				transactions.add(t);
				totalTransactions--;
			}
		}

		calculateReturns(transactions, type);
	}

	private static void calculateReturns(List<Transaction> transactions, FixedIncomeType type) {
		// TODO - fetch interest rates for the specified type
		// TODO - calculate returns compounded annually
	}

	public static class Transaction {
		public static enum TransactionType {
			PRINCIPLE,
			INTEREST;
		}

		private Date date;
		private int amount;
		private TransactionType transactionType;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

		public TransactionType getTransactionType() {
			return transactionType;
		}

		public void setTransactionType(TransactionType transactionType) {
			this.transactionType = transactionType;
		}
	}
}
