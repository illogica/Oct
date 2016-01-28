/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 *
 * @author Loris
 */
public class Materials extends AbstractAppState {
    private AppStateManager stateManager;
    private SimpleApplication app;
    
    private Material randomColorMat;
    private Material wireFrameMat;
    private Material normalsMat;
    private Material debugMat;
    
    private Material stoneWallMat;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        this.app = (SimpleApplication)app;
        
        //Random color
        randomColorMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        randomColorMat.setColor("Color", ColorRGBA.randomColor());
        
        //Wireframe
        wireFrameMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        wireFrameMat.setColor("Color", ColorRGBA.Yellow);
        wireFrameMat.getAdditionalRenderState().setWireframe(true);
        
        //Material showing normals
        normalsMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md");
        
        //A material with a debug texture
        debugMat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        Texture debugTex = app.getAssetManager().loadTexture("Textures/Materials/debug/debug_tex_1024.png");
        debugTex.setWrap(Texture.WrapMode.Repeat);
        debugMat.setTexture("DiffuseMap", debugTex);
        debugMat.setBoolean("UseMaterialColors",true);
        debugMat.setColor("Diffuse",ColorRGBA.White);
        debugMat.setColor("Specular",ColorRGBA.White);
        debugMat.setFloat("Shininess", 64f);  // [0,128]
        
        //Stone wall material
        stoneWallMat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        Texture stoneWallDiffuseTex = app.getAssetManager().loadTexture("Textures/Materials/stone-wall/stone-wall.jpg");
        Texture stoneWallNormTex = app.getAssetManager().loadTexture("Textures/Materials/stone-wall/stone-wall-norm.png");
        stoneWallDiffuseTex.setWrap(Texture.WrapMode.Repeat);
        stoneWallNormTex.setWrap(Texture.WrapMode.Repeat);
        stoneWallMat.setTexture("DiffuseMap", stoneWallDiffuseTex);
        stoneWallMat.setTexture("NormalMap",stoneWallNormTex);
        stoneWallMat.setBoolean("UseMaterialColors",true);    
        stoneWallMat.setColor("Diffuse",ColorRGBA.White);
        stoneWallMat.setColor("Specular",ColorRGBA.White);
        stoneWallMat.setFloat("Shininess", 64f);  // [0,128]
    }
    
    public Material getRandomColorMaterial(){ return randomColorMat; }
    public Material getNormalsMaterial() { return normalsMat; }
    public Material getWireframeMaterial() { return wireFrameMat; }
    public Material getDebugMaterial(){ return debugMat; }
    public Material getStoneWallMaterial() { return stoneWallMat; }
}
