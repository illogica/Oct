/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.illogica.oct.gui.CustomPicture;
import com.illogica.oct.gui.FancyConsole;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Loris
 */
public class Hud extends AbstractAppState implements ScreenController{
    
    SimpleApplication app;
    AppStateManager sm;
    
    private Nifty nifty;
    private Screen screen;
    private Console niftyConsole; //the gui component
    private FancyConsole console; //our implementation

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        this.app = (SimpleApplication)app;
        this.sm = stateManager;
        
        loadCrosshairs();
        
        //Initialize Nifty Gui
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                this.app.getAssetManager(),
                this.app.getInputManager(),
                this.app.getAudioRenderer(),
                this.app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        this.app.getGuiViewPort().addProcessor(niftyDisplay);
        nifty.fromXml("Interface/LocalScreen.xml", "start", this);
        //nifty.setDebugOptionPanelColors(true);   
        
        
        //create the FancyConsole
        
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
        
    public Screen getScreen(){
        return screen;
    }
        
    public Console getNiftyConsole(){
        return niftyConsole;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        
        //get a reference to the console
        niftyConsole = screen.findNiftyControl("console", Console.class);
        //Takes focus from Nifty gui
        screen.getFocusHandler().resetFocusElements();
        console = new FancyConsole(niftyConsole, nifty, this.app);
        niftyConsole.getTextField().setHeight(new SizeValue(10 + "px"));
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

}
