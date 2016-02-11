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
package com.illogica.oct.octree;

import com.illogica.oct.states.Materials;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * The Octree class works like a container for the root node of the tree and has
 * all the methods needed to modify the tree.
 * @author Loris
 */
public class Octree implements OctreeEditor{
    
    public static final byte TYPE_ROOT = 0; //root node
    public static final byte TYPE_1 = 1; //front up right
    public static final byte TYPE_2 = 2; //back up right
    public static final byte TYPE_3 = 3; //back up left
    public static final byte TYPE_4 = 4; //front up right
    public static final byte TYPE_5 = 5; //front bottom right
    public static final byte TYPE_6 = 6; //back bottom right
    public static final byte TYPE_7 = 7; //back bottom left
    public static final byte TYPE_8 = 8; //front bottom right
    
    public static final int X_POS = 0;
    public static final int X_NEG = 1;
    public static final int Y_POS = 2;
    public static final int Y_NEG = 3;
    public static final int Z_POS = 4;
    public static final int Z_NEG = 5;
    
    public static final int SIDE_FRONT = 0; //front face
    public static final int SIDE_RIGHT = 1; //right face
    public static final int SIDE_BACK = 2; //back face
    public static final int SIDE_LEFT = 3; //left face
    public static final int SIDE_TOP = 4; //top face
    public static final int SIDE_BOTTOM = 5; //bottom face

    //The characteristics of an octree
    private Octant root;

    private static OctreeListener listener;

    public Octant getRoot() {
        return root;
    }

    /**
     * Return an Octinfo to be used as a pointer. This is the
     * octant that is shown as a selector object.
     * The main difference between with the getOctant() method is that there
     * might not be and Octant in the requested position, but there will always
     * be an Octinfo.
     *
     * @param position
     * @param depth
     * @return
     */
    public Octinfo getOctinfo(Vector3f position, byte depth) {
        if (!isPositionValid(position)) {
            System.out.println("Invalid position in Octree.getSelectionOctant()");
            return null;
        }
        
        Vector3f origin = new Vector3f(root.getOrigin());
        float size = root.getEdgeSize();
        
        for(int i=0; i<depth; i++){
            int type = getOctantTypeForPoint(origin, position);
            size /= 2f; //edge size of the hypothetical cube
            float halfSize = size / 2f; //distance from cube center to cube surface
            switch(type){
                case TYPE_1: origin.addLocal(halfSize, halfSize, halfSize); break;
                case TYPE_2: origin.addLocal(halfSize, halfSize, -halfSize); break;
                case TYPE_3: origin.addLocal(-halfSize, halfSize, -halfSize); break;
                case TYPE_4: origin.addLocal(-halfSize, halfSize, halfSize); break;
                case TYPE_5: origin.addLocal(halfSize, -halfSize, halfSize); break;
                case TYPE_6: origin.addLocal(halfSize, -halfSize, -halfSize); break;
                case TYPE_7: origin.addLocal(-halfSize, -halfSize, -halfSize); break;
                case TYPE_8: origin.addLocal(-halfSize, -halfSize, halfSize); break;
            }
        }

        //we return a fictitious Octant, we just use it as a container for
        //the origin coordinates and the side length.
        return new Octinfo(origin, size, depth);
    }
   
    /**
     * Locate an existing octant in the octree or null if the octant do not
     * exist
     *
     * @param position
     * @param depth
     * @return
     */
    public Octant getOctant(Vector3f position, byte depth) {
        if (!isPositionValid(position)) {
            System.out.println("Invalid position in Octree.getOctant()");
            return null;
        }

        Octant currentOctant = getRoot();

        for (int i = 0; i < depth; i++) {
            if (currentOctant.hasChildren()) {
                int type = getOctantTypeForPoint(currentOctant.getOrigin(), position);
                currentOctant = currentOctant.getChildren()[type - 1];
            } else {
                return null;
            }
        }
        return currentOctant;
    }

    /**
     * Check if a point is inside the boundaries of the root cube
     *
     * @param position the point to check
     * @return true if the point is in a valid position (inside the root cube)
     */
    private boolean isPositionValid(Vector3f position) {
        float edgeDiv2 = root.getEdgeSize() / 2f;
        if (FastMath.abs(position.x) > (root.getOrigin().x + edgeDiv2)) {
            return false;
        }
        if (FastMath.abs(position.y) > (root.getOrigin().y + edgeDiv2)) {
            return false;
        }
        if (FastMath.abs(position.z) > (root.getOrigin().z + edgeDiv2)) {
            return false;
        }
        return true;
    }

    /**
     * Check to which octant a point belongs
     * @param origin the local origin
     * @param point the point to be checked
     * @return an int representing the octant where the point belongs
     */
    public static byte getOctantTypeForPoint(Vector3f origin, Vector3f point){
        Vector3f d = point.subtract(origin);
        if(d.x > 0){
            if(d.y > 0){
                if(d.z > 0){
                    return TYPE_1;
                } else {
                    return TYPE_2;
                }
            } else {
                if(d.z > 0){
                    return TYPE_5;
                } else {
                    return TYPE_6;
                }
            }
        } else {
            if(d.y > 0){
                if(d.z > 0){
                    return TYPE_4;
                } else {
                    return TYPE_3;
                }
            } else {
                if(d.z > 0){
                    return TYPE_8;
                } else {
                    return TYPE_7;
                }
            }
        }
    }
    
    ////                STATIC METHODS START HERE
    //Using static variables in this case helps saving memory because some vars
    //are shared between all the octants, like the octant listener.
    
    public static OctreeListener getListener(){
        return listener;
    }
    
    public static Octree createTemplateOctree(OctreeListener listener){
        Octree tree = new Octree();
        
        //First of all, register the listener
        Octree.listener = listener;
        listener.setOctree(tree);
        
        //Now generate the floor geometry:
        // four air cubes on the top
        // four concrete cubes on the bottom
        tree.generateRoot(1f, Vector3f.ZERO);
        tree.getRoot().subdivide();
        Octant children[] = tree.getRoot().getChildren();
        children[0].setMaterialType(Materials.MAT_AIR);
        children[1].setMaterialType(Materials.MAT_AIR);
        children[2].setMaterialType(Materials.MAT_AIR);
        children[3].setMaterialType(Materials.MAT_AIR);
        children[4].setMaterialType(Materials.MAT_RANDOM_COLOR);
        children[5].setMaterialType(Materials.MAT_RANDOM_COLOR);
        children[6].setMaterialType(Materials.MAT_RANDOM_COLOR);
        children[7].setMaterialType(Materials.MAT_RANDOM_COLOR);
        
        return tree;
    }
    
    public static Octree createTemplateOctree(OctreeListener listener, int size){
        Octree tree = new Octree();
        
        //First of all, register the listener
        Octree.listener = listener;
        listener.setOctree(tree);
        
        //Now generate the floor geometry:
        tree.generateRoot(FastMath.pow(2f, size) , Vector3f.ZERO);
        
        List<Octant> childrenToSubdivide = new ArrayList<Octant>();
        List<Octant> childrenSubdivided = new ArrayList<Octant>();
        
        tree.getRoot().subdivide();
        Octant rootChildren[] = tree.getRoot().getChildren();
        childrenToSubdivide.add(rootChildren[4]);
        childrenToSubdivide.add(rootChildren[5]);
        childrenToSubdivide.add(rootChildren[6]);
        childrenToSubdivide.add(rootChildren[7]);
        
        for(int i=0; i<size-1; i++){
            
            while(!childrenToSubdivide.isEmpty()){
                Octant o = childrenToSubdivide.remove(0);
                o.subdivide();
                for(Octant c : o.getChildren())
                    if(c.getType()<5){
                        childrenSubdivided.add(c);
                    }
            }
            
            while(!childrenSubdivided.isEmpty()){
                childrenToSubdivide.add(childrenSubdivided.remove(0));
            }
        }
        
        for(Octant t: childrenToSubdivide){
            t.setMaterialType(Materials.MAT_RANDOM_COLOR);
        }
       
        return tree;
    }
    
    public static Octree createSimpleTree(OctreeListener listener, byte size){
        Octree tree = new Octree();
        
        //First of all, register the listener
        Octree.listener = listener;
        listener.setOctree(tree);
        
        //Now generate the geometry:
        tree.generateRoot(FastMath.pow(2f, size) , Vector3f.ZERO)
                .setMaterialType(Materials.MAT_WIREFRAME);
                //.subdivide();
        
        return tree;
    }
    
    ////////////// OVERRIDDEN METHODS
    
    @Override
    public Octant getOctant(Octinfo o){
        if(!isPositionValid(o.origin())){
            System.out.println("Invalid position in Octree.getOctant()");
            return null;
        }
        
        Octant currentOctant = getRoot();
        
        for (int i = 0; i < o.depth; i++) {
            if(currentOctant.hasChildren()){
                int type = getOctantTypeForPoint(currentOctant.getOrigin(), o.origin());    
                currentOctant = currentOctant.getChildren()[type - 1];
            } else {
                return null;
            }
        }
        return currentOctant;
    }
    
    /**
     * Generates the root node of the Octree
     * @param size better be a float power of 2
     * @param origin 
     * @return the root Octant 
     */
    @Override
    public Octant generateRoot(float size, Vector3f origin){
        root = Octant.createOctant(null, size, origin, (byte)0, TYPE_ROOT);
        return root;
    }

    /**
     * Delete an octant from the tree
     * @param o the octant to be deleted
     * @return the deleted octant
     */
    @Override
    public Octant deleteOctant(Octant o) {
        return o.delete();
    }

    /**
     * Creates an octant in the specified position.
     * @param o the information about the octant to be generated
     * @return the octant generated
     */
    @Override
    public Octant createOctant(Octinfo o) {
        
        if(!isPositionValid(o.origin())){
            System.out.println("Cannot extrude outside root bounds");
            return null;
        }
        
        Octant currentOctant = root;
        
        while(currentOctant.getDepth() < o.depth){
            //subdivide the current octant if not already divided
            if(!currentOctant.hasChildren())
                currentOctant.subdivide();
            
            //get the octant type relative to the root origin
            byte octantType = getOctantTypeForPoint(currentOctant.getOrigin(), o.origin());
            currentOctant = currentOctant.getChildren()[octantType - (byte)1];
        }
        
        //currentOctant.setMaterialType(materialId);

        if(currentOctant.hasChildren())
            currentOctant.deleteChildren();
        return currentOctant;
    }

    @Override
    public Octant changeOctantMaterial(Octant o, int materialId) {
        return o.setMaterialType(materialId);
    }

    @Override
    public Octant subdivideOctant(Octant o) {
        return o.subdivide();
    }

    @Override
    public byte getOctreeUnitDepth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
