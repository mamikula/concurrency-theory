package org.example.waiter;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;



public class Main {

    public static int mealsNumber = 5;

    public static void main(String[] args) {
        int philosophersNumber = 10;
        List<Semaphore> forks = new ArrayList<>();
        List<Philosopher> philosophers = new ArrayList<>();
        Semaphore table = new Semaphore(philosophersNumber - 1);

        for(int i = 0; i < philosophersNumber; i++){
            forks.add(new Semaphore(1));
        }

        for(int i =0; i < philosophersNumber; i++){
            philosophers.add(new Philosopher(forks.get(i), forks.get((i + 1) % philosophersNumber), i, table));
        }

        philosophers.forEach(Thread::start);
        for (Philosopher philosopher : philosophers) {
            try {
                philosopher.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        philosophers.forEach(philosopher -> System.out.println("\nPhilosopher " + philosopher.id + " has eaten " + philosopher.meals +  " meals, average waiting time " + philosopher.getWaitingTime()));


    }
}