package org.example;

public class ZepsutySemaforBinarny implements Semafor{
    private boolean stan;

    public ZepsutySemaforBinarny(boolean stan) {
        this.stan = stan;
    }

    @Override
    public synchronized void P() {
        if(!stan) {
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
        if(stan){
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
