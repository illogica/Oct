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
        this.octree = Octree.createSimpleTree(stateManager.getState(Renderer.class), (byte)3);
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
            Octant o = octree.getOctant(oi);
            if(o!=null)
                o.setMaterialType(meterialId);
            else
                System.out.println("Can't apply material, non existent octant for " + oi);
        }
    }

    public void onExtrudeOctantRequest() {

        CollisionResult cs = sm.getState(SelectionManager.class).getLatestCollisionResult();
        Octinfo oi = sm.getState(SelectionManager.class).getLastSelectionOctinfo();
        Vector3f position = oi.origin().add(cs.getContactNormal().mult(oi.size));
        Octinfo o = new Octinfo(position, oi.size, oi.depth);
        
        //Create the octant and set its material
        Octant newOctant = octree.createOctant(o);
        if(newOctant!=null){
            newOctant.setMaterialType(sm.getState(Materials.class).getCurrentMaterialId());
        }
        
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
