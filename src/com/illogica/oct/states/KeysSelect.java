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
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 *
 * @author Loris
 */
public class KeysSelect extends AbstractAppState {
    
    public static final int MODE_EDIT_3D = 0;
    public static final int MODE_EDIT_GUI = 1;
    
    private int currentMode = 0;

    private final static String[] mouseMappings = new String[]{
        "WheelUp",
        "WheelDown",
        "MouseMove"
    };
    
    private final static String[] keyboardMappings = new String[]{
        "Switch Gui - 3d world",
        "Subdivide",
        "Increase step",
        "Decrease step",
        "MouseSelect",
        "Esc"
    };
    
    private final static String[] modifierKeys = new String[]{
        "LeftCtrl",
        "LeftShift",
        "RightCtrl",
        "RightShift"
    };

    private SimpleApplication app;
    private AppStateManager stateManager;
    
    boolean leftCtrlPressed = false;
    boolean rightCtrlPressed = false;
    boolean leftShiftPressed = false;
    boolean rightShiftPressed = false;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        System.out.println("Initialize KeySelectorAppState");
        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        
        this.app.getInputManager().deleteMapping(CameraInput.FLYCAM_ZOOMIN);
        this.app.getInputManager().deleteMapping(CameraInput.FLYCAM_ZOOMOUT);
        this.app.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

        this.app.getInputManager().addMapping("WheelUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        this.app.getInputManager().addMapping("WheelDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        
        this.app.getInputManager().addMapping("MouseMove", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        this.app.getInputManager().addMapping("MouseMove", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        this.app.getInputManager().addMapping("MouseMove", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        this.app.getInputManager().addMapping("MouseMove", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        
        this.app.getInputManager().addMapping("MouseSelect", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        this.app.getInputManager().addMapping("Switch Gui - 3d world", new KeyTrigger(KeyInput.KEY_F1));
        this.app.getInputManager().addMapping("Esc", new KeyTrigger(KeyInput.KEY_ESCAPE));
        this.app.getInputManager().addMapping("Subdivide", new KeyTrigger(KeyInput.KEY_1));
        this.app.getInputManager().addMapping("Increase step", new KeyTrigger(KeyInput.KEY_PGUP));
        this.app.getInputManager().addMapping("Decrease step", new KeyTrigger(KeyInput.KEY_PGDN));
        
        //Modifier keys
        this.app.getInputManager().addMapping("LeftCtrl", new KeyTrigger(KeyInput.KEY_LCONTROL));
        this.app.getInputManager().addMapping("RightCtrl", new KeyTrigger(KeyInput.KEY_RCONTROL));
        this.app.getInputManager().addMapping("LeftShift", new KeyTrigger(KeyInput.KEY_LSHIFT));
        this.app.getInputManager().addMapping("RightShift", new KeyTrigger(KeyInput.KEY_RSHIFT));
        
        this.app.getInputManager().addListener(actionListenerModifierKeys, modifierKeys);
        
        //Initialize the current mode, we start in 3D edit mode
        currentMode = MODE_EDIT_3D;
        this.app.getInputManager().addListener(actionListenerEditMode3D, keyboardMappings);
        this.app.getInputManager().addListener(analogListenerEditMode3D, mouseMappings);
    }
    
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }
    
    public boolean isCtrlPressed(){
        return leftCtrlPressed || rightCtrlPressed;
    }
    
    public boolean isShiftPressed(){
        return leftShiftPressed || rightShiftPressed;
    }
    
    /**
     * Used to see what mode we are in. Can be one of the public MODE_x constants
     * @return the current mode
     */
    public int getCurrentMode() {
        return currentMode;
    }
    
    public void switchToGuiMode(){
        currentMode = MODE_EDIT_GUI;
        stateManager.getState(Hud.class).focusToConsole();
        app.getInputManager().removeListener(actionListenerEditMode3D);
        app.getInputManager().removeListener(analogListenerEditMode3D);
        app.getInputManager().addListener(actionListenerEditModeGui, keyboardMappings);
        app.getInputManager().addListener(analogListenerEditModeGui, mouseMappings);
        app.getFlyByCamera().setDragToRotate(true);
    } 
    
    public void switchTo3dEditMode(){
        currentMode = MODE_EDIT_3D;
        stateManager.getState(Hud.class).unfocusConsole();
        app.getInputManager().removeListener(actionListenerEditModeGui);
        app.getInputManager().removeListener(analogListenerEditModeGui);
        app.getInputManager().addListener(actionListenerEditMode3D, keyboardMappings);
        app.getInputManager().addListener(analogListenerEditMode3D, mouseMappings);
        app.getFlyByCamera().setDragToRotate(false);
    }
    
    private final ActionListener actionListenerModifierKeys = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if(name.equals("LeftCtrl") && keyPressed){
                leftCtrlPressed = true;
            } else if(name.equals("LeftCtrl") && !keyPressed){
                leftCtrlPressed = false;
            } else if(name.equals("RightCtrl") && keyPressed){
                rightCtrlPressed = true;
            } else if(name.equals("RightCtrl") && !keyPressed){
                rightCtrlPressed = false;
            } else if(name.equals("LeftShift") && keyPressed){
                leftShiftPressed = true;
            } else if(name.equals("LeftShift") && !keyPressed){
                leftShiftPressed = false;
            } else if(name.equals("RightShift") && keyPressed){
                rightShiftPressed = true;
            } else if(name.equals("RightShift") && !keyPressed){
                rightShiftPressed = false;
            } 
        }
    };

    private final ActionListener actionListenerEditMode3D = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Switch Gui - 3d world") && !keyPressed) {
                 switchToGuiMode();
            } else if(name.equals("Subdivide") && !keyPressed){
                stateManager.getState(Engine.class).onSubdivideOctantRequest();
            } else if(name.equals("Increase step") && !keyPressed){
                stateManager.getState(Engine.class).increaseStep();
            } else if(name.equals("Decrease step") && !keyPressed){
                stateManager.getState(Engine.class).decreaseStep();
            } else if(name.equals("MouseSelect") && keyPressed){
                stateManager.getState(Engine.class).onMouseSelect();
            } else if(name.equals("Esc") && keyPressed){
                stateManager.getState(SelectionManager.class).selectionBoxesClear();
            }
        }
    };
    
    private final ActionListener actionListenerEditModeGui = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Switch Gui - 3d world") && !keyPressed) {
                switchTo3dEditMode();
            }
        }
    };
    
    private final AnalogListener analogListenerEditModeGui = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("WheelUp")) {
                if(isCtrlPressed())
                    System.out.println("Wheel up with ctrl");
                else
                    System.out.println("Wheel up without ctrl");
            }
        }
    };
    
    private final AnalogListener analogListenerEditMode3D = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("WheelUp")) {
                if(isCtrlPressed())
                    stateManager.getState(Engine.class).decreaseStep();
                else
                    stateManager.getState(Engine.class).onExtrudeOctantRequest();
            } else if (name.equals("WheelDown")) {
                if(isCtrlPressed())
                    stateManager.getState(Engine.class).increaseStep();
                else
                    stateManager.getState(Engine.class).onDeleteOctantRequest();
            } else if(name.equals("MouseMove")){
                stateManager.getState(Engine.class).onRefreshSelection();
            }
        }
    };
}
