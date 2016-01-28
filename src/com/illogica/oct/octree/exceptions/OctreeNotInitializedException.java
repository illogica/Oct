/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.octree.exceptions;

/**
 *
 * @author Loris
 */
public class OctreeNotInitializedException extends RuntimeException {

    /**
     * Creates a new instance of <code>OctreeNotInitializedException</code>
     * without detail message.
     */
    public OctreeNotInitializedException() {
    }

    /**
     * Constructs an instance of <code>OctreeNotInitializedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public OctreeNotInitializedException(String msg) {
        super(msg);
    }
}
