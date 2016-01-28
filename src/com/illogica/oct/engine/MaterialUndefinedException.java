/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.engine;

/**
 *
 * @author Loris
 */
public class MaterialUndefinedException extends RuntimeException {

    /**
     * Creates a new instance of <code>MaterialUndefinedException</code> without
     * detail message.
     */
    public MaterialUndefinedException() {
    }

    /**
     * Constructs an instance of <code>MaterialUndefinedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MaterialUndefinedException(String msg) {
        super(msg);
    }
}
