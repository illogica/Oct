/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author Loris
 */
public class Lighting extends AbstractAppState {

    private AppStateManager stateManager;
    private SimpleApplication app;
    DirectionalLight sun;
    AmbientLight ambient;
    
    public DirectionalLight getSun(){
        return this.sun;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        this.app = (SimpleApplication) app;
        
        /** A white, directional light source */ 
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        this.app.getRootNode().addLight(sun); 
        
        /** A white ambient light source. */ 
        ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        this.app.getRootNode().addLight(ambient); 
        
        
    }
}
