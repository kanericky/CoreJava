package threads.unsynch;

import threads.Bank;

/**
 * This program shows data corruption when multiple threads access a data structure.
 * @author kanericky
 */

public class UnsynchBankTest {

    public static final int NACCOUNTS = 100;
    public static final int INTIALAMOUNT = 1000;
    public static final int DELAY = 10;
    public static final int STEPS = 100;
    public static final double MAX_AMOUNT = 1000;

    public static void main(String[] args) {

        var bank = new Bank(NACCOUNTS, INTIALAMOUNT);

        for(int i = 0; i < NACCOUNTS; i++){
            int fromAccount = 1;
            Runnable task1 = () -> {
                try{
                    while(true){
                        int toAccount = (int) (bank.size() * Math.random());
                        double amount = MAX_AMOUNT * Math.random();
                        bank.transfer(fromAccount, toAccount, amount);
                        Thread.sleep((int) (DELAY * Math.random()));
                    }
                } catch (InterruptedException e){}
            };

            var t = new Thread(task1);
            t.start();
        }
    }
}
