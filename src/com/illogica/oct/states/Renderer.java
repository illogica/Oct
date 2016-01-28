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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;

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
        switch(mat){
            case Octree.MATERIAL_AIR:
                //we don't show air
                return; 
            case Octree.MATERIAL_RANDOM_COLOR:
                //Material newMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md");
                Spatial s = new Qube2(o, stateManager.getState(Materials.class).getDebugMaterial());//GeometryGenerators.getRandomColorCube(o);
                //((Qube2)s).scaleTextureCoordinates(FastMath.pow(2f, octree.getUnitDepth() - o.getDepth()));
                s.setName("Qube" + o.getId());
                s.setUserData("Octant", o);
                /*int length = */octantsScenegraphRoot.attachChild(s);
                //GeometryBatchFactory.optimize(octantsScenegraphRoot);
                break;
            default: 
                throw new MaterialUndefinedException("Unknown material: " + mat);
        }
    }

    @Override
    public void onOctantDeleted(Octant o) {
        octantsScenegraphRoot.detachChildNamed("Qube" + o.getId());
    }
    
    @Override
    public void onOctantMaterialChanged(Octant o) {
        Spatial s = octantsScenegraphRoot.getChild("Qube" + o.getId());
        if(s==null){
            //System.out.println("Cube" + o.getId() + " not found in scenegraph, generating...");
            onOctantGenerated(o);
            return; 
        }
        int mat = o.getMaterialType();
        switch(mat){
            case Octree.MATERIAL_AIR:
                octantsScenegraphRoot.detachChild(s); //remove the AIR objects, no need to render them
                //System.out.println("Detached Cube" + o.getId());
                return; 
            case Octree.MATERIAL_RANDOM_COLOR:
                //Material newMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                //newMat.setColor("Color", ColorRGBA.randomColor());
                //Material newMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md");
                ((Qube2)s).setMaterial(stateManager.getState(Materials.class).getDebugMaterial());
                //((Qube2)s).scaleTextureCoordinates( 1f / o.getDepth());
                //GeometryBatchFactory.optimize(octantsScenegraphRoot);
                break;
            default: 
                throw new MaterialUndefinedException("Unknown material: " + mat);
        }
    }

    
    public Octinfo getSelectionOctinfo(CollisionResult collisionResult) {
        if(octree==null)
            return null;
        //calculate a point on the octant just inside its bounds
        Vector3f collisionPoint = new Vector3f(collisionResult.getContactPoint());
        Vector3f collisionNormal = new Vector3f(collisionResult.getContactNormal());
        collisionNormal.negateLocal().multLocal(0.001f); //floats are reliable up to the 6th digit
        collisionPoint.addLocal(collisionNormal);
        return octree.getSelectionOctinfo(collisionPoint, stateManager.getState(SelectionManager.class).getStep());
    }
    
    public void refreshSelection(){
        if(octree!= null){
            
            //See what object we have under the cursor
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
            //Collide only with displayed Octants
            octantsScenegraphRoot.collideWith(ray, results);
            if(results.size()>0){
                Octinfo oi = getSelectionOctinfo(results.getClosestCollision());
                stateManager.getState(SelectionManager.class).setObject(results.getClosestCollision(), oi);
                
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
