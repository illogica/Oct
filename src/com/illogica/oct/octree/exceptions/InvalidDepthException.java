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
public class InvalidDepthException extends RuntimeException {

    /**
     * Creates a new instance of <code>InvalidDepth</code> without detail
     * message.
     */
    public InvalidDepthException() {
    }

    /**
     * Constructs an instance of <code>InvalidDepth</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public InvalidDepthException(String msg) {
        super(msg);
    }
}
