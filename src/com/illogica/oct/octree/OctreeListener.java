/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.octree;

/**
 *
 * @author Loris
 */
public interface OctreeListener {
    void onOctantGenerated(Octant o);
    void onOctantDeleted(Octant o);
    void onOctantMaterialChanged(Octant o);

    void setOctree(Octree tree);
}
