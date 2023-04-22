package org.example;

public class SemaforBinarny implements Semafor{
    private boolean stan;

    public SemaforBinarny(boolean stan) {
        this.stan = stan;
    }

    @Override
    public synchronized void P() {
        while (!stan) {
            try {
                this.wait();
            } catch (InterruptedException e){
                e.printStackTrace();
                return;
            }
        }
        stan = false;
        this.notify();
    }
    @Override
    public synchronized void V() {
        while(stan){
            try {
                this.wait();
            } catch (InterruptedException e){
                e.printStackTrace();
                return;
            }
        }
        stan = true;
        this.notify();
    }

}
