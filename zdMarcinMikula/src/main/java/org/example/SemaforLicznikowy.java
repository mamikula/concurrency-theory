package org.example;

import java.util.LinkedList;
import java.util.List;

public class SemaforLicznikowy implements Semafor {
    private int licznik;

    private SemaforBinarny zmniejszanie;
    private SemaforBinarny dostep;

    public SemaforLicznikowy(int licznik) {
        this.licznik = licznik;
        zmniejszanie = new SemaforBinarny(licznik > 0);
        dostep = new SemaforBinarny(true);
    }

    @Override
    public void V() {
        dostep.P();
        this.licznik += 1;
        if(licznik == 1) zmniejszanie.V();
        dostep.V();
    }
    @Override
    public void P() {
        zmniejszanie.P(); //blokuje mozliwosc odejmowania wszystkim watkom
        dostep.P(); //blokuje dostęp do zmiennej, zeby inny watek nie mogl jej zmienic (w szczegolnosci zwiekszajacy)
        this.licznik -= 1;
        if(licznik > 0) zmniejszanie.V(); //jezeli mozna to informuje wszystkie ze moga dalej dekrementowac
        dostep.V(); //odblokowuje dostep do zmiennej (zwiekszajace watki moga zwiekszac)
    }
    public int semaforLicznikowyStart() {
        List<Thread> dodajace = new LinkedList<>();
        List<Thread> odejmujace = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            odejmujace.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    if(j % 1000 == 0 && j != 0) System.out.println("Watęk " + finalI + " wykonal: " + j + " zmniejszen ");
                    this.P();
                }
            }));
            odejmujace.get(i).start();
        }

        for(int k = 0; k < 10; k++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 10; i++) {
                int finalI = i;
                dodajace.add(new Thread(() -> {
                    for (int j = 0; j < 500; j++)
                        this.V();
                    System.out.println("Watęk " + finalI + " wykonal: " + 500 + " zwiekszen");
                }));
                dodajace.get(10*k + i).start();
            }

            dodajace.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        odejmujace.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("licznik końcowy: " + this.licznik);
        return this.licznik;
    }


}
