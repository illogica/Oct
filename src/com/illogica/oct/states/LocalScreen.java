/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.illogica.oct.engine.GeometryGenerators;
import com.illogica.oct.octree.Octree;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Loris
 */
public class LocalScreen extends AbstractAppState{


    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;

    private Octree tree;
    
    private SelectionManager selectionManagerAppState;
    private Engine engineAppState;
    private KeysSelect keysSelectAppstate;
    private Renderer rendererAppState;
    private Materials materials;
    private Lighting lighting;
    private Hud hud;
    
    
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
        this.app.getCamera().setFrustumPerspective(45f, (float) this.app.getCamera().getWidth() / this.app.getCamera().getHeight(), 0.001f, 1000f);

        lighting = new Lighting();
        materials = new Materials();
        selectionManagerAppState = new SelectionManager();
        engineAppState = new Engine();
        keysSelectAppstate = new KeysSelect();
        rendererAppState= new Renderer();
        hud = new Hud();
        
        //rootNode.attachChild(GeometryGenerators.quadBasedCube(null));
        /*Octant o = new Octant(null, 2.0f, Vector3f.ZERO, (byte)1, (byte)1);
        Material newMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md");
        rootNode.attachChild(new Qube(o, newMat));*/
        
        this.app.getStateManager().attach(lighting);
        this.app.getStateManager().attach(materials);
        this.app.getStateManager().attach(selectionManagerAppState);
        this.app.getStateManager().attach(rendererAppState);
        this.app.getStateManager().attach(engineAppState);
        this.app.getStateManager().attach(hud);
        this.app.getStateManager().attach(keysSelectAppstate);
        //stateManager.attach(new VideoRecorderAppState()); //start recording
        
    }

    public SimpleApplication getApp(){
        return this.app;
    }
}
