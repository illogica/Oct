/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.illogica.oct.octree.Octant;
import com.illogica.oct.octree.Octinfo;
import com.illogica.oct.octree.Octree;
import static com.illogica.oct.octree.Octree.getOctantTypeForPoint;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 *
 * @author Loris
 */
public class Engine extends AbstractAppState {
    
    public static final float SELECT_PRECISION = 0.0001f;

    private SimpleApplication app;
    private AppStateManager sm;
    private Octree octree;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        System.out.println("Initialize EngineAppState");

        this.app = (SimpleApplication) app;
        this.sm = stateManager;
        //this.octree = Octree.createTemplateOctree(stateManager.getState(Renderer.class), 0);
        this.octree = Octree.createSimpleTree(stateManager.getState(Renderer.class), (byte)2);
    }

    @Override
    public void update(float tpf) {
        sm.getState(Lighting.class).getSun().setDirection(app.getCamera().getDirection());
    }

    public Octree getOctree() { return octree; }

    public void onSubdivideOctantRequest() {
        Geometry g = sm.getState(SelectionManager.class).getObjectUnderCursor();
        Octant o = g.getUserData("Octant");
        o.subdivide();
    }

    //To be deleted
    public void onDeleteOctantRequestOld() {

        Octinfo selInfo = sm.getState(SelectionManager.class).getLastSelectionOctinfo();
        Octant octant = octree.getOctant(selInfo);
        
        if (octant == null) {
            System.out.println("Engine.java 60: octant is null, using selection under cursor");
            octant = sm.getState(SelectionManager.class).getObjectUnderCursor().getParent().getUserData("Octant");
            //return;
        }
        
        if (octant == null){
            System.out.println("Engine.java 66: octant is definitely null, exiting.");
            return;
        }

        //if the octant we want to delete is a smaller portion of a bigger one
        //we need to subdivide the bigger one into smaller ones
        if (octant.getDepth() < sm.getState(SelectionManager.class).getStep()) {
            //CollisionResult cs = sm.getState(SelectionManager.class).getLatestCollisionResult();
            Vector3f position = selInfo.origin();//.subtract(cs.getContactNormal().mult(selInfo.size));
            Octinfo o = new Octinfo(position, selInfo.size, selInfo.depth);

            while (octant.getDepth() < selInfo.depth) {
                //subdivide the current octant if not already divided
                if (!octant.hasChildren()) {
                    octant.subdivide();
                }

                //get the octant type relative to the root origin
                byte octantType = getOctantTypeForPoint(octant.getOrigin(), o.origin());
                //System.out.println("Origin: " + octant.getOrigin() + ", position: " + o.origin());
                //System.out.println("octant to be generated is type " + octantType);
                octant = octant.getChildren()[octantType - (byte) 1];
            }
        }
        octree.deleteOctant(octant);
        onRefreshSelection();
    }

    public void onDeleteOctantRequest() {
        Octinfo selInfo = sm.getState(SelectionManager.class).getLastSelectionOctinfo();
        Octant octant = octree.getOctant(selInfo);

        //If the Octant is small and does not exist yet, we create its tree
        if (octant == null) {
            octant = octree.createOctant(selInfo);
        }
        
        octree.deleteOctant(octant);
        onRefreshSelection();
    }

    public void increaseStep() {
        sm.getState(SelectionManager.class).increaseStep();
        onRefreshSelection();
    }

    public void decreaseStep() {
        sm.getState(SelectionManager.class).DecreaseStep();
        onRefreshSelection();
    }

    public void onExtrudeOctantRequest() {

        CollisionResult cs = sm.getState(SelectionManager.class).getLatestCollisionResult();
        Octinfo oi = sm.getState(SelectionManager.class).getLastSelectionOctinfo();
        Vector3f position = oi.origin().add(cs.getContactNormal().mult(oi.size));
        Octinfo o = new Octinfo(position, oi.size, oi.depth);
        octree.createOctant(o);

        onRefreshSelection();
    }

    public void onRefreshSelection() {
        sm.getState(Renderer.class).refreshSelection();
    }

    public void onMouseSelect() {
        Geometry g = sm.getState(SelectionManager.class).getObjectUnderCursor();
        
        Octinfo selection = sm.getState(SelectionManager.class).getLastSelectionOctinfo();
        Octant o = octree.getOctant(selection);
        if( o != null ) {
            System.out.println("Selected octant: " + o);
            System.out.println("Front neighbor is " + o.getNeighbor(0));
            System.out.println("Right neighbor is " + o.getNeighbor(1));
            System.out.println("Back neighbor is " + o.getNeighbor(2));
            System.out.println("Left neighbor is " + o.getNeighbor(3));
            System.out.println("Top neighbor is " + o.getNeighbor(4));
            System.out.println("Bottom neighbor is " + o.getNeighbor(5));
        } else {
            System.out.println("Selected nothing.");
        }
    }
}
