public class BankAccount {
    private int balance;
    private final int id;
    private static int nextId = 1;

    public BankAccount(int initialBalance) {
        this.balance = initialBalance;
        this.id = nextId++;
    }

    public int getBalance() {
        return balance;
    }

    public int getId() {
        return id;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }
}
