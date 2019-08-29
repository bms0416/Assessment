package Bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class Bank {
	enum TRANS_TYPE {OPEN, CLOSE, DEPOSIT, WITHDRAW, TRANSFER_FROM, TRANSFER_TO;}
	enum ACCOUNT_STATUS {OPEN, CLOSED;}
	enum STATUS { SUCCESS, FAILURE };
	private static int nextAccountId = 1;
	private static int nextTransactionId = 1;
		
	public class Transaction {
		TRANS_TYPE type;
		double amount;
		Date date;
		public Transaction(TRANS_TYPE t, double a) {
			type = t;
			amount = a;
			date = new Date();
		}
		public Transaction(TRANS_TYPE t) {
			type = t;
			amount = 0;
			date = new Date();
		}
		public Transaction() {
		}
	}

	private class Account {
		private int id = 0;
		private Map<Integer, Transaction> transactions = new HashMap<>();
		private double balance;
		private ACCOUNT_STATUS accountStatus;
		public double getBalance() {
			return balance;
		}
		public Transaction getTransaction(int id) {
			return transactions.get(id);
		}
		public Account() {
			id = nextAccountId++;
		}
		public void deposit(double amount)
		{
			transactions.put(nextTransactionId++, new Transaction(TRANS_TYPE.DEPOSIT, amount));
			balance += amount;
		}
		public void withdraw(double amount) {
			transactions.put(nextTransactionId++, new Transaction(TRANS_TYPE.WITHDRAW, amount));
			balance -= amount;
		}
		public void transferTo(double amount)
		{
			transactions.put(nextTransactionId++, new Transaction(TRANS_TYPE.TRANSFER_TO, amount));
			balance += amount;
		}
		public void transferFrom(double amount) {
			transactions.put(nextTransactionId++, new Transaction(TRANS_TYPE.TRANSFER_FROM, amount));
			balance -= amount;
		}
		public void open() {
			transactions.put(nextTransactionId++, new Transaction(TRANS_TYPE.OPEN));
			accountStatus = ACCOUNT_STATUS.OPEN;
		}
		public void close() {
			if (balance > 0.0)
				withdraw(balance);
			transactions.put(nextTransactionId++, new Transaction(TRANS_TYPE.CLOSE));
			accountStatus = ACCOUNT_STATUS.CLOSED;
		}
		public ACCOUNT_STATUS getStatus() {
			return accountStatus;
		}
		public void putTransaction(Transaction t) {
			transactions.put(nextTransactionId, t);
		}
		public Map<Integer, Transaction> getTransactions() {
			return transactions;
		}
		public Map<Integer, Transaction> getTransactions(Date start, Date end) {
			return transactions;
		}
	}

	private Map<Integer, Account> accounts;
		
	public Bank() {
		accounts = new HashMap<>();
	}
		
	public int openAccount() {
		Account account = new Account();
		accounts.put(account.id, account);
		account.open();
		return account.id;
	}
		
	public double getBalance(int accountId) {
		return accounts.get(accountId).getBalance();
	}
	
	public Map<Integer, Transaction> getTransactions(int accountId) {
		return accounts.get(accountId).getTransactions();
	}
	
	public Map<Integer, Transaction> getTransactions(int accountId, Date start, Date end) {
		Map<Integer, Transaction> retval = new HashMap<>();
		Map<Integer, Transaction> transactions = getTransactions(accountId);
		
		for (Integer key : transactions.keySet()) {
			Transaction t = transactions.get(key);
			if (!t.date.before(start) && !t.date.after(end)) {
				retval.put(key, t);
			}
		}
		
		return retval;
	}
	
	public Account getAccount(int id) {
		return accounts.get(id);
	}
		
	public Transaction getTransaction(int accountId, int transactionId) {
		return accounts.get(accountId).getTransaction(transactionId);
	}
	
	public STATUS deposit(int accountId, double amount) {
		Account a = accounts.get(accountId);
		if (a.getStatus() == ACCOUNT_STATUS.CLOSED) {
			return STATUS.FAILURE;
		}
		a.deposit(amount);
		return STATUS.SUCCESS;
	}
		
	public STATUS withdraw(int accountId, double amount) {
		Account a = accounts.get(accountId);
		if (a.getStatus() == ACCOUNT_STATUS.CLOSED)
			return STATUS.FAILURE;
		if (a.getBalance() < amount) {
			return STATUS.FAILURE;
		}
		a.withdraw(amount);
		return STATUS.SUCCESS;
	}
	
	public STATUS transfer(int from, int to, double amount) {
		Account fromAccount = accounts.get(from);
		Account toAccount = accounts.get(to);
		
		if (fromAccount.getStatus() == ACCOUNT_STATUS.CLOSED)
			return STATUS.FAILURE;
		if (toAccount.getStatus() == ACCOUNT_STATUS.CLOSED)
			return STATUS.FAILURE;
		if (fromAccount.getBalance() < amount) 
			return STATUS.FAILURE;
		fromAccount.transferFrom(amount);
		toAccount.transferTo(amount);
		return STATUS.SUCCESS;
	}
	
	public void closeAccount(int accountId) {
		Account a = accounts.get(accountId);
		a.close();
	}
	public static void main(String[] args) {
		Bank bank = new Bank();
		int accountId1 = bank.openAccount();
		bank.deposit(accountId1, 100.00);
		System.out.println("Final balance is " + bank.getAccount(accountId1).getBalance());
		bank.withdraw(accountId1,  25.00);
		System.out.println("Final balance is " + bank.getAccount(accountId1).getBalance());
		//bank.closeAccount(accountId1);
		System.out.println("Final balance is " + bank.getAccount(accountId1).getBalance());
		Map<Integer, Transaction> m = bank.getTransactions(accountId1);
		for (Integer key : m.keySet()) {
			Transaction t = m.get(key);
			System.out.println(key + " -- " + t.type + " -- " + t.amount + " == " + t.date);
		}
		

		int accountId2 = bank.openAccount();
		bank.deposit(accountId2, 100.00);
		System.out.println("Final balance is " + bank.getAccount(accountId2).getBalance());
		bank.withdraw(accountId2,  25.00);
		System.out.println("Final balance is " + bank.getAccount(accountId2).getBalance());
		bank.transfer(accountId1,  accountId2,  10.00);
		System.out.println("Balance Account 1 is " + bank.getAccount(accountId1).getBalance());
		System.out.println("Balance Account 2 is " + bank.getAccount(accountId2).getBalance());
		bank.closeAccount(accountId2);
		System.out.println("Final balance is " + bank.getAccount(accountId2).getBalance());
		Map<Integer, Transaction> m2 = bank.getTransactions(accountId2);
		for (Integer key : m2.keySet()) {
			Transaction t = m2.get(key);
			System.out.println(key + " -- " + t.type + " -- " + t.amount + " == " + t.date);
		}
		
		System.out.println(bank.getBalance(accountId1));
	}
}
