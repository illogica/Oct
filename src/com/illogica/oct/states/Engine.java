/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.illogica.oct.octree.Octant;
import com.illogica.oct.octree.Octinfo;
import com.illogica.oct.octree.Octree;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.List;

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
        this.octree = Octree.createSimpleTree(stateManager.getState(Renderer.class), (byte)1);
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
        sm.getState(SelectionManager.class).stepIncrease();
        onRefreshSelection();
    }

    public void decreaseStep() {
        sm.getState(SelectionManager.class).stepDecrease();
        onRefreshSelection();
    }
    
    /**
     * Apply the given material to the current selection.
     * If nothing is selected, just change the current material.
     * @param meterialId the material Id
     */
    public void setMaterial(int meterialId){
        sm.getState(Materials.class).setCurrentMaterialId(meterialId);
        List<Octinfo> ois = sm.getState(SelectionManager.class).selectionBoxesOctinfos();
        for(Octinfo oi : ois){
            octree.getOctant(oi).setMaterialType(meterialId);
        }
        sm.getState(Hud.class).resetTemplateMaterial();
    }

    public void onExtrudeOctantRequest() {

        CollisionResult cs = sm.getState(SelectionManager.class).getLatestCollisionResult();
        Octinfo oi = sm.getState(SelectionManager.class).getLastSelectionOctinfo();
        Vector3f position = oi.origin().add(cs.getContactNormal().mult(oi.size));
        Octinfo o = new Octinfo(position, oi.size, oi.depth);
        
        //Create the octant and set its material
        Octant newOctant = octree.createOctant(o);
        newOctant.setMaterialType(sm.getState(Materials.class).getCurrentMaterialId());
        
        onRefreshSelection();
    }

    public void onRefreshSelection() {
        sm.getState(Renderer.class).refreshSelection();
    }

    public void onMouseSelect() {
        Geometry g = sm.getState(SelectionManager.class).getObjectUnderCursor();
        
        Octinfo selection = sm.getState(SelectionManager.class).getLastSelectionOctinfo();
        
        sm.getState(SelectionManager.class).selectionBoxesAdd(selection);
        
        Octant o = octree.getOctant(selection);
        if( o != null ) {
            System.out.println("Selected octant: " + o);
            System.out.println("Front neighbor is " + o.getNeighbor(0));
            System.out.println("Right neighbor is " + o.getNeighbor(1));
            System.out.println("Back neighbor is " + o.getNeighbor(2));
            System.out.println("Left neighbor is " + o.getNeighbor(3));
            System.out.println("Top neighbor is " + o.getNeighbor(4));
            System.out.println("Bottom neighbor is " + o.getNeighbor(5));
            System.out.println("hasNeighbor FRONT: " + o.hasNeighbor(Octree.SIDE_FRONT));
            System.out.println("hasNeighbor RIGHT: " + o.hasNeighbor(Octree.SIDE_RIGHT));
            System.out.println("hasNeighbor BACK: " + o.hasNeighbor(Octree.SIDE_BACK));
            System.out.println("hasNeighbor LEFT: " + o.hasNeighbor(Octree.SIDE_LEFT));
            System.out.println("hasNeighbor TOP: " + o.hasNeighbor(Octree.SIDE_TOP));
            System.out.println("hasNeighbor BOTTOM: " + o.hasNeighbor(Octree.SIDE_BOTTOM));
            
        } else {
            System.out.println("Selected nothing.");
        }
    }
}
