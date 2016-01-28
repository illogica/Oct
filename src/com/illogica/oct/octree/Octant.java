/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.octree;

import static com.illogica.oct.octree.Octree.*;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.io.IOException;

/**
 * An Octree node, also called Octant
 * 
 * Sub-cubes position relative to the parent origin, in the Jme coordinates:
 * 
 * CUBE X   Y   Z
 * 1    +   +   +
 * 2    +   +   -
 * 3    -   +   -
 * 4    -   +   +  
 * 5    +   -   +
 * 6    +   -   -
 * 7    -   -   -
 * 8    -   -   +
 * Cube 0 is the parent.
 * 
 * Each cube has 6 faces:
 * A    front (XY)
 * B    right (YZ)
 * C    back (XY)
 * D    left (YZ)
 * E    top (XZ)
 * F    bottom (XZ)
 *                                     2________1
 * Each cube has 8 corners (vertices) /   E    /|
 * 0    front top right             3/_______0/ |
 * 1    back top right              |        | B|
 * 2    back top left               |   A    |  |
 * 3    front top left              |        | / 5
 * 4    front bottom right          |________|/
 * 5    back bottom right          7         4
 * 6    back bottom left
 * 7    front bottom left
 * 
 * I don't really like the name "Octant", but "Node" was already taken from
 * JmonkeyEngine
 * @author Loris
 */
public class Octant implements Savable, Comparable{

    private static int ID = 0; //thought about long, but int should be enough
    
    private int id;
    private byte depth; //128 levels of depth are waayyyy more than enough
    
    private Vector3f origin;
    private float edgeSize;
    private byte octantType;
    private int materialType;
        
    private Octant parent;
    private Octant children[];
    
    /**
     * Private constructor. To create an Octant object you must call
     * createOctant
     * @param parent
     * @param size dimension of the cube edge
     * @param origin
     * @param depth
     * @param octantType 
     */
    public Octant(Octant parent, float size, Vector3f origin, byte depth, byte octantType){
        this.id = ID++;
        this.parent = parent;
        this.edgeSize = size;
        this.origin = origin;
        this.depth = depth;
        this.octantType = octantType;
        if(parent!= null){
            this.materialType = parent.materialType;// inherit material of the parent
        } else {
            this.materialType = Octree.MATERIAL_AIR; //material for the root octant
        }
        System.out.println("Created node with id " + id);
    }
    
    /**
     * Official method to call when you need to generate an Octant
     * @param parent
     * @param size
     * @param origin
     * @param depth
     * @param octantType
     * @return 
     */
    public static Octant createOctant(Octant parent, float size, Vector3f origin, byte depth, byte octantType){
        Octant o = new Octant(parent, size, origin, depth, octantType);
        if(Octree.getListener() != null)
            Octree.getListener().onOctantGenerated(o);
        return o;
    }
    
    /**
     * I don't like this. Better creating a new Octinfo object with just
     * the informations needed.
     * @return 
     */
    /*public static Octant createDummyOctant(){
        Octant o = new Octant(null, 0f, new Vector3f(0f,0f,0f), (byte)0, (byte)0);
        return o;
    }*/
    
    /**
     * Subdivide the octant into 8 new subOctants, set the children's material
     * as the parent material
     * @return the current Octant, useful for chaining
     */
    public Octant subdivide(){
        children = new Octant[8];
        float size = edgeSize / 4;
        children[0] = createOctant(this, edgeSize/2, origin.add(new Vector3f(size, size, size)), (byte) (depth+1), Octree.TYPE_1);
        children[1] = createOctant(this, edgeSize/2, origin.add(new Vector3f(size, size, -size)), (byte) (depth+1), Octree.TYPE_2);
        children[2] = createOctant(this, edgeSize/2, origin.add(new Vector3f(-size, size, -size)), (byte) (depth+1), Octree.TYPE_3);
        children[3] = createOctant(this, edgeSize/2, origin.add(new Vector3f(-size, size, size)), (byte) (depth+1), Octree.TYPE_4);
        children[4] = createOctant(this, edgeSize/2, origin.add(new Vector3f(size, -size, size)), (byte) (depth+1), Octree.TYPE_5);
        children[5] = createOctant(this, edgeSize/2, origin.add(new Vector3f(size, -size, -size)), (byte) (depth+1), Octree.TYPE_6);
        children[6] = createOctant(this, edgeSize/2, origin.add(new Vector3f(-size, -size, -size)), (byte) (depth+1), Octree.TYPE_7);
        children[7] = createOctant(this, edgeSize/2, origin.add(new Vector3f(-size, -size, size)), (byte) (depth+1), Octree.TYPE_8);
        
        this.setMaterialType(Octree.MATERIAL_AIR); //hide "this", the parent cube
        return this;
    }
    
    /**
     * Removes all the children subtree and sets the material to invisible
     * @return 
     */
    public Octant delete(){
        if(this.hasChildren()){
            for (Octant child : children) {
                child.delete();
            }
            this.children = null;
        }
        if(Octree.getListener() != null)
            Octree.getListener().onOctantDeleted(this); //remove from the scenegraph
        this.setMaterialType(MATERIAL_AIR);
        return this;
    }
    
    /**
     * Removes this octant's children subtree
     */
    public void deleteChildren(){
        if(this.hasChildren()){
            for (Octant child : children) {
                child.delete();
            }
            this.children = null;
        }
    }
    
    /**
     * Removes children from an Octant
     */
    public void resetChildren(){
        this.children = null; //PERFORMANCE HINT: recycle octants in an OctantPool
    }
    
    public boolean hasChildren(){ return children!=null;}
    public Octant[] getChildren(){return children;}
    public int getId(){return id;}
    public Octant getParent(){return parent;}
    public float getEdgeSize(){return edgeSize;}
    public Vector3f getOrigin(){return origin;}
    public byte getDepth(){return this.depth;}
    public byte getType(){return this.octantType;}
    
    /**
     * Set the material for an Octant
     * @param newType
     * @return this Octant, useful for chaining
     */
    public Octant setMaterialType(int newType){
        this.materialType = newType;
        
        if(Octree.getListener() != null)
                Octree.getListener().onOctantMaterialChanged(this);
        
        return this;
    }

    public int getMaterialType(){return materialType;}
    
    /*public void updateNeighborVisibility(){
        //cycle through this octant faces and hide the neighbours' faces
        Octant neighbour;
        
        neighbour = getNeighbor(SIDE_FRONT);
        if(neighbour != null) neighbour.setSideVisibility(SIDE_BACK, (materialType == MATERIAL_AIR));
        
        neighbour = getNeighbor(SIDE_RIGHT);
        if(neighbour != null) neighbour.setSideVisibility(SIDE_LEFT, (materialType == MATERIAL_AIR));
        
        neighbour = getNeighbor(SIDE_BACK);
        if(neighbour != null) neighbour.setSideVisibility(SIDE_FRONT, (materialType == MATERIAL_AIR));
        
        neighbour = getNeighbor(SIDE_LEFT);
        if(neighbour != null) neighbour.setSideVisibility(SIDE_RIGHT, (materialType == MATERIAL_AIR));
        
        neighbour = getNeighbor(SIDE_TOP);
        if(neighbour != null) neighbour.setSideVisibility(SIDE_BOTTOM, (materialType == MATERIAL_AIR));
        
        neighbour = getNeighbor(SIDE_BOTTOM);
        if(neighbour != null) neighbour.setSideVisibility(SIDE_TOP, (materialType == MATERIAL_AIR));
            
        /*if(visibleQuads[SIDE_FRONT] == false && !(materialType==MATERIAL_AIR)){ getNeighbor(SIDE_FRONT).setSideVisibility(SIDE_BACK, false); }
        if(visibleQuads[SIDE_RIGHT] == false && !(materialType==MATERIAL_AIR)){ getNeighbor(SIDE_RIGHT).setSideVisibility(SIDE_LEFT, false); }
        if(visibleQuads[SIDE_BACK] == false && !(materialType==MATERIAL_AIR)){ getNeighbor(SIDE_BACK).setSideVisibility(SIDE_FRONT, false); }
        if(visibleQuads[SIDE_LEFT] == false && !(materialType==MATERIAL_AIR)){ getNeighbor(SIDE_LEFT).setSideVisibility(SIDE_RIGHT, false); }
        if(visibleQuads[SIDE_TOP] == false && !(materialType==MATERIAL_AIR)){ getNeighbor(SIDE_TOP).setSideVisibility(SIDE_BOTTOM, false); }
        if(visibleQuads[SIDE_BOTTOM] == false && !(materialType==MATERIAL_AIR)){ getNeighbor(SIDE_BOTTOM).setSideVisibility(SIDE_TOP, false); }*/
    //}
    
    /**
     * Goes up to the octree until the root octant is found
     * @return 
     */
    public Octant getRoot(){ //TODO: PRIVATE
        Octant currentNode = this.getParent();
        while(currentNode.parent != null){
            currentNode = currentNode.getParent();
        }
        return currentNode;
    }
    
    public Octant getNeighbor(int side){
        float halfSize = edgeSize/2f;
        halfSize += 0.0001;
        switch(side){
            case SIDE_FRONT: return getOctant(origin.add(Vector3f.UNIT_Z.mult(halfSize)), depth);
            case SIDE_RIGHT: return getOctant(origin.add(Vector3f.UNIT_X.mult(halfSize)), depth);
            case SIDE_BACK: return getOctant(origin.add(Vector3f.UNIT_Z.mult(-halfSize)), depth);
            case SIDE_LEFT: return getOctant(origin.add(Vector3f.UNIT_X.mult(-halfSize)), depth);
            case SIDE_TOP: return getOctant(origin.add(Vector3f.UNIT_Y.mult(halfSize)), depth);
            case SIDE_BOTTOM: return getOctant(origin.add(Vector3f.UNIT_Y.mult(-halfSize)), depth);
            default: return null;
        }
    }
    
    /**
     * Locate an existing octant in the octree or null if the octant do not exist
     * @param position
     * @param depth
     * @return 
     */
    public Octant getOctant(Vector3f position, byte depth){
        if(!isPositionValid(position))
            return null;
        
        Octant currentOctant = getRoot();
        
        for (byte i = 0; i < depth; i++) {
            if(currentOctant.hasChildren()){
                byte type = Octree.getOctantTypeForPoint(currentOctant.getOrigin(), position);
                currentOctant = currentOctant.getChildren()[type - 1];
            } else {
                return null;
            }
        }
        return currentOctant;
    }
    
    private boolean isPositionValid(Vector3f position){
        Octant root = getRoot();
        float edgeDiv2 = root.edgeSize/2f;
        if(FastMath.abs(position.x) > (root.origin.x + edgeDiv2))
            return false;
        if(FastMath.abs(position.y) > (root.origin.y + edgeDiv2))
            return false;
        if(FastMath.abs(position.z) > (root.origin.z + edgeDiv2))
            return false;
        return true;
    }
    
    @Override   //to be tested
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(id, "id", 0);
        capsule.write(depth, "depth", (byte)0);
        capsule.write(origin, "origin", new Vector3f());
        capsule.write(edgeSize, "edgeSize", 0f);
        capsule.write(octantType, "octantType", (byte)0);
        capsule.write(materialType, "materialType", 0);
        capsule.write(parent, "parent", null);
        capsule.write(children, "children", null);
    }

    @Override   //to be tested
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        id = capsule.readInt("id", 0);
        depth = capsule.readByte("depth", depth);
        origin = (Vector3f) capsule.readSavable("origin", new Vector3f());
        edgeSize = capsule.readFloat("edgeSize", 0f);
        octantType = capsule.readByte("octantType", (byte)0);
        materialType = capsule.readInt("materialType", 0);
        parent = (Octant) capsule.readSavable("parent", null);
        children = (Octant[]) capsule.readSavableArray("children", null);
    }

    /**
     * <code>this</code> is lower if is deepest than t
     * @param t
     * @return 
     */
    @Override
    public int compareTo(Object t) {
        return ((Octant)t).depth - this.depth;
    }

    /**
     * Two octants are equal if the have the same size,
     * same position in the tree, and are of the same type
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if(o==null) { return false; }
        if (!(o instanceof Octant)) { return false; }
        
       return this.depth == ((Octant)o).getDepth()
               && this.origin.equals(((Octant)o).getOrigin())
               && this.octantType == ((Octant)o).getType();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.depth;
        hash = 59 * hash + (this.origin != null ? this.origin.hashCode() : 0);
        hash = 59 * hash + this.octantType;
        return hash;
    }
    
    @Override
    public String toString() {
        String s = "Id:" + id + ", (" + origin.x + "," + origin.y + "," + origin.z + "), size:" + edgeSize + ", depth:" + depth;
        return s;
    }
    
    public Octinfo getOctinfo(){
        return new Octinfo(this.origin, edgeSize, depth);
    }
}
