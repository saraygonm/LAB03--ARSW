/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 *
 * @author hcadavid - Milton Gutierrez - Saray Mendivelso
 */
public class HostBlackListsValidator {
    private static final int BLACK_LIST_ALARM_COUNT=5;
    private int numberOfIps = 80000;
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    private AtomicInteger ocurrencesCount = new AtomicInteger();
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     * @throws InterruptedException 
     */
    public List<Integer> checkHost(String ipaddress, int N){
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        ArrayList<BlackListThread> threads = new ArrayList<>();
        int[][] rangeOfIpsForEachThread = distributorOfIps(N);
        int checkedListsCount = 0;

        for(int i = 0; i < N; i++){
            threads.add(new BlackListThread(rangeOfIpsForEachThread[i][0], rangeOfIpsForEachThread[i][1], ipaddress, ocurrencesCount));
        }
        for(BlackListThread b: threads){
            b.start();
        }

        for(BlackListThread b: threads){
            try {
                b.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            blackListOcurrences.addAll(b.getBlackListOcurrences());
            checkedListsCount += b.getCheckedLists();
        }
        if (ocurrencesCount.get() == BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    private int[][] distributorOfIps(int N){
        if(N == 1){
            return new int[][]{{1, numberOfIps}};
        }
        else{
            int[] numberOfIpsForTheThreads = new int [N]; //Create the array which contains the number of ips each thread will check
            int div = numberOfIps / N;  
            int mod = numberOfIps % N;
            //Calculate the number of ips for each thread to check
            for(int i = 0; i < N ; i++){
                if(i+1 == N){
                    numberOfIpsForTheThreads[i]=div+mod;
                }else{
                    numberOfIpsForTheThreads[i]=div;
                }
            } 
            //Calculate the range of ips for each thread
            int[][] rangeOfIpsForEachThread = new int[N][2];
            int start = 1;
            int end = numberOfIpsForTheThreads[0];
            rangeOfIpsForEachThread[0] = new int[]{start, end};
            for(int i = 1; i < N; i++){
                start += numberOfIpsForTheThreads[i-1];
                end += numberOfIpsForTheThreads[i];
                rangeOfIpsForEachThread[i] = new int[]{start, end}; 
            }    
            return rangeOfIpsForEachThread;
        }
    }
}
    
    
    
    
