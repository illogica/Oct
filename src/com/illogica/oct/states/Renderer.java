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
            Spatial s = new Qube2(o, stateManager.getState(Materials.class).getCurrentMaterial());
            s.setName("Qube" + o.getId());
            s.setUserData("Octant", o);
            
            if(!batchNodes.containsKey(mat)){
                BatchNode materialRoot = new BatchNode("Mat" + mat);
                batchNodes.put(mat, materialRoot);
                octantsScenegraphRoot.attachChild(materialRoot);
            }
            batchNodes.get(mat).attachChild(s);
            batchNodes.get(mat).batch();
        }
    }

    @Override
    public void onOctantDeleted(Octant o) {
        octantsScenegraphRoot.detachChildNamed("Qube" + o.getId());
    }
    
    @Override
    public void onOctantMaterialChanged(Octant o) {
        
        //find the node among the BatchNodes
        Node toBeEdited = null;
        for(BatchNode b: batchNodes.values()){
            toBeEdited = (Node)b.getChild("Qube" + o.getId());
            
            if(toBeEdited != null){
                int detachChild = toBeEdited.getParent().detachChild(toBeEdited);
                    if(detachChild== -1){
                } else {
                        
                    //TODO: this is a huge bottleneck
                    b.batch();   //with batchnodes, you need to call this at every detach()
                }
                onOctantGenerated(o);
                return;
            }
        }
        
        if(toBeEdited == null){
            onOctantGenerated(o);
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
            }
        }
    }
}
