package edu.eci.arsw.threads;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class ThreadCheck extends Thread{

    private HostBlacklistsDataSourceFacade skds;
    private int rangeLimitA;
    private int rangeLimitB;
    private String ipAdress;
    private LinkedList<Integer> indexes;
    private int serversChecked;

    private boolean end=false;

    private Object lock;

    private boolean paused = false;

    private int ocurrences;

    public void pauseThread(){
        paused = true;
    }

    public void resumeThread(){
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean hasEnd() {
        return end;
    }
    public void endThread(){
        end = true;
    }

    public ThreadCheck(HostBlacklistsDataSourceFacade skds, int rangeLimitA, int rangeLimitB, String ipAdress, Object lock){
        setServerList(skds, rangeLimitA, rangeLimitB);
        setIpToLookFor(ipAdress);
        this.indexes = new LinkedList<>();
        this.serversChecked = 0;
        this.lock = lock;

    }

    public static void main(String[] args){
        Object lock = new Object();
        ThreadCheck t = new ThreadCheck(HostBlacklistsDataSourceFacade.getInstance(), 0000,9000, "202.24.34.55",lock);
        t.start();
        //System.out.println("No. de servers:");
        //System.out.println(HostBlacklistsDataSourceFacade.getInstance().getRegisteredServersCount());
    }
    
    @Override
    public void run(){
        while(!end) {
            if(!paused) {
                countOccurrences();
                //System.out.println("No. de ocurrencias:");
                //System.out.println(getOccurrences());
            }else {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setServerList(HostBlacklistsDataSourceFacade skds, int rangeLimitA, int rangeLimitB){
        this.skds = skds;
        this.rangeLimitA = rangeLimitA;
        this.rangeLimitB = rangeLimitB;
    }

    public void setIpToLookFor(String ipAdress){
        this.ipAdress = ipAdress;
    }

    public void countOccurrences(){
        ocurrences = 0;
        for(int i = rangeLimitA; i < rangeLimitB; i++){
            this.serversChecked++;
            if(skds.isInBlackListServer(i, ipAdress)){
                ocurrences++;
                indexes.add(i);
            }
        }
    }

    public int getOccurrences(){
        return this.ocurrences;
    }

    public int getServersChecked() {
        return serversChecked;
    }

    public LinkedList<Integer> getIndexes(){
        return this.indexes;
    }

}
