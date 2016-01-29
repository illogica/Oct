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
    
    public static final int MATERIAL_AIR = 0; //default
    public static final int MATERIAL_RANDOM_COLOR = 1;
    public static final int MATERIAL_WIREFRAME = 2;
    public static final int MATERIAL_NORMALS = 3;
    public static final int MATERIAL_DEBUG = 4;
    
    public static final int MATERIAL_STONE_WALL = 100;
    
    private AppStateManager stateManager;
    private SimpleApplication app;
    
    private Material materialCurrent; //the current material
    
    private Material matRandomColor;
    private Material matWireFrame;
    private Material matNormals;
    private Material matDebug;
    
    private Material matStoneWall;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        this.app = (SimpleApplication)app;
        
        //Random color
        matRandomColor = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matRandomColor.setColor("Color", ColorRGBA.randomColor());
        
        //Wireframe
        matWireFrame = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matWireFrame.setColor("Color", ColorRGBA.Yellow);
        matWireFrame.getAdditionalRenderState().setWireframe(true);
        
        //Material showing normals
        matNormals = new Material(app.getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md");
        
        //A material with a debug texture
        matDebug = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        Texture debugTex = app.getAssetManager().loadTexture("Textures/Materials/debug/debug_tex_1024.png");
        debugTex.setWrap(Texture.WrapMode.Repeat);
        matDebug.setTexture("DiffuseMap", debugTex);
        matDebug.setBoolean("UseMaterialColors",true);
        matDebug.setColor("Diffuse",ColorRGBA.White);
        matDebug.setColor("Specular",ColorRGBA.White);
        matDebug.setFloat("Shininess", 64f);  // [0,128]
        
        //Stone wall material
        matStoneWall = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        Texture stoneWallDiffuseTex = app.getAssetManager().loadTexture("Textures/Materials/stone-wall/stone-wall.jpg");
        Texture stoneWallNormTex = app.getAssetManager().loadTexture("Textures/Materials/stone-wall/stone-wall-norm.png");
        stoneWallDiffuseTex.setWrap(Texture.WrapMode.Repeat);
        stoneWallNormTex.setWrap(Texture.WrapMode.Repeat);
        matStoneWall.setTexture("DiffuseMap", stoneWallDiffuseTex);
        matStoneWall.setTexture("NormalMap",stoneWallNormTex);
        matStoneWall.setBoolean("UseMaterialColors",true);    
        matStoneWall.setColor("Diffuse",ColorRGBA.White);
        matStoneWall.setColor("Specular",ColorRGBA.White);
        matStoneWall.setFloat("Shininess", 64f);  // [0,128]
        
        
        materialCurrent = matStoneWall;
    }
    
    public Material getCurrentMaterial(){ return materialCurrent; }
    
    public Material getMaterial(int id){
        switch(id){
            case MATERIAL_AIR:
                return null;
            case MATERIAL_RANDOM_COLOR:
                materialCurrent = matRandomColor;
                return matRandomColor;
            case MATERIAL_WIREFRAME:
                materialCurrent = matWireFrame;
                return matWireFrame;
            case MATERIAL_NORMALS:
                materialCurrent = matNormals;
                return matNormals;
            case MATERIAL_DEBUG:
                materialCurrent = matDebug;
                return matDebug;
            case MATERIAL_STONE_WALL:
                materialCurrent = matStoneWall;
                return matStoneWall;
        }
        return matDebug;
    }
}
