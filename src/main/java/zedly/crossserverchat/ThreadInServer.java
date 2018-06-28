/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Dennis
 */
public class ThreadInServer extends ThreadIn {

    private final ServerConnectionManager scm;
    private long lastSendNanos = 0;

    public ThreadInServer(ThreadOut threadOut, Socket socket, ServerConnectionManager scm) {
        super(threadOut, socket);
        this.scm = scm;
    }

    @Override
    protected void onChatReceived(String message) {
        CrossServerChat.broadcastMessage(message);
        scm.forwardMessage(message, this);
    }

    @Override
    protected void onPing(long nanos) {
        roundTripTime = System.nanoTime() - lastSendNanos;
    }

    protected void sendPing() {
        this.lastSendNanos = System.nanoTime();
        threadOut.sendPing(roundTripTime);
    }

    @Override
    protected boolean handshake() throws IOException {
        if (dis.read() != 0xFF || dis.readLong() != 0x0123456789ABCDEFL) {
            return false;
        }
        return true;
    }

    @Override
    protected void linkToServerChat() {
        scm.addConnection(this);
        Bukkit.broadcast("" + ChatColor.GRAY + ChatColor.ITALIC + "[CrossServerChat: " + getIP() + " connected]", "crossserverchat.notify");
    }

    @Override
    protected void unlinkFromServerChat() {
        if (scm.removeConnection(this)) {
            Bukkit.broadcast("" + ChatColor.GRAY + ChatColor.ITALIC + "[CrossServerChat: " + getIP() + " disconnected]", "crossserverchat.notify");
        }
    }
}
