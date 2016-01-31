/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.illogica.oct.gui.CustomPicture;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Loris
 */
public class Hud extends AbstractAppState {
    
    SimpleApplication app;
    AppStateManager sm;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        this.app = (SimpleApplication)app;
        this.sm = stateManager;
        
        loadCrosshairs();
    }
    
    private void loadCrosshairs(){
        
        //Material mat = new Material(app.getAssetManager(), "Materials/Gui/Crosshair/Crosshair.j3md");
        //mat.setTexture("Texture", app.getAssetManager().loadTexture("Textures/Crosshair/cross_normal.png"));
        CustomPicture pic = new CustomPicture("Crosshair");
        pic.setImage(app.getAssetManager(), "Textures/Crosshair/cross_normal.png", true);
        pic.setWidth(32);
        pic.setHeight(32);
        pic.setPosition((Display.getWidth()/2)-16, (Display.getHeight()/2)-16);
        app.getGuiNode().attachChild(pic);
    }
}
