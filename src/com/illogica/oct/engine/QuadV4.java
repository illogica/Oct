/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.engine;

import com.illogica.oct.octree.Octant;
import com.illogica.oct.octree.Octree;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author Loris
 */
public class QuadV4 extends Mesh {
    private Vector3f v0;
    private Vector3f v1;
    private Vector3f v2;
    private Vector3f v3;
    
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

    public QuadV4(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Octant o, int side, int unitDepth){
        updateGeometry(v0, v1, v2, v3, o, side);
    }
    
    public QuadV4(Vector3f vertices[], Octant o, int side, int unitDepth){
        updateGeometry(vertices[0], vertices[1], vertices[2], vertices[3], o, side);
    }

    private void updateGeometry(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Octant o, int side) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    
        Vector3f n0 = (v1.subtract(v0).cross(v3.subtract(v0))).normalize();
        Vector3f n1 = (v2.subtract(v1).cross(v0.subtract(v1))).normalize();
        Vector3f n2 = (v3.subtract(v2).cross(v1.subtract(v2))).normalize();
        Vector3f n3 = (v0.subtract(v3).cross(v2.subtract(v3))).normalize();
        
        setBuffer(VertexBuffer.Type.Position, 3, new float[]{
                  v0.x, v0.y, v0.z
                , v1.x, v1.y, v1.z
                , v2.x, v2.y, v2.z
                , v3.x, v3.y, v3.z });
        
        //Calculate texture coordinates and scaling for this cube
        tileSize = o.getEdgeSize();
        rootSize = o.getEdgeSize() * FastMath.pow(2f, o.getDepth()); //useless
        calculateShifts(o, side);
        
        float texCoords[] = null;
        
        switch(side){
            case Octree.SIDE_FRONT:
            case Octree.SIDE_BACK:
                texCoords = new float[]{shiftX, shiftY,
                                    shiftX + tileSize, shiftY,
                                    shiftX + tileSize, shiftY + tileSize,
                                    shiftX, shiftY + tileSize};
                break;
            case Octree.SIDE_LEFT:
            case Octree.SIDE_RIGHT:
                texCoords = new float[]{shiftZ, shiftY,
                                    shiftZ + tileSize, shiftY,
                                    shiftZ + tileSize, shiftY + tileSize,
                                    shiftZ, shiftY + tileSize};
                break;
            case Octree.SIDE_TOP:
            case Octree.SIDE_BOTTOM:
                texCoords = new float[]{shiftX, shiftZ,
                                    shiftX + tileSize, shiftZ,
                                    shiftX + tileSize, shiftZ + tileSize,
                                    shiftX, shiftZ + tileSize};
                break;
        }
        setBuffer(VertexBuffer.Type.TexCoord, 2, texCoords);
        
        
        setBuffer(VertexBuffer.Type.Normal, 3, new float[]{
                  n0.x, n0.y, n0.z
                , n1.x, n1.y, n1.z
                , n2.x, n2.y, n2.z
                , n3.x, n3.y, n3.z });
        
        setBuffer(VertexBuffer.Type.Index, 3, new short[]{0, 1, 2,
                                                 0, 2, 3});
        
        TangentBinormalGenerator.generate(this);
        
        /*System.out.print("TYPE:"+ o.getType()+",Side:" + side + ",shiftX:" + shiftX +",shiftY:" + shiftY + ",shiftZ:" + shiftZ + ",tileSize:" + tileSize + ",texCoords:");
        System.out.print("(" + texCoords[0]+ "," +texCoords[1] + ")");    
        System.out.print("(" + texCoords[2]+ "," +texCoords[3] + ")");    
        System.out.print("(" + texCoords[4]+ "," +texCoords[5] + ")");    
        System.out.print("(" + texCoords[6]+ "," +texCoords[7] + ")");    
        System.out.println("");*/
        updateBound();
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
