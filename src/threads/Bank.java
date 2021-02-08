package threads;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A bank with a number of bank accounts...
 */

public class Bank {

    private final double[] accounts;
    private Condition sufficientFunds;
    private ReentrantLock bankLock = new ReentrantLock();

    /**
     * Construct the bank
     * @param n number of accounts
     * @param initialBalance the initial balance of these accounts...
     */
    public Bank(int n, double initialBalance){
        this.accounts = new double[n];
        Arrays.fill(accounts, initialBalance);
        sufficientFunds = bankLock.newCondition();
    }

    /**
     * Transfer the money from one account to another
     * @param from the account to transfer from
     * @param to the account to transfer to
     * @param amount the amount to transfer
     */
    public void transferUnsafe(int from, int to, double amount){
        if(accounts[from] < amount) return;
        System.out.println(Thread.currentThread());

        accounts[from] -= amount;
        System.out.printf(" %10.2f from %d to %d", amount, from, to);

        accounts[to] += amount;
        System.out.printf(" Total balance: %10.2f%n", getTotalBalance());

    }

    /**
     * Transfer with thread synchronized
     */
    public void transfer(int from, int to, double amount){
        bankLock.lock();
        try{
            while(accounts[from] < amount)
                sufficientFunds.await(); // Wait

            System.out.println(Thread.currentThread());

            sufficientFunds.signalAll();

            accounts[from] -= amount;
            System.out.printf(" %10.2f from %d to %d", amount, from, to);

            accounts[to] += amount;
            System.out.printf(" Total balance: %10.2f%n", getTotalBalance());
        }
        //IMPORTANT!: Unlock the lock when finished...
        catch (InterruptedException e) {
            e.printStackTrace();
        } finally{

            bankLock.unlock();
        }
    }

    /**
     * Using synchronized word to rewrite transfer method
     */
    public synchronized void transferUsingSyn (int from, int to, double amount) throws InterruptedException{
        while(accounts[from] < amount)
            wait();
        accounts[from] -= amount;
        accounts[to] += amount;
        notifyAll();
    }


    /**
     * Gets the sum of all account balances
     * @return
     */
    private double getTotalBalance() {
        bankLock.lock();
        try {
            double sum = 0;
            for(double a : accounts) sum += a;
            return sum;
        } finally {
            bankLock.unlock();
        }
    }

    /**
     * Gets the number of accounts in a bank
     * @return the number of accounts
     */
    public int size(){
        return accounts.length;
    }


}
