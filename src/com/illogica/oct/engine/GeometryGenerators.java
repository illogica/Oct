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
import com.illogica.oct.octree.Octinfo;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author Loris
 */
public class GeometryGenerators {
    private static AssetManager assetManager;
    
    public static void initialize(AssetManager am){
        assetManager = am;
    }
    
    /**
     * Generates a cube based on the octant.
     * New versions will load materials and whatever according to the data
     * saved into the octant.
     * @param n
     * @return 
     */
    public static Geometry getRandomColorCube(Octant n) {
        Box b = new Box(n.getEdgeSize() / 1.5f, n.getEdgeSize() / 1.5f, n.getEdgeSize() / 1.5f);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        mat.setColor("Color", ColorRGBA.randomColor());
        geom.setMaterial(mat);
        geom.setName("Cube" + n.getId());
        geom.setUserData("Octant", n);
        geom.setLocalTranslation(n.getOrigin());
        return geom;
    }
    
    public static Geometry getCubeByOctinfo(Octinfo n, Material mat) {
        Box b = new Box(n.size / 1.99f, n.size / 1.99f, n.size / 1.99f);
        Geometry geom = new Geometry("Box", b);
        geom.setMaterial(mat);
        geom.setName(n.toString());
        geom.setUserData("Octinfo", n);
        geom.setLocalTranslation(n.origin());
        return geom;
    }
    
    /*public static Spatial quadBasedCube(Octant n){
        //see if n.hasVertices(), then do this:
        float s = n.getEdgeSize()/2;
        Vector3f v0 = n.getOrigin().add(new Vector3f(  s,  s,  s));
        Vector3f v1 = n.getOrigin().add(new Vector3f(  s,  s, -s));
        Vector3f v2 = n.getOrigin().add(new Vector3f( -s,  s, -s));
        Vector3f v3 = n.getOrigin().add(new Vector3f( -s,  s,  s));
        Vector3f v4 = n.getOrigin().add(new Vector3f(  s, -s,  s));
        Vector3f v5 = n.getOrigin().add(new Vector3f(  s, -s, -s));
        Vector3f v6 = n.getOrigin().add(new Vector3f( -s, -s, -s));
        Vector3f v7 = n.getOrigin().add(new Vector3f( -s, -s,  s));
        
        QuadV4 qa = new QuadV4(v7, v4, v0, v3);
        QuadV4 qb = new QuadV4(v4, v5, v1, v0);
        QuadV4 qc = new QuadV4(v5, v6, v2, v1);
        QuadV4 qd = new QuadV4(v6, v7, v3, v2);
        QuadV4 qe = new QuadV4(v3, v0, v1, v2);
        QuadV4 qf = new QuadV4(v6, v5, v4, v7);
               
        //QuadV4 q = new QuadV4(new Vector3f(1f,1f,0f), new Vector3f(3f,1f,0f), new Vector3f(3f,3f,5f), new Vector3f(1f,3f,5f));
        Geometry geomA = new Geometry("Qa", qa);
        Geometry geomB = new Geometry("Qb", qb);
        Geometry geomC = new Geometry("Qc", qc);
        Geometry geomD = new Geometry("Qd", qd);
        Geometry geomE = new Geometry("Qe", qe);
        Geometry geomF = new Geometry("Qf", qf);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        //mat.setColor("Color", ColorRGBA.randomColor());
        geomA.setMaterial(mat);
        geomB.setMaterial(mat);
        geomC.setMaterial(mat);
        geomD.setMaterial(mat);
        geomE.setMaterial(mat);
        geomF.setMaterial(mat);
        
        Node qube = new Node("Qube");
        
        qube.attachChild(geomA);
        qube.attachChild(geomB);
        qube.attachChild(geomC);
        qube.attachChild(geomD);
        qube.attachChild(geomE);
        qube.attachChild(geomF);       
        
        return qube;
    }*/
    
    /**
     * Generates a unit cube with a given material.
     * Useful for the Gui
     * @param mat the material to use
     * @param name a name to identify the box
     * @return 
     */
    public static Geometry boxByMat(Material mat, String name){
        Box b = new Box(0.5f, 0.5f, 0.5f);
        TangentBinormalGenerator.generate(b);
        Geometry geom = new Geometry(name, b);
        geom.setMaterial(mat);
        return geom;
    }
    
    /**
     * Generates a wireframe white cube
     * @param o
     * @return 
     */
    public static Geometry wireframeWhiteCube(Octant o){
        WireBox w = new WireBox(o.getEdgeSize()/2, o.getEdgeSize()/2, o.getEdgeSize()/2);
        w.setLineWidth(1);
        Geometry geom = new Geometry("WireBox", w);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setWireframe(true);
        geom.setMaterial(mat);
        geom.setLocalTranslation(o.getOrigin());
        geom.scale(1.001f);
        return geom;
    }
    
    /**
     * Creates a wireframe quad on the XY plane
     * @return 
     */
    public static Node wireFrameQuad(){
        Vector3f p1, p2, p3, p4;
        p1 = new Vector3f(0.5f, 0.5f, 0f);
        p2 = new Vector3f(0.5f, -0.5f, 0f);
        p3 = new Vector3f(-0.5f, -0.5f, 0f);
        p4 = new Vector3f(-0.5f, 0.5f, 0f);
        Line line1 = new Line(p1, p2);
        line1.setLineWidth(3);
        Geometry geom1 = new Geometry("Line1", line1);
        Line line2 = new Line(p2, p3);
        line2.setLineWidth(3);
        Geometry geom2 = new Geometry("Line2", line2);
        Line line3 = new Line(p3, p4);
        line2.setLineWidth(3);
        Geometry geom3 = new Geometry("Line3", line3);
        Line line4 = new Line(p4, p1);
        line4.setLineWidth(3);
        Geometry geom4 = new Geometry("Line4", line4);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        mat.getAdditionalRenderState().setWireframe(true);
        geom1.setMaterial(mat);
        geom2.setMaterial(mat);
        geom3.setMaterial(mat);
        geom4.setMaterial(mat);
        
        Node node = new Node("Wireframe quad");
        node.attachChild(geom1);
        node.attachChild(geom2);
        node.attachChild(geom3);
        node.attachChild(geom4);
        return node;
    }
    
    public static Geometry[] wireFrameQuadFromNormal(Vector3f normal, Octinfo n){
        Vector3f p1, p2, p3, p4;
        p1 = p2 = p3 = p4 = Vector3f.ZERO;
        float transl = n.size/ 2f;
        float side = n.size / 2f;
        
        if(normal.x == 1f){ //plane YZ
            p1 = new Vector3f(transl, side, side);
            p2 = new Vector3f(transl, side, -side);
            p3 = new Vector3f(transl, -side, -side);
            p4 = new Vector3f(transl, -side, side);
        } else if (normal.x == -1f){
            p1 = new Vector3f(-transl, side, side);
            p2 = new Vector3f(-transl, side, -side);
            p3 = new Vector3f(-transl, -side, -side);
            p4 = new Vector3f(-transl, -side, side);
        } else if (normal.y == 1f){
            p1 = new Vector3f(side, transl, side);
            p2 = new Vector3f(side, transl, -side);
            p3 = new Vector3f(-side, transl, -side);
            p4 = new Vector3f(-side, transl, side);
        } else if (normal.y == -1f){
            p1 = new Vector3f(side, -transl, side);
            p2 = new Vector3f(side, -transl, -side);
            p3 = new Vector3f(-side, -transl, -side);
            p4 = new Vector3f(-side, -transl, side);
        } else if (normal.z == 1f){
            p1 = new Vector3f(side, side, transl);
            p2 = new Vector3f(side, -side, transl);
            p3 = new Vector3f(-side, -side, transl);
            p4 = new Vector3f(-side, side, transl);
        } else if (normal.z == -1f){
            p1 = new Vector3f(side, side, -transl);
            p2 = new Vector3f(side, -side, -transl);
            p3 = new Vector3f(-side, -side, -transl);
            p4 = new Vector3f(-side, side, -transl);
        }else {
            System.out.println("E' proprio un sistema del cazzo.");
        }
        
        Line line1 = new Line(p1, p2);
        line1.setLineWidth(3);
        Geometry geom1 = new Geometry("Line1", line1);
        Line line2 = new Line(p2, p3);
        line2.setLineWidth(3);
        Geometry geom2 = new Geometry("Line2", line2);
        Line line3 = new Line(p3, p4);
        line2.setLineWidth(3);
        Geometry geom3 = new Geometry("Line3", line3);
        Line line4 = new Line(p4, p1);
        line4.setLineWidth(3);
        Geometry geom4 = new Geometry("Line4", line4);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        mat.getAdditionalRenderState().setWireframe(true);
        geom1.setMaterial(mat);
        geom1.setLocalTranslation(n.origin());
        geom2.setMaterial(mat);
        geom2.setLocalTranslation(n.origin());
        geom3.setMaterial(mat);
        geom3.setLocalTranslation(n.origin());
        geom4.setMaterial(mat);
        geom4.setLocalTranslation(n.origin());
        
        Geometry g[] = {geom1, geom2, geom3, geom4};
        return  g;
    }
    
    //for debugging purposes
    public static Geometry putShape(Mesh mesh, ColorRGBA color){
        Geometry g = new Geometry("coordinate axis", mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        return g;
    }
}
