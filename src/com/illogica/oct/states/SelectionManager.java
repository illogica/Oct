/*
 * Copyright (c) 2016, Illogica - Loris Pederiva
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.illogica.oct.states;

import com.illogica.oct.engine.GeometryGenerators;
import com.illogica.oct.engine.SelectionControl;
import com.illogica.oct.octree.Octinfo;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 * Selection Geometry is the geometry used to decorate an existing object in
 * order to see where the focus is. Selected object is the actual scenegraph
 * object having focus.
 *
 * @author Loris
 */
public class SelectionManager extends AbstractAppState {
    public static final int MAX_DEPTH = 14; //maximum allowed octree and drawing step depth
    
    private byte step = 1; // equals to depth, but with a slightly different meaning
    
    private SimpleApplication app;
    private AppStateManager sm;

    private Geometry geometryUnderCursor;

    private Node selectionNode;
    private Node selectionBoxes; //the selection boxes to attach to the scenegraph
    private SelectionControl selectionControl;

    private CollisionResult lastCollisionResult;
    private Octinfo lastSelectionOctinfo;
    
    //private List<Octinfo> selectionBoxesOi; // a list of Octinfo representing the selection boxes


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Initialize SelectionManagerAppState");

        this.app = (SimpleApplication)app;
        this.sm = stateManager;
        
        geometryUnderCursor = new Geometry("dummy geometry");
        lastSelectionOctinfo = new Octinfo();

        //init the selection node
        this.selectionNode = new Node("Selection cursor");
        this.selectionNode.attachChild(GeometryGenerators.wireFrameQuad());
        this.selectionControl = new SelectionControl();
        this.selectionNode.addControl(selectionControl);
        this.app.getRootNode().attachChild(selectionNode);
        
        //init the selection boxes node
        this.selectionBoxes = new Node("Selection boxes");
        this.app.getRootNode().attachChild(selectionBoxes);
    }

    /**
     * Tracks the current selection of the user. Is called by the renderer.
     *
     * @param collisionResult
     * @param oi the fictitious octant where the selection should be
     */
    public void updateSelection(CollisionResult collisionResult, Octinfo oi) {
        if (collisionResult != null) {
            this.lastCollisionResult = collisionResult;
            this.geometryUnderCursor = collisionResult.getGeometry();

            selectionControl.updateData(collisionResult, oi);

            lastSelectionOctinfo = oi;
        }
    }

    public CollisionResult getLatestCollisionResult() {
        return this.lastCollisionResult;
    }

    public Geometry getObjectUnderCursor() {
        return geometryUnderCursor;
    }

    public Octinfo getLastSelectionOctinfo() {
        return lastSelectionOctinfo;
    }
    
    public void selectionBoxesClear(){
        selectionBoxes.detachAllChildren();
    }
    
    public void selectionBoxesAdd(Octinfo oi){
        if(selectionBoxes.getChild(oi.toString()) == null)
            selectionBoxes.attachChild(GeometryGenerators.getCubeByOctinfo(oi, sm.getState(Materials.class).getSelectionBoxMaterial()));
    }
    
    /**
     * Returns a list of Octinfos from all the currently selection boxes
     * @return 
     */
    public List<Octinfo> selectionBoxesOctinfos(){
        List<Octinfo> ois = new ArrayList<Octinfo>();
        for(Spatial s: selectionBoxes.getChildren()){
            ois.add((Octinfo) s.getUserData("Octinfo"));
        }
        return ois;
    }
    
    public byte getStep() {
        return step;
    }
    
    public void setStep(byte step) {
        if (step > (byte) 0 && step <= MAX_DEPTH) {
            this.step = step;
        }
        System.out.println("New step size: " + step);
    }

    public void stepIncrease() {
        if (step < MAX_DEPTH) {
            app.getFlyByCamera().setMoveSpeed(app.getFlyByCamera().getMoveSpeed()/2f);
            this.step++;
        }
        System.out.println("New step: " + step);
    }

    public void stepDecrease() {
        if (step > (byte) 0) {
            app.getFlyByCamera().setMoveSpeed(app.getFlyByCamera().getMoveSpeed()*2f);
            this.step--;
        }
        System.out.println("New step: " + step);
    }
}
