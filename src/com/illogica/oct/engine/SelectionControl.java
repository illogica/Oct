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
