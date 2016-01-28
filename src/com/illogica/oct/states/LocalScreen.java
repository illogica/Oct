/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.illogica.oct.gui.FancyConsole;
import com.illogica.oct.octree.Octree;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Loris
 */
public class LocalScreen extends AbstractAppState implements ScreenController{

    private Nifty nifty;
    private Screen screen;
    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private Console niftyConsole;
    private FancyConsole console;
    private Octree tree;
    
    private SelectionManager selectionManagerAppState;
    private Engine engineAppState;
    private KeysSelect keysSelectAppstate;
    private Renderer rendererAppState;
    private Materials materials;
    private Lighting lighting;
    
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
                
        // setup the flyby camera
        this.app.getFlyByCamera().setDragToRotate(false);
        this.app.getFlyByCamera().setMoveSpeed(10f);
        
        //get a reference to the console
        niftyConsole = screen.findNiftyControl("console", Console.class);
        
        //Takes focus from Nifty gui
        screen.getFocusHandler().resetFocusElements();
        
        //create the FancyConsole
        console = new FancyConsole(niftyConsole, nifty, this.app);

        lighting = new Lighting();
        materials = new Materials();
        selectionManagerAppState = new SelectionManager();
        engineAppState = new Engine();
        keysSelectAppstate = new KeysSelect();
        rendererAppState= new Renderer();
        
        //rootNode.attachChild(GeometryGenerators.quadBasedCube(null));
        /*Octant o = new Octant(null, 2.0f, Vector3f.ZERO, (byte)1, (byte)1);
        Material newMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md");
        rootNode.attachChild(new Qube(o, newMat));*/
        
        this.app.getStateManager().attach(lighting);
        this.app.getStateManager().attach(materials);
        this.app.getStateManager().attach(selectionManagerAppState);
        this.app.getStateManager().attach(keysSelectAppstate);
        this.app.getStateManager().attach(rendererAppState);
        this.app.getStateManager().attach(engineAppState);
        
    }
    
    public Screen getScreen(){
        return screen;
    }
    
    public Console getNiftyConsole(){
        return niftyConsole;
    }
    
    public SimpleApplication getApp(){
        return this.app;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        
    }

    @Override
    public void onEndScreen() {
    }
   
}
