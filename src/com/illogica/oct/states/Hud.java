/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.illogica.oct.engine.GeometryGenerators;
import com.illogica.oct.gui.CustomPicture;
import com.illogica.oct.gui.FancyConsole;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    Geometry currentMaterialBox;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        this.app = (SimpleApplication)app;
        this.sm = stateManager;
        
        loadCrosshairs();
        
        //Initialize Nifty Gui
        Logger.getLogger("").setLevel(Level.SEVERE); //Nifty is quite chatty, let's shut it
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay (
                this.app.getAssetManager(),
                this.app.getInputManager(),
                this.app.getAudioRenderer(),
                this.app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        this.app.getGuiViewPort().addProcessor(niftyDisplay);
        nifty.fromXml("Interface/LocalScreen.xml", "start", this);
        
        //un-focus Nifty gui, bind() must have been already called
        screen.getFocusHandler().resetFocusElements();
        
        
        //TEST
        float boxHeight = Display.getHeight() / 5f;
        currentMaterialBox = GeometryGenerators.boxByMat(sm.getState(Materials.class).getMaterial(Materials.MAT_STONE_WALL));
        this.app.getGuiNode().attachChild(currentMaterialBox);
        currentMaterialBox.setLocalTranslation( boxHeight - 10f, Display.getHeight() - boxHeight - 10f, 0f);
        currentMaterialBox.rotate(0.1f, 0f, 0f);
        currentMaterialBox.setLocalScale(boxHeight);
        
        /** A white, directional light source */ 
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(1f, -1f, -1f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        this.app.getGuiNode().addLight(sun); 
        
        /** A white ambient light source. */ 
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        this.app.getGuiNode().addLight(ambient); 
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        currentMaterialBox.rotate(0f, tpf/10f, 0f);
        
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

    public void focusToConsole(){
        niftyConsole.getTextField().setFocus();
    }
    
    public void unfocusConsole(){
        screen.getFocusHandler().resetFocusElements();
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        
        //get a reference to the console
        niftyConsole = screen.findNiftyControl("console", Console.class);
        
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
