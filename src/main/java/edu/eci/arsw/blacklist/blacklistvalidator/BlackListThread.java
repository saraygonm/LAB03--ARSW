package edu.eci.arsw.blacklistvalidator;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlackListThread extends Thread{
    private static final int BLACK_LIST_ALARM_COUNT=5; //direccion registrada 5 veces
    private int start;
    private int end;
    private int checkedListsCount;
    private String ipAddress;
    private LinkedList<Integer> blackListOcurrences=new LinkedList<>();
    private AtomicInteger ocurrencesCount;

    @Override
    public void run() {
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        for (int i = this.start ; i <= this.end && ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT ;i++){
    
            this.checkedListsCount++; 

            if (skds.isInBlackListServer(i, ipAddress)){
                
                blackListOcurrences.add(i);
                
                ocurrencesCount.incrementAndGet();
            }
        }
    }  

    /**
     * Creates an instances of a BlackListThread, defining the range of the lists and the ipAddress for the BlackListThread.
     * @param start
     * @param end
     * @param ipAddress
     */
    public BlackListThread(int start, int end, String ipAddress, AtomicInteger ocurrencesCount){
        this.start = start;
        this.end = end;
        this.ipAddress = ipAddress;
        this.ocurrencesCount = ocurrencesCount;
        this.checkedListsCount = 0;
    }

    /**
     * Returns the number of lists checked
     * @return checkedListsCount
     */
    public int getCheckedLists() {
        return this.checkedListsCount;
    }


    /**
     * Returns a linkedlist which contains the number of the list in which the ipaddress was found
     * @return
     */
    public LinkedList<Integer> getBlackListOcurrences(){
        return this.blackListOcurrences;
    }
}


    
