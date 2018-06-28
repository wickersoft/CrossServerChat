/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.net.Socket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Dennis
 */
public class ThreadInClient extends ThreadIn {

    public ThreadInClient(ThreadOut threadOut, Socket socket) {
        super(threadOut, socket);
    }

    @Override
    protected void onChatReceived(String message) {
        CrossServerChat.broadcastMessage(message);
    }

    @Override
    protected void onPing(long nanos) {
        roundTripTime = nanos;
        threadOut.sendPing(nanos);
    }

    @Override
    protected boolean handshake() {
        threadOut.sendPing(0x0123456789ABCDEFL);
        return true;
    }

    @Override
    protected void linkToServerChat() {
        Bukkit.broadcast("" + ChatColor.GRAY + ChatColor.ITALIC + "[CrossServerChat: connected to " + getIP() + "]", "crossserverchat.notify");
    }

    @Override
    protected void unlinkFromServerChat() {
        Bukkit.broadcast("" + ChatColor.GRAY + ChatColor.ITALIC + "[CrossServerChat: disconnected from " + getIP() + "]", "crossserverchat.notify");
    }
}
