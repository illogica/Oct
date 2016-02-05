/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.gui;

/**
 * List of the commands
 * @author Loris
 */
public interface Commands {
    
    public void clear();
    public int connect();
    public int connect(String host);
    public int connect(String host, int port);
    public void quit(); //quit the application
    public void setMaterial(int material);
    public void setMoveSpeed(float speed);
    public int start(); //starts a server
    public void stop(); //stops a server
    
}
