/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Dennis
 */
public class CommandProcessor {

    private static final HashMap<Character, Integer> CHAR_WIDTHS = new HashMap<>();
    private static final String broadcastFormat = ChatColor.translateAlternateColorCodes('&', CrossServerChat.instance().getConfig().getString("broadcastFormat", "&6[&4Broadcast&6] &a"));
    private static final ConnectionManager cm = CrossServerChat.instance().getConnectionManager();

    public static boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (label) {
            case "cc":
                sender.sendMessage(generateHLineTitle("CrossServerChat - Status"));
                sender.sendMessage("");
                if (cm instanceof ServerConnectionManager) {
                    sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Node type: " + ChatColor.RED + "Host");
                } else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Node type: " + ChatColor.GREEN + "Client");
                }
                sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Connected to:");
                List<? extends ThreadIn> connections = cm.getConnections();
                if (connections.isEmpty()) {
                    sender.sendMessage(ChatColor.DARK_GRAY + "   - " + ChatColor.GRAY + "(None)");
                } else {
                    for (ThreadIn threadIn : cm.getConnections()) {
                        long aliveTime = threadIn.getAliveTime();
                        long minutes = (aliveTime / 60000000000L) % 60;
                        long hours = aliveTime / 3600000000000L;
                        sender.sendMessage(ChatColor.DARK_GRAY + "   - " + ChatColor.GRAY + "IP: " + ChatColor.AQUA + threadIn.getIP()
                                + ChatColor.GRAY + ", Age: " + ChatColor.AQUA + hours + "h " + minutes + "m"
                                + ChatColor.GRAY + ", RTT: " + ChatColor.AQUA + threadIn.getRoundTripTime() / 1000000 + "ms");
                    }
                }
                sender.sendMessage("");
                break;
            case "cbc":
                if (args.length == 0) {
                    sender.sendMessage("/cbc {message} - broadcasts a message to all connected servers");
                    return true;
                }
                String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
                message = broadcastFormat + message;
                cm.sendMessage(message);
                Bukkit.broadcastMessage(message);
                break;
        }

        return true;
    }

    public static String generateHLineTitle(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.DARK_GRAY).append(ChatColor.STRIKETHROUGH).append("     ");
        sb.append(ChatColor.BLUE).append(" ").append(title).append(" ");
        sb.append(ChatColor.DARK_GRAY).append(ChatColor.STRIKETHROUGH);
        for (int i = getTextWidth(ChatColor.stripColor(title)) + 21; i < 310; i += 4) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static int getTextWidth(String text) {
        int width = 0;
        for (char c : text.toCharArray()) {
            if (CHAR_WIDTHS.containsKey(c)) {
                width += CHAR_WIDTHS.get(c) + 1;
            } else {
                width += 6;
            }
        }
        return width;
    }

    static {
        CHAR_WIDTHS.put('I', 3);
        CHAR_WIDTHS.put('i', 1);
        CHAR_WIDTHS.put('k', 4);
        CHAR_WIDTHS.put('l', 2);
        CHAR_WIDTHS.put('t', 4);
        CHAR_WIDTHS.put('_', 5);
        CHAR_WIDTHS.put('-', 5);
        CHAR_WIDTHS.put(' ', 5);
        CHAR_WIDTHS.put('!', 1);
        CHAR_WIDTHS.put('@', 6);
        CHAR_WIDTHS.put('(', 4);
        CHAR_WIDTHS.put(')', 4);
        CHAR_WIDTHS.put('{', 4);
        CHAR_WIDTHS.put('}', 4);
        CHAR_WIDTHS.put('[', 3);
        CHAR_WIDTHS.put(']', 3);
        CHAR_WIDTHS.put(':', 1);
        CHAR_WIDTHS.put(';', 1);
        CHAR_WIDTHS.put('"', 3);
        CHAR_WIDTHS.put('\'', 1);
        CHAR_WIDTHS.put('<', 4);
        CHAR_WIDTHS.put('>', 4);
        CHAR_WIDTHS.put('|', 1);
        CHAR_WIDTHS.put('.', 1);
        CHAR_WIDTHS.put(',', 1);
        CHAR_WIDTHS.put(' ', 3);
    }
}
