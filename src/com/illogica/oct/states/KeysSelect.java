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
    
    private final static String[] mouseMappings = new String[]{
        "WheelUp",
        "WheelDown",
        "MouseMove"
    };
    
    private final static String[] keyboardMappings = new String[]{
        "Console",
        "Subdivide",
        "Increase step",
        "Decrease step",
        "MouseSelect",
        "Ctrl"
    };

    private SimpleApplication app;
    private AppStateManager stateManager;
    boolean dragToRotate = false;
    
    boolean ctrlPressed = false;

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
        
        this.app.getInputManager().addMapping("Console", new KeyTrigger(KeyInput.KEY_ESCAPE));
        this.app.getInputManager().addMapping("Subdivide", new KeyTrigger(KeyInput.KEY_1));
        this.app.getInputManager().addMapping("Increase step", new KeyTrigger(KeyInput.KEY_PGUP));
        this.app.getInputManager().addMapping("Decrease step", new KeyTrigger(KeyInput.KEY_PGDN));
        this.app.getInputManager().addMapping("Ctrl", new KeyTrigger(KeyInput.KEY_LCONTROL));
        this.app.getInputManager().addMapping("Ctrl", new KeyTrigger(KeyInput.KEY_RCONTROL));
        
        this.app.getInputManager().addListener(actionListener, keyboardMappings);
        this.app.getInputManager().addListener(analogListener, mouseMappings);
    }
    
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);

    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Console") && !keyPressed) {
                dragToRotate = !dragToRotate;
                if (dragToRotate == false) { //focus on 3d world
                    stateManager.getState(LocalScreen.class).getScreen().getFocusHandler().resetFocusElements();
                } else {
                    //screen.getFocusHandler().setKeyFocus( niftyConsole);
                    stateManager.getState(LocalScreen.class).getNiftyConsole().setFocus();
                }
                app.getFlyByCamera().setDragToRotate(dragToRotate);
            } else if(name.equals("Subdivide") && !keyPressed){
                stateManager.getState(Engine.class).onSubdivideOctantRequest();
            } else if(name.equals("Increase step") && !keyPressed){
                stateManager.getState(Engine.class).increaseStep();
            } else if(name.equals("Decrease step") && !keyPressed){
                stateManager.getState(Engine.class).decreaseStep();
            } else if(name.equals("MouseSelect") && keyPressed){
                stateManager.getState(Engine.class).onMouseSelect();
            } else if(name.equals("Ctrl") && keyPressed){
                ctrlPressed = true;
            } else if(name.equals("Ctrl") && !keyPressed){
                ctrlPressed = false;
            } 
        }
    };
    
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("WheelUp")) {
                if(ctrlPressed)
                    stateManager.getState(Engine.class).decreaseStep();
                else
                    stateManager.getState(Engine.class).onExtrudeOctantRequest();
                
            } else if (name.equals("WheelDown")) {
                if(ctrlPressed)
                    stateManager.getState(Engine.class).increaseStep();
                else
                    stateManager.getState(Engine.class).onDeleteOctantRequest();
            } else if(name.equals("MouseMove")){
                stateManager.getState(Engine.class).onRefreshSelection();
            }
        }
    };
}
