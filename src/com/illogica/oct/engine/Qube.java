/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.engine;

import com.illogica.oct.octree.Octant;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * A Cube made of custom quads
 * @author Loris
 */
public class Qube extends Node{
    
    Vector3f v0,v1,v2,v3,v4,v5,v6,v7;
    QuadV4 qa,qb,qc,qd,qe,qf;
    Geometry geomA,geomB,geomC,geomD,geomE,geomF;
    Octant o;
    Material mat;
    
    public Qube(){}
    
    public Qube(Octant o, Material mat){
        updateGeometry(o, mat);
    }
    
    public void updateMaterial(Material material){
        if(geomA!=null){
            geomA.setMaterial(material);
            geomB.setMaterial(material);
            geomC.setMaterial(material);
            geomD.setMaterial(material);
            geomE.setMaterial(material);
            geomF.setMaterial(material);
        }
    }
    
    public void scaleTextureCoordinates(float n){
        /*qa.scaleTextureCoordinates(new Vector2f(n,n));
        qb.scaleTextureCoordinates(new Vector2f(n,n));
        qc.scaleTextureCoordinates(new Vector2f(n,n));
        qd.scaleTextureCoordinates(new Vector2f(n,n));
        qe.scaleTextureCoordinates(new Vector2f(n,n));
        qf.scaleTextureCoordinates(new Vector2f(n,n));*/
    }
    
    private void updateGeometry(Octant o, Material mat){
        //see if n.hasVertices(), then do this:
        this.o = o;
        this.mat = mat;
        
        float s = o.getEdgeSize()/2;
        v0 = o.getOrigin().add(new Vector3f(  s,  s,  s));
        v1 = o.getOrigin().add(new Vector3f(  s,  s, -s));
        v2 = o.getOrigin().add(new Vector3f( -s,  s, -s));
        v3 = o.getOrigin().add(new Vector3f( -s,  s,  s));
        v4 = o.getOrigin().add(new Vector3f(  s, -s,  s));
        v5 = o.getOrigin().add(new Vector3f(  s, -s, -s));
        v6 = o.getOrigin().add(new Vector3f( -s, -s, -s));
        v7 = o.getOrigin().add(new Vector3f( -s, -s,  s));
        
        /*qa = new QuadV4(v7, v4, v0, v3, o, );
        qb = new QuadV4(v4, v5, v1, v0);
        qc = new QuadV4(v5, v6, v2, v1);
        qd = new QuadV4(v6, v7, v3, v2);
        qe = new QuadV4(v3, v0, v1, v2);
        qf = new QuadV4(v6, v5, v4, v7);*/
               
        //QuadV4 q = new QuadV4(new Vector3f(1f,1f,0f), new Vector3f(3f,1f,0f), new Vector3f(3f,3f,5f), new Vector3f(1f,3f,5f));
        /*geomA = new Geometry("Qa", qa);
        geomA.setMesh(qe);
        geomB = new Geometry("Qb", qb);
        geomC = new Geometry("Qc", qc);
        geomD = new Geometry("Qd", qd);
        geomE = new Geometry("Qe", qe);
        geomF = new Geometry("Qf", qf);*/
        
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        //mat.setColor("Color", ColorRGBA.randomColor());
        geomA.setMaterial(mat);
        geomB.setMaterial(mat);
        geomC.setMaterial(mat);
        geomD.setMaterial(mat);
        geomE.setMaterial(mat);
        geomF.setMaterial(mat);
        
        this.attachChild(geomA);
        this.attachChild(geomB);
        this.attachChild(geomC);
        this.attachChild(geomD);
        this.attachChild(geomE);
        this.attachChild(geomF);       
    }
}
