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
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 *
 * @author Loris
 */
public class Materials extends AbstractAppState {

    // INVISIBLE MATERIAL     0
    public static final int MAT_AIR = 0; //default

    // USEFUL MATERIALS       1-10
    public static final int MAT_RANDOM_COLOR = 1;
    public static final int MAT_WIREFRAME = 2;
    public static final int MAT_NORMALS = 3;
    public static final int MAT_DEBUG = 4;

    //TRANSPARENT COLORS      10   
    // use http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:materials_overview for reference
    public static final int MAT_TRANSPARENT_GREEN = 10;

    //SOLID COLORS            100
    public static final int MAT_SOLID_BLACK = 100;
    public static final int MAT_SOLID_WHITE = 101;
    public static final int MAT_SOLID_DARKGRAY = 102;
    public static final int MAT_SOLID_GRAY = 103;
    public static final int MAT_SOLID_LIGHTGRAY = 104;
    public static final int MAT_SOLID_RED = 105;
    public static final int MAT_SOLID_GREEN = 106;
    public static final int MAT_SOLID_BLUE = 107;
    public static final int MAT_SOLID_YELLOW = 108;
    public static final int MAT_SOLID_MAGENTA = 109;
    public static final int MAT_SOLID_CYAN = 110;
    public static final int MAT_SOLID_ORANGE = 111;
    public static final int MAT_SOLID_BROWN = 112;
    public static final int MAT_SOLID_PINK = 113;

    //STONE TEXTURES          200    
    public static final int MAT_STONE_WALL = 200;

    //Debug colors
    private Material matRandomColor;
    private Material matWireFrame;
    private Material matNormals;
    private Material matDebug;

    //Solid colors
    private Material matSolidBlack;
    private Material matSolidWhite;
    private Material matSolidDarkGray;
    private Material matSolidGray;
    private Material matSolidLightGray;
    private Material matSolidRed;
    private Material matSolidGreen;
    private Material matSolidBlue;
    private Material matSolidYellow;
    private Material matSolidMagenta;
    private Material matSolidCyan;
    private Material matSolidOrange;
    private Material matSolidBrown;
    private Material matSolidPink;

    //Transparent colors
    private Material matTransparentGreen;

    //Textures
    private Material matStoneWall;
    
    private AppStateManager stateManager;
    private SimpleApplication app;

    private int currentMaterialId;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        this.app = (SimpleApplication) app;

        //Random color
        matRandomColor = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matRandomColor.setColor("Color", ColorRGBA.randomColor());
        matRandomColor.setName("Random Color");

        //Wireframe
        matWireFrame = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matWireFrame.setColor("Color", ColorRGBA.Yellow);
        matWireFrame.getAdditionalRenderState().setWireframe(true);
        matWireFrame.setName("Wireframe");

        //Material showing normals
        matNormals = new Material(app.getAssetManager(), "Common/MatDefs/Misc/ShowNormals.j3md");
        matNormals.setName("Normals");

        //A material with a debug texture
        matDebug = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        Texture debugTex = app.getAssetManager().loadTexture("Textures/Materials/debug/debug_tex_1024.png");
        debugTex.setWrap(Texture.WrapMode.Repeat);
        matDebug.setTexture("DiffuseMap", debugTex);
        matDebug.setBoolean("UseMaterialColors", true);
        matDebug.setColor("Diffuse", ColorRGBA.White);
        matDebug.setColor("Specular", ColorRGBA.White);
        matDebug.setFloat("Shininess", 64f);  // [0,128]
        matDebug.setName("Debug");

        //Solid colors
        matSolidBlack = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidBlack.setColor("Color", ColorRGBA.Black);
        matSolidBlack.setName("Solid black");
        matSolidWhite = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidWhite.setColor("Color", ColorRGBA.White);
        matSolidWhite.setName("Solid white");
        matSolidDarkGray = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidDarkGray.setColor("Color", ColorRGBA.DarkGray);
        matSolidDarkGray.setName("Solid dark grey");
        matSolidGray = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidGray.setColor("Color", ColorRGBA.Gray);
        matSolidGray.setName("Solid gray");
        matSolidLightGray = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidLightGray.setColor("Color", ColorRGBA.LightGray);
        matSolidLightGray.setName("Solid light gray");
        matSolidRed = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidRed.setColor("Color", ColorRGBA.Red);
        matSolidRed.setName("Solid red");
        matSolidGreen = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidGreen.setColor("Color", ColorRGBA.Green);
        matSolidGreen.setName("Solid green");
        matSolidBlue = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidBlue.setColor("Color", ColorRGBA.Blue);
        matSolidBlue.setName("Solid blue");
        matSolidYellow = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidYellow.setColor("Color", ColorRGBA.Yellow);
        matSolidYellow.setName("Solid yellow");
        matSolidMagenta = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidMagenta.setColor("Color", ColorRGBA.Magenta);
        matSolidMagenta.setName("Solid magenta");
        matSolidCyan = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidCyan.setColor("Color", ColorRGBA.Cyan);
        matSolidCyan.setName("Solid cyan");
        matSolidOrange = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidOrange.setColor("Color", ColorRGBA.Orange);
        matSolidOrange.setName("Solid orange");
        matSolidBrown = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidBrown.setColor("Color", ColorRGBA.Brown);
        matSolidBrown.setName("Solid brown");
        matSolidPink = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matSolidPink.setColor("Color", ColorRGBA.Pink);
        matSolidPink.setName("Solid pink");

        //Transparent green 
        matTransparentGreen = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matTransparentGreen.setColor("Color", new ColorRGBA(0f, 0.8f, 0f, 0.5f));
        matTransparentGreen.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        matTransparentGreen.getAdditionalRenderState().setDepthWrite(true);
        matTransparentGreen.getAdditionalRenderState().setAlphaTest(true);
        matTransparentGreen.getAdditionalRenderState().setAlphaFallOff(0.5f);
        matTransparentGreen.setName("Transparent Green");

        //Stone wall material
        matStoneWall = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        Texture stoneWallDiffuseTex = app.getAssetManager().loadTexture("Textures/Materials/stone-wall/stone-wall.jpg");
        Texture stoneWallNormTex = app.getAssetManager().loadTexture("Textures/Materials/stone-wall/stone-wall-norm.png");
        stoneWallDiffuseTex.setWrap(Texture.WrapMode.Repeat);
        stoneWallNormTex.setWrap(Texture.WrapMode.Repeat);
        matStoneWall.setTexture("DiffuseMap", stoneWallDiffuseTex);
        matStoneWall.setTexture("NormalMap", stoneWallNormTex);
        matStoneWall.setBoolean("UseMaterialColors", true);
        matStoneWall.setColor("Diffuse", ColorRGBA.White);
        matStoneWall.setColor("Specular", ColorRGBA.White);
        matStoneWall.setFloat("Shininess", 64f);  // [0,128]
        matStoneWall.setName("Stone wall");

        currentMaterialId = MAT_WIREFRAME;
    }
    
    public Material getSelectionBoxMaterial(){
        return matTransparentGreen;
    }

    public Material getCurrentMaterial() {
        return getMaterial(currentMaterialId);
    }
    
    public int getCurrentMaterialId(){
        return currentMaterialId;
    }
    
    public void setCurrentMaterialId(int id){
        this.currentMaterialId = id;
    }

    public Material getMaterial(int id) {
        currentMaterialId = id;
        switch (id) {
            case MAT_AIR: return null;
            case MAT_RANDOM_COLOR: return matRandomColor;
            case MAT_WIREFRAME: return matWireFrame;
            case MAT_NORMALS: return matNormals;
            case MAT_DEBUG: return matDebug;
            case MAT_SOLID_BLACK: return matSolidBlack;
            case MAT_SOLID_WHITE: return matSolidWhite;
            case MAT_SOLID_DARKGRAY: return matSolidDarkGray;
            case MAT_SOLID_GRAY: return matSolidGray;
            case MAT_SOLID_LIGHTGRAY: return matSolidLightGray;
            case MAT_SOLID_RED: return matSolidRed;
            case MAT_SOLID_GREEN: return matSolidGreen;
            case MAT_SOLID_BLUE: return matSolidBlue;
            case MAT_SOLID_YELLOW: return matSolidYellow;
            case MAT_SOLID_MAGENTA: return matSolidMagenta;
            case MAT_SOLID_CYAN: return matSolidCyan;
            case MAT_SOLID_ORANGE: return matSolidOrange;
            case MAT_SOLID_BROWN: return matSolidBrown;
            case MAT_SOLID_PINK: return matSolidPink;
            case MAT_TRANSPARENT_GREEN: return matTransparentGreen;
            case MAT_STONE_WALL: return matStoneWall;
            default: return matDebug;
        }
    }
}
