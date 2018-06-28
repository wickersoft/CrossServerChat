/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Dennis
 */
public class ClientConnectionManager extends ConnectionManager {

    private ThreadInClient threadIn = null;
    private ThreadOut threadOut = null;

    public void run() {

        FileConfiguration fc = CrossServerChat.instance().getConfig();
        String ip = fc.getString("ip");
        int port = fc.getInt("port");
        Socket s = null;
        try {
            while (true) {
                try {
                    // Establish connection
                    s = new Socket();
                    s.connect(new InetSocketAddress(ip, port), 5000);
                    s.setSoTimeout(5000);

                    OutputStream os = s.getOutputStream();

                    threadOut = new ThreadOut(os);
                    threadIn = new ThreadInClient(threadOut, s);

                    threadOut.start();
                    threadIn.start();

                    // Wait while connection is alive
                    threadIn.join();
                    threadOut.join();

                    // Clean up
                    s.close();
                    this.threadIn = null;
                    this.threadOut = null;
                } catch (IOException ex) {
                    try {
                        if (s != null && s.isConnected()) {
                            s.close();
                        }
                    } catch (IOException ex1) {
                    }
                    sleep(5000);
                }
            }
        } catch (InterruptedException ex) {
        }
    }

    @Override
    public void sendMessage(String message) {
        if (threadOut != null) {
            threadOut.sendMessage(message);
        }
    }

    public List<? extends ThreadIn> getConnections() {
        LinkedList<ThreadInClient> list = new LinkedList<>();
        if (threadIn != null) {
            list.add(threadIn);
        }
        return list;
    }
}
