/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.octree;

import com.illogica.oct.engine.QuadV4;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 *
 * @author Loris
 */
public class OctantData {
    Octant o;
    public int materialType;
    float[] vertices;
    public float posArray[];
    public float texCoordsArray[];
    public float normArray[];
    public float tanArray[];
    public short indArray[];
    
    public OctantData(Octant o){
        this.o = o;
    }
    
    public Mesh compileArrays(){
        //Assuming the shape is a perfect cube
        Vector3f v0, v1, v2, v3, v4, v5, v6, v7;
        float s = o.getEdgeSize() / 2f;
        v0 = o.getOrigin().add(new Vector3f(s, s, s));
        v1 = o.getOrigin().add(new Vector3f(s, s, -s));
        v2 = o.getOrigin().add(new Vector3f(-s, s, -s));
        v3 = o.getOrigin().add(new Vector3f(-s, s, s));
        v4 = o.getOrigin().add(new Vector3f(s, -s, s));
        v5 = o.getOrigin().add(new Vector3f(s, -s, -s));
        v6 = o.getOrigin().add(new Vector3f(-s, -s, -s));
        v7 = o.getOrigin().add(new Vector3f(-s, -s, s));
        
        posArray = new float[12*6];
        texCoordsArray = new float[8*6];
        normArray = new float[12*6];
        tanArray = new float[16*6];
        indArray = new short[6*6];
        
        for(int i=0; i<6; i++){
            QuadV4 quad;
            switch (i) {
            case 0:
                quad = new QuadV4(v7, v4, v0, v3, o, i);
                break;
            case 1:
                quad = new QuadV4(v4, v5, v1, v0, o, i);
                break;
            case 2:
                quad = new QuadV4(v5, v6, v2, v1, o, i);
                break;
            case 3:
                quad = new QuadV4(v6, v7, v3, v2, o, i);
                break;
            case 4:
                quad = new QuadV4(v3, v0, v1, v2, o, i);
                break;
            case 5:
                quad = new QuadV4(v6, v5, v4, v7, o, i);
                break;
            default:
                throw new IllegalStateException("Wrong side number");
            }
            
            System.arraycopy(quad.positionArray, 0, posArray, i * quad.positionArray.length, quad.positionArray.length);
            System.arraycopy(quad.texCoordsArray, 0, texCoordsArray, i * quad.texCoordsArray.length, quad.texCoordsArray.length);
            System.arraycopy(quad.normalArray, 0, normArray, i * quad.normalArray.length, quad.normalArray.length);
            //System.arraycopy(quad.tangentArray, 0, tanArray, i * quad.tangentArray.length, quad.tangentArray.length);
            
            for(int j=0; j<quad.indexArray.length;j++){
                short val = quad.indexArray[j];
                val += 4*i;
                indArray[(quad.indexArray.length * i) + j] = val;
            }
        }
        
        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(posArray));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoordsArray));
        mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normArray));
        mesh.setBuffer(Type.Index,    3, BufferUtils.createShortBuffer(indArray));
        mesh.updateBound();
        return mesh;
    }
}
