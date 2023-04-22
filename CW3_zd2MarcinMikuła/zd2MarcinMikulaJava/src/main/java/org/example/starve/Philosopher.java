package org.example.starve;

import java.util.concurrent.Semaphore;

import static org.example.starve.Main.mealsNumber;

public class Philosopher extends Thread {

    public int id;
    public int meals = 0;
    private Semaphore left;
    private Semaphore right;

    public long waitingTime;


    public Philosopher(Semaphore left, Semaphore right, int id){
        this.left = left;
        this.right = right;
        this.id = id;
        this.waitingTime = 0;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        boolean hasEaten = false;
        for (int i = 0; i < mealsNumber; i++) {
            try {
                if(hasEaten) {
                    startTime = System.currentTimeMillis();
                    hasEaten = false;
                }
                right.acquire();
                if (left.tryAcquire()) {
                    countTime(startTime, System.currentTimeMillis());
                    hasEaten = true;
                    meals += 1;
                    Thread.sleep(400);
                    System.out.println("Philosopher " + id + " has eaten " + meals + " meals");
                    left.release();
                }
                this.right.release();
//                Thread.sleep(300);
                // jeżeli nie będzie contenplate(sleepa powyżej) 2 wątki od razu się wykonają i skończą
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(meals < mealsNumber) {
            countTime(startTime, System.currentTimeMillis());
        }
    }
    private void countTime(long start, long end){
        waitingTime += end - start;
    }

}
