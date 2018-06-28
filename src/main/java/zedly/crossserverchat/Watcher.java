/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Dennis
 */
public class Watcher implements Listener {
    
    private static final Watcher instance = new Watcher();
    private final ConnectionManager connectionManager = CrossServerChat.instance().getConnectionManager();
    
    public static Watcher instance() {
        return instance;
    }
    
    private Watcher() {
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent evt) {
        if(!evt.isCancelled()) {
            connectionManager.sendMessage(String.format(evt.getFormat(), evt.getPlayer().getDisplayName(), evt.getMessage()));
        }
    }
    
}
