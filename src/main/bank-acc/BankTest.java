import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankTest {
    private Bank bank;
    private BankAccount account1;
    private BankAccount account2;

    @Test
    void simpleTransfer() {
        bank = new Bank();
        account1 = new BankAccount(1000);
        account2 = new BankAccount(1000);
        bank.sendToAccount(account1, account2, 100);
        assertEquals(900, account1.getBalance());
        assertEquals(1100, account2.getBalance());
    }

    @Test
    void threadsTransfer() throws InterruptedException {
        bank = new Bank();
        account1 = new BankAccount(1000);
        account2 = new BankAccount(1000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                bank.sendToAccount(account1, account2, 1);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                bank.sendToAccount(account2, account1, 1);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertEquals(1000, account1.getBalance());
        assertEquals(1000, account2.getBalance());
    }

    @Test
    void notEnoughMoney() {
        bank = new Bank();
        account1 = new BankAccount(1000);
        account2 = new BankAccount(1000);
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, account2, 2000);
        });
    }

    @Test
    void nullFrom() {
        bank = new Bank();
        account1 = new BankAccount(1000);
        account2 = new BankAccount(1000);
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(null, account2, 100);
        });
    }

    @Test
    void nullTo() {
        bank = new Bank();
        account1 = new BankAccount(1000);
        account2 = new BankAccount(1000);
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, null, 100);
        });
    }

    @Test
    void NegativeAmount() {
        bank = new Bank();
        account1 = new BankAccount(1000);
        account2 = new BankAccount(1000);
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, account2, -100);
        });
    }

    @Test
    void ZeroAmount() {
        bank = new Bank();
        account1 = new BankAccount(1000);
        account2 = new BankAccount(1000);
        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, account2, 0);
        });
    }

    @Test
    void Deadlock() throws InterruptedException {
        bank = new Bank();
        account1 = new BankAccount(1000);
        account2 = new BankAccount(1000);
        BankAccount acc1 = new BankAccount(1000);
        BankAccount acc2 = new BankAccount(1000);

        Thread t1 = new Thread(() -> {
            bank.sendToAccountDeadlock(acc1, acc2, 100);
        });
        Thread t2 = new Thread(() -> {
            bank.sendToAccountDeadlock(acc2, acc1, 100);
        });

        t1.start();
        t2.start();
        t1.join(1000);
        t2.join(1000);

        assertTrue(t1.isAlive() || t2.isAlive(), "Deadlock should occur");
    }
}

