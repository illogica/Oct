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
import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Loris
 */
public class StartScreenState extends AbstractAppState implements ScreenController {

    private Nifty nifty;
    private Screen screen;
    private SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;

    public String getProgramTitle() {
        return "Cooperative Octree Editor";
    }

    public void startGame(String nextScreen) {
        LocalScreen lss = new LocalScreen();
        app.getStateManager().attach(lss);
        app.getStateManager().detach(this);
                
        nifty.addXml("Interface/LocalScreen.xml");
        nifty.gotoScreen("localScreen");  // switch to another screen
    }

    public void quitGame() {
        app.stop();
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        // disable the fly cam
        this.app.getFlyByCamera().setDragToRotate(true);
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
