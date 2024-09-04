/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread {

    private Queue<Integer> queue;


    public Consumer(Queue<Integer> queue) {
        this.queue = queue;
    }

    // segun el profe no usar sleep(), la estrategia seria: notify() agregando 1 a la cola si hay elemtos
    // consumidor se le debe modificar el if en el momento en que tenga la cola como 0 para que deje de buscar, mientras esto pasa se debe poner a mimir al productor con queue.wait()

    @Override
    /*
    VERSION ANTERIOR  DEL RUN
    public void run() {
        while (true) {

            if (queue.size() > 0) {
                int elem=queue.poll();
                System.out.println("Consumer consumes "+elem);
            }

        }
    }
    */
    public void run() {
            while (true) {
                synchronized (queue) {
                    // si la Cola esta vacia, el consumidor debe esperar con wait()
                    if(queue.isEmpty()) { 
                        try {
                            //System.out.println("Consumer waiting...");
                            queue.wait();
                        }catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    else{
                        //en caso de que hayan elementos en la cola debe consumir 1
                        int elem = queue.poll();
                        System.out.println("Consumer consumes " + elem);
                        // Después de consumir, notifica al productor en caso de que esté esperando
                        queue.notifyAll();
                    }

                }
            }
        }
    }




