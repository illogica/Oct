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
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 *
 * @author Loris
 */
public class QuadV4 {
    
    public float positionArray[];
    public float texCoordsArray[];
    public float normalArray[];
    public short indexArray[];
    
    private float tileSize;
    private float rootSize;
    float shiftX = 0f;
    float shiftY = 0f;
    float shiftZ = 0f;
    int side;

    /**
     * Serialization only. Do not use.
     */
    public QuadV4(){
    }

    public QuadV4(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Octant o, int side){
        updateGeometry(v0, v1, v2, v3, o, side);
    }
    
    public QuadV4(Vector3f vertices[], Octant o, int side, int unitDepth){
        updateGeometry(vertices[0], vertices[1], vertices[2], vertices[3], o, side);
    }

    private void updateGeometry(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Octant o, int side) {
    
        Vector3f n0 = (v1.subtract(v0).cross(v3.subtract(v0))).normalize();
        Vector3f n1 = (v2.subtract(v1).cross(v0.subtract(v1))).normalize();
        Vector3f n2 = (v3.subtract(v2).cross(v1.subtract(v2))).normalize();
        Vector3f n3 = (v0.subtract(v3).cross(v2.subtract(v3))).normalize();
        
        positionArray = new float[]{
            v0.x, v0.y, v0.z
                , v1.x, v1.y, v1.z
                , v2.x, v2.y, v2.z
                , v3.x, v3.y, v3.z };
        
        //Calculate texture coordinates and scaling for this cube
        tileSize = o.getEdgeSize();
        rootSize = o.getEdgeSize() * FastMath.pow(2f, o.getDepth()); //useless
        calculateShifts(o, side);
        
        switch(side){
            case Octree.SIDE_FRONT:
            case Octree.SIDE_BACK:
                texCoordsArray = new float[]{shiftX, shiftY,
                                    shiftX + tileSize, shiftY,
                                    shiftX + tileSize, shiftY + tileSize,
                                    shiftX, shiftY + tileSize};
                break;
            case Octree.SIDE_LEFT:
            case Octree.SIDE_RIGHT:
                texCoordsArray = new float[]{shiftZ, shiftY,
                                    shiftZ + tileSize, shiftY,
                                    shiftZ + tileSize, shiftY + tileSize,
                                    shiftZ, shiftY + tileSize};
                break;
            case Octree.SIDE_TOP:
            case Octree.SIDE_BOTTOM:
                texCoordsArray = new float[]{shiftX, shiftZ,
                                    shiftX + tileSize, shiftZ,
                                    shiftX + tileSize, shiftZ + tileSize,
                                    shiftX, shiftZ + tileSize};
                break;
        }

        normalArray = new float[]{
                  n0.x, n0.y, n0.z
                , n1.x, n1.y, n1.z
                , n2.x, n2.y, n2.z
                , n3.x, n3.y, n3.z };
        
        indexArray = new short[]{0, 1, 2,
                                0, 2, 3};
    }
    
    /**
     * Calculates how much the texture has to be shifted
     * @param o the current octant to be painted
     * @param side which side of the octant we are working on
     */
    private void calculateShifts(Octant o, int side){
        Octant current = o;
        
        while(true){
            float quantum = current.getEdgeSize();
            switch(side){
                case Octree.SIDE_FRONT: //front side
                    switch(current.getType()){
                        case Octree.TYPE_1:
                        case Octree.TYPE_2:
                            shiftX += quantum;
                            shiftY += quantum;
                            break;
                        case Octree.TYPE_4:
                        case Octree.TYPE_3:
                            shiftY += quantum;
                            break;
                        case Octree.TYPE_5:
                        case Octree.TYPE_6:
                            shiftX += quantum;
                            break;
                        case Octree.TYPE_8:
                        case Octree.TYPE_7:
                            break;
                    }
                break;
                
                case Octree.SIDE_RIGHT:
                    switch(current.getType()){
                        case Octree.TYPE_2:
                        case Octree.TYPE_3:
                            shiftY += quantum;
                            shiftZ += quantum;
                            break;
                        case Octree.TYPE_6:
                        case Octree.TYPE_7:
                            shiftZ += quantum;
                            break;
                        case Octree.TYPE_1:
                        case Octree.TYPE_4:
                            shiftY += quantum;
                            break;
                        case Octree.TYPE_5:
                        case Octree.TYPE_8:
                            break;
                    }
                break;
                    
                case Octree.SIDE_BACK: //back side
                    switch(current.getType()){
                        case Octree.TYPE_3:
                        case Octree.TYPE_4:
                            shiftX += quantum;
                            shiftY += quantum;
                            break;
                        case Octree.TYPE_7:
                        case Octree.TYPE_8:
                            shiftX += quantum;
                            break;
                        case Octree.TYPE_1:
                        case Octree.TYPE_2:
                            shiftY += quantum;
                            break;
                        case Octree.TYPE_6:
                        case Octree.TYPE_5:
                            break;
                    }
                break;
                    
                case Octree.SIDE_LEFT: //left side
                    switch(current.getType()){
                        case Octree.TYPE_4:
                        case Octree.TYPE_1:
                            shiftY += quantum;
                            shiftZ += quantum;
                            break;
                        case Octree.TYPE_3:
                        case Octree.TYPE_2:
                            shiftY += quantum;
                            break;
                        case Octree.TYPE_8:
                        case Octree.TYPE_5:
                            shiftZ += quantum;
                            break;
                        case Octree.TYPE_7:
                        case Octree.TYPE_6:
                            break;
                    }
                break;
                    
                case Octree.SIDE_TOP:
                    switch(current.getType()){
                        case Octree.TYPE_2:
                        case Octree.TYPE_6:
                            shiftX += quantum;
                            shiftZ += quantum;
                            break;
                        case Octree.TYPE_1:
                        case Octree.TYPE_5:
                            shiftX += quantum;
                            break;
                        case Octree.TYPE_3:
                        case Octree.TYPE_7:
                            shiftZ += quantum;
                            break;
                        case Octree.TYPE_4:
                        case Octree.TYPE_8:
                            break;
                    }
                break;
                
                case Octree.SIDE_BOTTOM:
                    switch(current.getType()){
                        case Octree.TYPE_1:
                        case Octree.TYPE_5:
                            shiftX += quantum;
                            shiftZ += quantum;
                            break;
                        case Octree.TYPE_4:
                        case Octree.TYPE_8:
                            shiftZ += quantum;
                            break;
                        case Octree.TYPE_2:
                        case Octree.TYPE_6:
                            shiftX += quantum;
                            break;
                        case Octree.TYPE_3:
                        case Octree.TYPE_7:
                            break;
                    }
                break;
            }
            
            if(current.getType()== 0)
                break;//watch out for root
            else 
                current = current.getParent();
        }
    } 
}
