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

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import java.io.IOException;

/**
 * Lightweight data structure containing atomic spatial data about an Octant.
 * They encapsulate only the origin, depth and size of the node and are used
 * to exchange node data.
 * @author Loris
 */
public class Octinfo implements Savable{
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

    @Override
    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
