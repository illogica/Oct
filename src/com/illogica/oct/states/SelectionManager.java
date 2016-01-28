/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * Selection Geometry is the geometry used to decorate an existing object in
 * order to see where the focus is. Selected object is the actual scenegraph
 * object having focus.
 *
 * @author Loris
 */
public class SelectionManager extends AbstractAppState {
    public static final int MAX_DEPTH = 16; //maximum allowed octree and drawing step depth
    
    private byte step = 1; // equals to depth, but with a slightly different meaning
    
    private SimpleApplication app;
    private AppStateManager stateManager;

    private Geometry oldGeometryUnderCursor;
    private Geometry geometryUnderCursor;

    private Node selectionNode;
    private SelectionControl selectionControl;
    private Node quad; //the white outline for faces

    private CollisionResult lastCollisionResult;
    private Octinfo lastSelectionOctinfo;
    private Vector3f lastNormal;


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Initialize SelectionManagerAppState");

        this.app = (SimpleApplication)app;
        this.stateManager = stateManager;
        
        oldGeometryUnderCursor = new Geometry("dummy geometry");
        geometryUnderCursor = new Geometry("dummy geometry");
        lastSelectionOctinfo = new Octinfo();
        lastNormal = new Vector3f(0f, 0f, 0f);

        this.selectionNode = new Node("Selection");
        this.selectionNode.attachChild(GeometryGenerators.wireFrameQuad());
        this.selectionControl = new SelectionControl();
        this.selectionNode.addControl(selectionControl);
        this.app.getRootNode().attachChild(selectionNode);
    }

    /**
     * Tracks the current selection of the user. Is called by the renderer.
     *
     * @param collisionResult
     * @param oi the fictitious octant where the selection should be
     */
    public void setObject(CollisionResult collisionResult, Octinfo oi) {
        if (collisionResult != null) {
            this.lastCollisionResult = collisionResult;

            this.oldGeometryUnderCursor = geometryUnderCursor;
            this.geometryUnderCursor = collisionResult.getGeometry();

            selectionControl.updateData(collisionResult, oi);

            lastSelectionOctinfo = oi;
            lastNormal = lastCollisionResult.getContactNormal();
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

    public boolean compareVector3f(Vector3f v1, Vector3f v2) {
        return ((v1.x == v2.x) && (v1.y == v2.y) && (v1.z == v2.z));
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

    public void increaseStep() {
        if (step < MAX_DEPTH) {
            this.step++;
        }
        System.out.println("New step: " + step);
    }

    public void DecreaseStep() {
        if (step > (byte) 0) {
            this.step--;
        }
        System.out.println("New step: " + step);
    }
}
