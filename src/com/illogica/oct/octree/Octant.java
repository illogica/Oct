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

import static com.illogica.oct.octree.Octree.*;
import com.illogica.oct.states.Materials;
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
public class Octant implements Savable, Comparable {

    private static int ID = 0; //thought about long, but int should be enough

    //Physical characteristics of this node
    private Vector3f origin;
    private float edgeSize;
    private byte octantType;

    //User data contained in this node
    private int id;
    public OctantData data;
    //private int materialType;
    

    //Tree data structure stuff
    private Octant parent;
    private Octant children[];
    private byte depth; //128 levels of depth are waayyyy more than enough
    //maybe I could save some byte implementing the getDepth()
    //function as an actual recursive search for the node depth

    /**
     * Private constructor. To create an Octant object you must call
     * createOctant
     *
     * @param parent
     * @param size dimension of the cube edge
     * @param origin
     * @param depth
     * @param octantType
     */
    private Octant(Octant parent, float size, Vector3f origin, byte depth, byte octantType) {
        this.data = new OctantData(this);
        this.id = ID++;
        this.parent = parent;
        this.edgeSize = size;
        this.origin = origin;
        this.depth = depth;
        this.octantType = octantType;
        if (parent != null) {
            this.data.materialType = parent.data.materialType;// inherit material of the parent
        } else {
            this.data.materialType = Materials.MAT_AIR; //material for the root octant
        }
        System.out.println("Octant: Created node with id " + id + " and material " + data.materialType);
    }

    /**
     * Official method to call when you need to generate an Octant
     *
     * @param parent
     * @param size
     * @param origin
     * @param depth
     * @param octantType
     * @return
     */
    public static Octant createOctant(Octant parent, float size, Vector3f origin, byte depth, byte octantType) {
        Octant o = new Octant(parent, size, origin, depth, octantType);
        if (Octree.getListener() != null) {
            Octree.getListener().onOctantGenerated(o);
        }
        return o;
    }

    public boolean hasChildren() {
        return children != null;
    }

    public Octant[] getChildren() {
        return children;
    }

    public int getId() {
        return id;
    }

    public Octant getParent() {
        return parent;
    }

    public float getEdgeSize() {
        return edgeSize;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public byte getDepth() {
        return this.depth;
    }

    public byte getType() {
        return this.octantType;
    }

    /**
     * Set the material for an Octant
     *
     * @param newType
     * @return this Octant, useful for chaining
     */
    public Octant setMaterialType(int newType) {
        this.data.materialType = newType;

        if (Octree.getListener() != null) {
            Octree.getListener().onOctantMaterialChanged(this);
        }

        return this;
    }

    public int getMaterialType() {
        return data.materialType;
    }

    /**
     * Goes up to the octree until the root octant is found
     *
     * @return
     */
    public Octant getRoot() { //TODO: MAKE PRIVATE
        Octant currentNode = this.getParent();
        while (currentNode.parent != null) {
            currentNode = currentNode.getParent();
        }
        return currentNode;
    }

    public Octant getNeighbor(int side) {
        float halfSize = edgeSize / 2f;
        halfSize += 0.0001;
        switch (side) {
            case SIDE_FRONT:
                return getOctant(origin.add(Vector3f.UNIT_Z.mult(halfSize)), depth);
            case SIDE_RIGHT:
                return getOctant(origin.add(Vector3f.UNIT_X.mult(halfSize)), depth);
            case SIDE_BACK:
                return getOctant(origin.add(Vector3f.UNIT_Z.mult(-halfSize)), depth);
            case SIDE_LEFT:
                return getOctant(origin.add(Vector3f.UNIT_X.mult(-halfSize)), depth);
            case SIDE_TOP:
                return getOctant(origin.add(Vector3f.UNIT_Y.mult(halfSize)), depth);
            case SIDE_BOTTOM:
                return getOctant(origin.add(Vector3f.UNIT_Y.mult(-halfSize)), depth);
            default:
                return null;
        }
    }
    
    public boolean hasNeighbor(int side){
        byte d = this.depth;
        while(d > 0){
            Octant o = null;
            switch (side) {
                case SIDE_FRONT:
                    o = getOctant(origin.add(Vector3f.UNIT_Z.mult(edgeSize)), d);
                    break;
                case SIDE_RIGHT:
                    o = getOctant(origin.add(Vector3f.UNIT_X.mult(edgeSize)), d);
                    break;
                case SIDE_BACK:
                    o = getOctant(origin.add(Vector3f.UNIT_Z.mult(-edgeSize)), d);
                    break;
                case SIDE_LEFT:
                    o = getOctant(origin.add(Vector3f.UNIT_X.mult(-edgeSize)), d);
                    break;
                case SIDE_TOP:
                    o = getOctant(origin.add(Vector3f.UNIT_Y.mult(edgeSize)), d);
                    break;
                case SIDE_BOTTOM:
                    o = getOctant(origin.add(Vector3f.UNIT_Y.mult(-edgeSize)), d);
                    break;
                default:
                    break;
            }
            if(o != null && o.data.materialType!=Materials.MAT_AIR)
                return true;
            d--;
        }
        return false;
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
            return null;
        }

        Octant currentOctant = getRoot();

        for (byte i = 0; i < depth; i++) {
            if (currentOctant.hasChildren()) {
                byte type = Octree.getOctantTypeForPoint(currentOctant.getOrigin(), position);
                currentOctant = currentOctant.getChildren()[type - 1];
            } else {
                return null;
            }
        }
        return currentOctant;
    }

    /**
     * Removes all the children subtrees and sets the material to invisible.
     * It is not a real deletion, because the Octant must be one of the 8 children
     * of a parent. We just delete its children and set its material to an 
     * invisible one.
     * @return the current Octant
     */
    public Octant delete() {
        deleteChildren();
        if (Octree.getListener() != null) {
            Octree.getListener().onOctantDeleted(this); //remove from the scenegraph
        }
        this.setMaterialType(Materials.MAT_AIR);
        return this;
    }

    /**
     * Removes this octant's children subtrees
     */
    public void deleteChildren() {
        if (this.hasChildren()) {
            for (Octant child : children) {
                child.delete();
            }
            this.children = null;
        }
    }

    /**
     * Subdivide the octant into 8 new subOctants, set the children's material
     * as the parent material and set the parent material as invisible (air)
     *
     * @return the current Octant, useful for chaining
     */
    public Octant subdivide() {
        children = new Octant[8];
        float size = edgeSize / 4;
        children[0] = createOctant(this, edgeSize / 2, origin.add(new Vector3f(size, size, size)), (byte) (depth + 1), Octree.TYPE_1);
        children[1] = createOctant(this, edgeSize / 2, origin.add(new Vector3f(size, size, -size)), (byte) (depth + 1), Octree.TYPE_2);
        children[2] = createOctant(this, edgeSize / 2, origin.add(new Vector3f(-size, size, -size)), (byte) (depth + 1), Octree.TYPE_3);
        children[3] = createOctant(this, edgeSize / 2, origin.add(new Vector3f(-size, size, size)), (byte) (depth + 1), Octree.TYPE_4);
        children[4] = createOctant(this, edgeSize / 2, origin.add(new Vector3f(size, -size, size)), (byte) (depth + 1), Octree.TYPE_5);
        children[5] = createOctant(this, edgeSize / 2, origin.add(new Vector3f(size, -size, -size)), (byte) (depth + 1), Octree.TYPE_6);
        children[6] = createOctant(this, edgeSize / 2, origin.add(new Vector3f(-size, -size, -size)), (byte) (depth + 1), Octree.TYPE_7);
        children[7] = createOctant(this, edgeSize / 2, origin.add(new Vector3f(-size, -size, size)), (byte) (depth + 1), Octree.TYPE_8);

        this.setMaterialType(Materials.MAT_AIR); //hide "this", the parent cube
        return this;
    }

    /**
     * Make sure the provided position is within borders of the root cube
     *
     * @param position
     * @return true if the position is valid
     */
    private boolean isPositionValid(Vector3f position) {
        Octant root = getRoot();
        float edgeDiv2 = root.edgeSize / 2f;
        if (FastMath.abs(position.x) > (root.origin.x + edgeDiv2)) {
            return false;
        }
        if (FastMath.abs(position.y) > (root.origin.y + edgeDiv2)) {
            return false;
        }
        if (FastMath.abs(position.z) > (root.origin.z + edgeDiv2)) {
            return false;
        }
        return true;
    }

    @Override   //to be tested
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(id, "id", 0);
        capsule.write(depth, "depth", (byte) 0);
        capsule.write(origin, "origin", new Vector3f());
        capsule.write(edgeSize, "edgeSize", 0f);
        capsule.write(octantType, "octantType", (byte) 0);
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
        octantType = capsule.readByte("octantType", (byte) 0);
        parent = (Octant) capsule.readSavable("parent", null);
        children = (Octant[]) capsule.readSavableArray("children", null);
    }

    /**
     * <code>this</code> is lower if is deeper than t
     *
     * @param t
     * @return
     */
    @Override
    public int compareTo(Object t) {
        return ((Octant) t).depth - this.depth;
    }

    /**
     * Two octants are equal if the have the same size, same position in the
     * tree, and are of the same type
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Octant)) {
            return false;
        }

        return this.depth == ((Octant) o).getDepth()
                && this.origin.equals(((Octant) o).getOrigin())
                && this.octantType == ((Octant) o).getType();
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

    /**
     * Octinfos are lightweight objects containing information about nodes.
     * They encapsulate only the origin, depth and size of the node and are used
     * to exchange node data.
     * @return an Octinfo representation of this node
     */
    public Octinfo getOctinfo() {
        return new Octinfo(this.origin, edgeSize, depth);
    }
}
