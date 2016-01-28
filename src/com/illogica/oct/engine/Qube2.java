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
 *
 * @author Loris
 */
public class Qube2 extends Node {

    Vector3f v0, v1, v2, v3, v4, v5, v6, v7;
    QuadV4 quad[];
    Geometry geom[];
    Octant o;
    Material mat;

    public Qube2() {
    }

    public Qube2(Octant o, Material mat) {
        updateGeometry(o, mat);
    }

    public void updateMaterial(Material material) {
        for (int i = 0; i < 6; i++) {
            geom[i].setMaterial(material);
        }
    }

    public void scaleTextureCoordinates(float n) {
        Vector2f scaleVec = new Vector2f(n, n);

        for (int i = 0; i < 6; i++) {
            quad[i].scaleTextureCoordinates(scaleVec);
        }
    }

    private void updateGeometry(Octant o, Material mat) {
        this.detachAllChildren();
        quad = new QuadV4[6];
        geom = new Geometry[6];

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

        for (int i = 0; i < 6; i++) {

            quad[i] = quadBySide(i);
            geom[i] = new Geometry("Q" + i, quad[i]);
            geom[i].setMaterial(mat);
            this.attachChild(geom[i]);
        }
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
}
