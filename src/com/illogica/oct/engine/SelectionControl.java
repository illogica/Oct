/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.engine;

import com.illogica.oct.octree.Octinfo;
import com.jme3.collision.CollisionResult;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Loris
 */
public class SelectionControl extends AbstractControl {
    

    Quaternion xRotation;
    Quaternion yRotation;
    Quaternion zRotation;
    CollisionResult collisionResult;
    Octinfo oi;

    public SelectionControl() {
        oi = new Octinfo();

        xRotation = new Quaternion();
        xRotation.fromAngleNormalAxis(FastMath.PI / 2f, Vector3f.UNIT_X);
        yRotation = new Quaternion();
        yRotation.fromAngleNormalAxis(FastMath.PI / 2f, Vector3f.UNIT_Y);
        zRotation = new Quaternion();
        zRotation.fromAngleNormalAxis(0, Vector3f.UNIT_Z);

    }

    public void updateData(CollisionResult result, Octinfo oi) {
        this.collisionResult = result;
        this.oi = oi;
        /*spatial.setLocalScale(oi.size);
         Vector3f normal = collisionResult.getContactNormal();
         if (normal.x == 1f || normal.x == -1f) {
         spatial.setLocalRotation(yRotation);
         }
         if (normal.y == 1f || normal.y == -1f) {
         spatial.setLocalRotation(xRotation);
         }
         if (normal.z == 1f || normal.z == -1f) {
         spatial.setLocalRotation(zRotation);
         }
         spatial.setLocalTranslation(oi.origin().addLocal(collisionResult.getContactNormal().mult(oi.size / 2f)));*/
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (collisionResult != null) {
            spatial.setLocalScale(oi.size);
            Vector3f normal = collisionResult.getContactNormal();
            if (normal.x == 1f || normal.x == -1f) {
                spatial.setLocalRotation(yRotation);
            }
            if (normal.y == 1f || normal.y == -1f) {
                spatial.setLocalRotation(xRotation);
            }
            if (normal.z == 1f || normal.z == -1f) {
                spatial.setLocalRotation(zRotation);
            }
            spatial.setLocalTranslation(oi.origin().addLocal(collisionResult.getContactNormal().mult(oi.size / 2f)));
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
