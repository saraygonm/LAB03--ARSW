package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private boolean Pause;
    private final String name;
    private final Object block;


    private final Random r = new Random(System.currentTimeMillis());

    private Boolean isDeath;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.block = new Object();
    }

    public void run() {
        while (immortalsPopulation.size() > 1 && this.getHealth() > 0) {
            synchronized (block) {
                while (Pause) {

                    try {
                        block.wait();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Immortal im;
            int myIndex = immortalsPopulation.indexOf(this);
            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight, in a future it could add a race condition
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }


            im = immortalsPopulation.get(nextFighterIndex);

            //Race conditions
            this.fight(im);
            if (im.getHealth() <= 0) {
                immortalsPopulation.remove(im);
                isDeath = true;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void fight(Immortal i2) {
        Immortal lock1 = ImmortalCode(i2);
        Immortal lock2 = lock1 == this ? i2 : this;
        synchronized(lock1){
            synchronized(lock2){
                if(getHealth() > 0){
                    if (i2.getHealth() > 0) { //getHealth and modificationHealth has race conditions, we can address them with a syncronized block
                        i2.modificationHealth(i2.getHealth() - defaultDamageValue);
                        this.health += defaultDamageValue;
                        updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                    }
                    else {
                        updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                    }
                }
                
            }
         }
    }



    public Immortal ImmortalCode(Immortal i2) {
        Immortal lockToUse;
        int hash1 = System.identityHashCode(this);
        int hash2 = System.identityHashCode(i2);
        if (hash1 < hash2) {
            lockToUse = this;
        } else if (hash1 > hash2) {
            lockToUse = i2;
        } else {
            lockToUse = this;
        }
        return lockToUse;
    }

    public void modificationHealth(int v) {
        health = v;

    }
    //Race conditions
    public int getHealth() {

        return health;

    }


    public void pause() {
        synchronized (block) {
            Pause = true;
        }

    }
    public void keepFighting(){
        synchronized (block) {
            Pause = false;
            block.notifyAll();
        }
    }

    @Override
    public String toString() {
        return name + "[" + health + "]";
    }
    public void detenertodo(){
        isDeath = true;}
}


