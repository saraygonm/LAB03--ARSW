/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class Producer extends Thread {

    private Queue<Integer> queue = null;

    private int dataSeed = 0;
    private Random rand=null;
    private final long stockLimit;

    public Producer(Queue<Integer> queue, long stockLimit) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
        this.stockLimit=stockLimit;
    }

    @Override
    public void run() {
        while (true) {
            dataSeed = dataSeed + rand.nextInt(100);
            synchronized (queue) {
                //Se verifica que se puedan añadir elementos a la cola que respeten el limite.
                if(queue.size() < stockLimit){
                    queue.add(dataSeed);
                    System.out.println("Producer added " + dataSeed);
                }
                else{
                    System.out.println("Can't produce: sotckLimit reached");
                }
                queue.notifyAll();
                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }
    }
}
