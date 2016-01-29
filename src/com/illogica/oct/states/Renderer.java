/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.illogica.oct.engine.GeometryGenerators;
import com.illogica.oct.engine.MaterialUndefinedException;
import com.illogica.oct.engine.Qube2;
import com.illogica.oct.octree.Octant;
import com.illogica.oct.octree.Octinfo;
import com.illogica.oct.octree.Octree;
import com.illogica.oct.octree.OctreeListener;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import java.util.HashMap;
import java.util.Map;

/**
 * Takes an Octree and a SimpleApplication and attaches the visible Octree cubes
 * in the SimpleApplication rootNode.
 *
 * @author Loris
 */
public class Renderer extends AbstractAppState implements OctreeListener{

    private SimpleApplication app;
    private AppStateManager stateManager;
    private Octree octree;
    private Node octantsScenegraphRoot;
    private Node selectionObjectScenegraphRoot;
    Arrow arrow; //TODO: MOVE ARROW TO THE SELECTION CONTROL
    Geometry arrowGeometry;
    
    Map<Integer,BatchNode> batchNodes; //one batch for each material
    
    @Override
    public void setOctree(Octree tree){
        this.octree = tree;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        System.out.println("Initialize RenderAppState");
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        octantsScenegraphRoot = new Node("octants root node");
        selectionObjectScenegraphRoot = new Node("selection object root node");
        this.app.getRootNode().attachChild(octantsScenegraphRoot);
        this.app.getRootNode().attachChild(selectionObjectScenegraphRoot);
        this.batchNodes = new HashMap<Integer, BatchNode>();
        
        arrow = new Arrow(Vector3f.UNIT_X);
        arrowGeometry = GeometryGenerators.putShape(arrow, ColorRGBA.Green);
        this.app.getRootNode().attachChild(arrowGeometry);
    }
    
    @Override
    public void update(float tpf){
    }
    
    public Node getTreeRoot(){return octantsScenegraphRoot;}

    @Override
    public void onOctantGenerated(Octant o) {
        int mat = o.getMaterialType();
        if(mat == Materials.MATERIAL_AIR){
            System.out.println("GENERATED: Material air, do nothing");
            //do nothing, we don't show air
        } else {
            System.out.println("GENERATED: Material " + mat);
            Spatial s = new Qube2(o, stateManager.getState(Materials.class).getDebugMaterial());// getMaterial(mat));
            s.setName("Qube" + o.getId());
            s.setUserData("Octant", o);
            
            if(!batchNodes.containsKey(mat)){
                BatchNode materialRoot = new BatchNode("Mat" + mat);
                batchNodes.put(mat, materialRoot);
                octantsScenegraphRoot.attachChild(materialRoot);
            }
            batchNodes.get(mat).attachChild(s);
            //octantsScenegraphRoot.attachChild(s);
        }
    }

    @Override
    public void onOctantDeleted(Octant o) {
        octantsScenegraphRoot.detachChildNamed("Qube" + o.getId());
    }
    
    @Override
    public void onOctantMaterialChanged(Octant o) {
        int mat = o.getMaterialType();
        
        Spatial s =  octantsScenegraphRoot.getChild("Qube" + o.getId());
        //Spatial s = batchNodes.get(mat).getChild("Qube" + o.getId());
        
        if(s==null){
            //System.out.println("Cube" + o.getId() + " not found in scenegraph, generating...");
            onOctantGenerated(o);
            return; 
        }
        if(mat ==  Materials.MATERIAL_AIR){
            System.out.println("CHANGED: to material AIR");
            
            Node toBeDeleted = null;
            //look into all BatchNodes:
            for(BatchNode b : batchNodes.values()){
                toBeDeleted = (Node)b.getChild("Qube" + o.getId());
                if(toBeDeleted!= null){
                    System.out.println("Found!!!! " + toBeDeleted);
                    break;
                }
            }
            
            int detachChild = toBeDeleted.getParent().detachChild(toBeDeleted);
            if(detachChild== -1)
                System.out.println("DETACH FAILED");
            
            int res = octantsScenegraphRoot.detachChild(s); //remove the AIR objects, no need to render them
            if(res == -1){
                System.out.println("Not found!" + s.getName());
            }
        }
        else { //apply material
            System.out.println("CHANGED: Material " + mat);
            
            ((Qube2)s).setMaterial(stateManager.getState(Materials.class).getDebugMaterial());//getMaterial(mat));
        }
    }

    /**
     * Given a CollisionResult object, calculates the Octinfo related to the
     * collision.
     * @param collisionResult
     * @return 
     */
    public Octinfo getSelectionOctinfo(CollisionResult collisionResult) {
        if(octree==null)
            return null;
        //calculate a point on the octant just inside its bounds
        Vector3f collisionPoint = new Vector3f(collisionResult.getContactPoint());
        Vector3f collisionNormal = new Vector3f(collisionResult.getContactNormal());
        collisionNormal.negateLocal().multLocal(Engine.SELECT_PRECISION); //floats are reliable up to the 6th digit
        collisionPoint.addLocal(collisionNormal);
        return octree.getOctinfo(collisionPoint, stateManager.getState(SelectionManager.class).getStep());
    }
    
    public void refreshSelection(){
        if(octree!= null){
            
            //See what object we have under the cursor
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
            
            octantsScenegraphRoot.collideWith(ray, results);
            if(results.size()>0){
                Octinfo oi = getSelectionOctinfo(results.getClosestCollision());
                stateManager.getState(SelectionManager.class).updateSelection(results.getClosestCollision(), oi);
                
                if(arrow!= null){
                    Vector3f contactNormal = new Vector3f(results.getClosestCollision().getContactNormal());
                    arrow.setArrowExtent(contactNormal.mult(0.1f));
                }
                if(arrowGeometry!=null){
                    arrowGeometry.setLocalTranslation(results.getClosestCollision().getContactPoint());
                }
                //System.out.println("Normal: " + results.getClosestCollision().getContactNormal());
                //System.out.println("Point: " + results.getClosestCollision().getContactPoint());
            }
        }
    }
}
