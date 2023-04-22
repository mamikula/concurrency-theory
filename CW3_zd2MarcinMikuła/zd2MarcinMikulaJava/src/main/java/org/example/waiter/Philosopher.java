package org.example.waiter;

import java.util.concurrent.Semaphore;

import static org.example.waiter.Main.mealsNumber;

public class  Philosopher extends Thread {

    public int id;
    public int meals =0;
    private Semaphore left;
    private Semaphore right;

    private Semaphore table;

    private long waitingTime;

    public Philosopher(Semaphore left, Semaphore right, int id, Semaphore table){
        this.left = left;
        this.right = right;
        this.id = id;
        this.table = table;
        this.waitingTime = 0;
    }

    @Override
    public void run() {
        long startTime;
        for (int i = 0; i < mealsNumber; i++) {
            try {
                startTime = System.currentTimeMillis();
                table.acquire();
                right.acquire();
                left.acquire();
                countTime(startTime, System.currentTimeMillis());
                meals += 1;
                Thread.sleep(400);
                System.out.println("Philosopher " + id + " has eaten " + meals + " meals");
                left.release();
                right.release();
                table.release();

                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public long getWaitingTime() {
        return waitingTime / mealsNumber;
    }

    private void countTime(long start, long end){
        waitingTime += end - start;
    }

}
