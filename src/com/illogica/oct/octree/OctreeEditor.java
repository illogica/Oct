/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.octree;

import com.jme3.math.Vector3f;

/**
 *
 * @author Loris
 */
public interface OctreeEditor {
    
    public Octant generateRoot(float size, Vector3f origin);
    
    //At which depth do we have octant size = 1.0f
    public byte getOctreeUnitDepth();
    
    //Gets an octant given its coordinates and depth
    public Octant getOctant(Octinfo o);
    public Octant deleteOctant(Octant o);
    public Octant createOctant(Octinfo o);
    public Octant subdivideOctant(Octant o);
    public Octant changeOctantMaterial(Octant o, int Material);
   
}
