package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private Boolean fighting;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.fighting = true;
    }

    public void run() {
        while (this.health < 300 && this.health > 0) {
            if(fighting){
                Immortal im;
                int myIndex = immortalsPopulation.indexOf(this);
                int nextFighterIndex = r.nextInt(immortalsPopulation.size());

                //avoid self-fight, in a future it could add a race condition
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }

                //Race conditions in the access of possibly, the same immortal allocated in the list.
                synchronized(immortalsPopulation){
                    im = immortalsPopulation.get(nextFighterIndex);
                }   
                
                //Race conditions
                this.fight(im);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                synchronized(immortalsPopulation){
                    try {
                        immortalsPopulation.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void fight(Immortal i2) {
        synchronized(i2){
            if (i2.getHealth() > 0) { //getHealth and changeHealth has race conditions, we can address them with a syncronized block
                i2.changeHealth(i2.getHealth() - defaultDamageValue);
                this.health += defaultDamageValue;
                updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
            }
            else {
                updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
            }
        }
    }

    //Race conditions
    public void changeHealth(int v) {
        synchronized(this){
            health = v;  
        }
         
    }

    //Race conditions
    public int getHealth() {
        synchronized(this){
            return health; 
        }   
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public void stopImmortal(){
        this.fighting = false;
    }

    public void resumeImmortal(){
        this.fighting = true;
        synchronized(immortalsPopulation){
            immortalsPopulation.notifyAll();
        }
    }

}
