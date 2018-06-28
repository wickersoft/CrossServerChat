/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Dennis
 */
public class ServerConnectionManager extends ConnectionManager {

    private final ArrayList<ThreadInServer> connections = new ArrayList<>();

    @Override
    public void sendMessage(String message) {
        for (ThreadIn in : connections) {
            in.threadOut.sendMessage(message);
        }
    }

    public void forwardMessage(String message, ThreadIn skip) {
        for (ThreadIn in : connections) {
            if (in != skip) {
                in.threadOut.sendMessage(message);
            }
        }
    }

    public void run() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CrossServerChat.instance(), () -> {
            spreadPings();
        }, 20, 20);

        FileConfiguration fc = CrossServerChat.instance().getConfig();
        String ip = fc.getString("ip");
        int port = fc.getInt("port");

        try {
            ServerSocket ss = new ServerSocket(port);

            while (!isInterrupted()) {
                Socket s = ss.accept();

                s.setSoTimeout(5000);

                OutputStream os = s.getOutputStream();

                ThreadOut threadOut = new ThreadOut(os);
                ThreadInServer threadIn = new ThreadInServer(threadOut, s, this);

                threadOut.start();
                threadIn.start();
            }
        } catch (IOException ex) {
            return;
        }
    }

    public synchronized void spreadPings() {
        for (ThreadInServer in : connections) {
            in.sendPing();
        }
    }

    public synchronized void addConnection(ThreadInServer threadIn) {
        connections.add(threadIn);
    }

    public synchronized boolean removeConnection(ThreadInServer threadIn) {
        return connections.remove(threadIn);
    }

    @Override
    public List<? extends ThreadIn> getConnections() {
        return connections;
    }
}
