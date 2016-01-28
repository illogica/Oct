/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.server;

import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Loris
 */
public class Server implements ServerInterface {
    private static int id = 0;
    com.jme3.network.Server _server;
    
    public static final String NAME = "Cooptree Server";
    public static final int VERSION = 1;
    public static final int PORT = 5110;
    public static final int UDP_PORT = 5110;
    
    @Override
    public int start(){
        try {
            _server = Network.createServer(NAME, VERSION, PORT, UDP_PORT);
            _server.start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return id++;
    }
    
    public void stop(){
        for(HostedConnection conn : _server.getConnections()){
            conn.close("Server closing");
        }   
        //_server.removeConnectionListener(this);
        //_server.removeMessageListener(this);
        _server.close();
    }
}
