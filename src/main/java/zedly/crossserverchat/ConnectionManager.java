/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.crossserverchat;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Dennis
 */
public abstract class ConnectionManager extends Thread {

    public abstract void sendMessage(String message);

    public abstract List<? extends ThreadIn> getConnections();
    
}
