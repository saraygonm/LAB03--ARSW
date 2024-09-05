/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {
    public static void main(String a[]){
        CountThread primerThread = new CountThread(0,99);
        CountThread segundoThread = new CountThread(100,199);
        CountThread tercerThread = new CountThread(200, 299);
        primerThread.run();
        segundoThread.run();
        tercerThread.run();
    }
   
}
