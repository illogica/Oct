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
package com.illogica.oct.engine;

import com.illogica.oct.octree.Octant;
import com.illogica.oct.octree.Octree;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.TangentBinormalGenerator;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * A Cube made of custom quads
 *
 * @author Loris
 */
public class Qube2 extends Node {

    Vector3f v0, v1, v2, v3, v4, v5, v6, v7;
    QuadV4 quad[];
    Geometry geom[];
    Octant o;
    Material mat;
    Mesh m;
            

    public Qube2() {
    }

    public Qube2(Octant o, Material mat) {
        updateGeometry(o, mat);
    }

    public void scaleTextureCoordinates(float n) {
        Vector2f scaleVec = new Vector2f(n, n);
        
        m.scaleTextureCoordinates(scaleVec);
    }

    private void updateGeometry(Octant o, Material mat) {
        this.detachAllChildren();
        quad = new QuadV4[6];
        //geom = new Geometry[6];

        //see if n.hasVertices(), then do this:
        this.o = o;
        this.mat = mat;

        float s = o.getEdgeSize() / 2;
        v0 = o.getOrigin().add(new Vector3f(s, s, s));
        v1 = o.getOrigin().add(new Vector3f(s, s, -s));
        v2 = o.getOrigin().add(new Vector3f(-s, s, -s));
        v3 = o.getOrigin().add(new Vector3f(-s, s, s));
        v4 = o.getOrigin().add(new Vector3f(s, -s, s));
        v5 = o.getOrigin().add(new Vector3f(s, -s, -s));
        v6 = o.getOrigin().add(new Vector3f(-s, -s, -s));
        v7 = o.getOrigin().add(new Vector3f(-s, -s, s));

        mergeSides();
        //scaleTextureCoordinates(FastMath.pow(2f, Octree.getUnitDepth()));
    }

    private QuadV4 quadBySide(int side) {
        switch (side) {
            case 0:
                return new QuadV4(v7, v4, v0, v3, o, side);
            case 1:
                return new QuadV4(v4, v5, v1, v0, o, side);
            case 2:
                return new QuadV4(v5, v6, v2, v1, o, side);
            case 3:
                return new QuadV4(v6, v7, v3, v2, o, side);
            case 4:
                return new QuadV4(v3, v0, v1, v2, o, side);
            case 5:
                return new QuadV4(v6, v5, v4, v7, o, side);
            default:
                throw new IllegalStateException("Wrong side number");
        }
    }
    
    private void mergeSides(){
        
        int sidesCount = 6;
        boolean hiddenSides[] = new boolean[]{false,false,false,false,false,false};
        
        if(o.hasNeighbor(Octree.SIDE_FRONT)){
            sidesCount--;
            hiddenSides[0] = true;
        }
        if(o.hasNeighbor(Octree.SIDE_LEFT)){
            sidesCount--;
            hiddenSides[1] = true;
        }
        if(o.hasNeighbor(Octree.SIDE_BACK)){
            sidesCount--;
            hiddenSides[2] = true;
        }
        if(o.hasNeighbor(Octree.SIDE_RIGHT)){
            sidesCount--;
            hiddenSides[3] = true;
        }
        if(o.hasNeighbor(Octree.SIDE_TOP)){
            sidesCount--;
            hiddenSides[4] = true;
        }
        if(o.hasNeighbor(Octree.SIDE_BOTTOM)){
            sidesCount--;
            hiddenSides[5] = true;
        }
        
        FloatBuffer position = FloatBuffer.allocate(12*sidesCount);
        FloatBuffer tex = FloatBuffer.allocate(8*sidesCount);
        FloatBuffer normal = FloatBuffer.allocate(12*sidesCount);
        ShortBuffer indices = ShortBuffer.allocate(6*sidesCount);
        
        int cnt = 0;
        for (int i = 0; i < 6; i++) {
            if(!hiddenSides[i]){
                quad[cnt] = quadBySide(i);
                //System.out.println(quad[i].getBufferList());
                position.put(quad[cnt].positionArray);
                tex.put(quad[cnt].texCoordsArray);
                normal.put(quad[cnt].normalArray);
            
                for(int j=0; j<quad[cnt].indexArray.length; j++){
                    short val = quad[cnt].indexArray[j];
                    val += 4*cnt;
                    indices.put(val);
                }
                cnt++;
            }
            
            //geom[i] = new Geometry("Q" + i, quad[i]);
            //geom[i].setMaterial(mat);
            //this.attachChild(geom[i]);
        }
        
        m = new Mesh();
        m.setBuffer(VertexBuffer.Type.Position, 3, position);
        m.setBuffer(VertexBuffer.Type.TexCoord, 2, tex);
        m.setBuffer(VertexBuffer.Type.Normal, 3, normal);
        m.setBuffer(VertexBuffer.Type.Index, 3, indices);
        TangentBinormalGenerator.generate(m);
        m.updateBound();
        
        Geometry g = new Geometry("Qube", m);
        g.setMaterial(mat);
        if(mat.getName().startsWith("Transparent")){
            g.setQueueBucket(Bucket.Transparent);
        }
        this.attachChild(g);
    }
}
