/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 *
 * @author Dennis
 */
public class ThreadOut extends Thread {

    private final LinkedList<String> messageQueue = new LinkedList<>();
    private final DataOutputStream dos;
    private long pingId = 0;
    private boolean alive = true;

    public ThreadOut(OutputStream os) {
        this.dos = new DataOutputStream(os);
    }

    public void run() {
        try {
            while (true) {
                synchronized (this) {
                    wait();
                }

                if (!alive) {
                    return;
                }
                
                if(messageQueue.isEmpty()) {
                    dos.write(0xFF);
                    dos.writeLong(pingId);
                    continue;
                }

                while (!messageQueue.isEmpty()) {
                    String message = messageQueue.remove();
                    int len = message.length() * 2;
                    dos.write(0x00);
                    dos.writeInt(len);
                    dos.write(message.getBytes("UTF-16BE"));
                }
            }

        } catch (InterruptedException | IOException ex) {
        }
    }

    public synchronized void sendMessage(String message) {
        messageQueue.add(message);
        notify();
    }

    public synchronized void sendPing(long ping) {
        pingId = ping;
        notify();
    }

    public synchronized void stopRunning() {
        alive = false;
        notify();
    }
}
