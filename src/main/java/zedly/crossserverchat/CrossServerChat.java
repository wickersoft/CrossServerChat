/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Dennis
 */
public class CrossServerChat extends JavaPlugin {

    private static final ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private static CrossServerChat instance;
    private static ConnectionManager connectionManager;

    public static CrossServerChat instance() {
        return instance;
    }

    public void onLoad() {
        instance = this;
    }

    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            while (!messageQueue.isEmpty()) {
                Bukkit.broadcastMessage(messageQueue.remove());
            }
        }, 1, 1);

        if (getConfig().getBoolean("isHost")) {
            connectionManager = new ServerConnectionManager();
        } else {

            connectionManager = new ClientConnectionManager();
        }
        connectionManager.start();
        Bukkit.getPluginManager().registerEvents(Watcher.instance(), this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return CommandProcessor.onCommand(sender, command, label, args);
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static void broadcastMessage(String message) {
        messageQueue.add(message);
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
