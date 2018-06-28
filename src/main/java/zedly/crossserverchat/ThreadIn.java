/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Dennis
 */
public abstract class ThreadIn extends Thread {

    protected final ThreadOut threadOut;
    protected final Socket socket;
    protected DataInputStream dis;
    protected long establishmentTime = 0;
    protected long roundTripTime = 0;
    private boolean alive = true;

    public ThreadIn(ThreadOut threadOut, Socket socket) {
        this.threadOut = threadOut;
        this.socket = socket;
    }

    public void run() {
        try {
            this.dis = new DataInputStream(socket.getInputStream());
            if (handshake()) {
                linkToServerChat();
            } else {
                closeConnection();
                return;
            }

            this.establishmentTime = System.nanoTime();

            while (alive) {
                int op = dis.read();
                switch (op) {
                    case 0:
                        int len = dis.readInt();
                        byte[] buf = new byte[len];
                        dis.readFully(buf);
                        String message = new String(buf, "UTF-16BE");
                        onChatReceived(message);
                        break;
                    case 0xFF:
                        long nanos = dis.readLong();
                        onPing(nanos);
                        break;
                    default:
                        closeConnection();
                        return;
                }
            }
        } catch (IOException ex) {
            closeConnection();
            //Abkack();
        }
    }

    public void stopRunning() {
        alive = false;
    }

    protected void closeConnection() {
        try {
            if (socket.isConnected()) {
                socket.close();
            }
        } catch (IOException ex) {
        }
        threadOut.stopRunning();
        unlinkFromServerChat();
    }

    public long getRoundTripTime() {
        return roundTripTime;
    }

    protected abstract void onChatReceived(String Message);

    protected abstract void onPing(long nanos);

    protected abstract boolean handshake() throws IOException;

    protected abstract void linkToServerChat();

    protected abstract void unlinkFromServerChat();

    protected String getIP() {
        return socket.getInetAddress().getHostAddress();
    }

    protected long getRTT() {
        return roundTripTime;
    }

    protected long getAliveTime() {
        return System.nanoTime() - establishmentTime;
    }

}
