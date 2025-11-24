public class Bank {
    public void sendToAccountDeadlock(BankAccount from, BankAccount to, int amount) {
        synchronized (from) {
            synchronized (to) {
                if (from.getBalance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
            }
        }
    }

    public void sendToAccount(BankAccount from, BankAccount to, int amount) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Счёт не может быть null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма не отрицательна");
        }
        if (from.getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно средств");
        }

        BankAccount first = from.getId() < to.getId() ? from : to;
        BankAccount second = from.getId() < to.getId() ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
            }
        }
    }
}
