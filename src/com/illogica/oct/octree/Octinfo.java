/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.octree;

import com.jme3.math.Vector3f;

/**
 * Lightweight data structure containing atomic spatial data about an Octant.
 * @author Loris
 */
public class Octinfo {
    public float originX;
    public float originY;
    public float originZ;
    public float size;
    public byte depth;
    
    public Octinfo(Vector3f origin, float size, byte depth){
        this.originX = origin.x;
        this.originY = origin.y;
        this.originZ = origin.z;
        this.size = size;
        this.depth = depth;
    }
    
    public Octinfo(float originX, float originY, float originZ, float size, byte depth){
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
        this.size = size;
        this.depth = depth;
    }
    
    public Octinfo(){
        originX = originY = originZ = size = 0f;
        depth = (byte)0;
    }

    public Vector3f origin(){
        return new Vector3f(originX, originY, originZ);
    }

    @Override
    public boolean equals(Object o) {
        if(! (o instanceof Octinfo)) return false;
        
        Octinfo of = (Octinfo)o;
        
        return (originX == of.originX
                && of.originY == of.originY
                && of.originZ == of.originZ
                && of.depth == of.depth
                && of.size == of.size);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Float.floatToIntBits(this.originX);
        hash = 53 * hash + Float.floatToIntBits(this.originY);
        hash = 53 * hash + Float.floatToIntBits(this.originZ);
        hash = 53 * hash + Float.floatToIntBits(this.size);
        hash = 53 * hash + this.depth;
        return hash;
    }

    @Override
    public String toString() {
        return "(" + originX + "," + originY + "," + originZ + "), size:" + size + ", depth:" + depth;
    }
}
