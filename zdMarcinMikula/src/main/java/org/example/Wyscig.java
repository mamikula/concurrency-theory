package org.example;

import java.util.LinkedList;
import java.util.List;

public class Wyscig {
    private int licznik;
    private Semafor semafor;

    public Wyscig(Semafor semafor) {
        this.licznik = 0;
        this.semafor = semafor;
    }

    public void zwieksz(){
        semafor.P();
        licznik += 1;
        semafor.V();
    }

    public void zmniejsz(){
        semafor.P();
        licznik -= 1;
        semafor.V();
    }

    public int wyscigStart() {
        List<Thread> dodajace = new LinkedList<>();
        List<Thread> odejmujace = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            dodajace.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++)
                    this.zwieksz();
            }));

            odejmujace.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++)
                    this.zmniejsz();
            }));
        }

        dodajace.forEach(Thread::start);
        odejmujace.forEach(Thread::start);
        dodajace.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        odejmujace.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return this.licznik;
    }



}
