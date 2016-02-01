package com.illogica.oct;

import com.illogica.oct.engine.GeometryGenerators;
import com.illogica.oct.states.LocalScreen;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

/**
 * Main class
 *
 * @author Loris
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {

        Main app = new Main();
        
        //APP SETTINGS
        AppSettings settings = new AppSettings(true);
        settings.setSettingsDialogImage("");
        settings.setTitle("Cooptree");
        settings.setResolution(1366, 768);
        settings.setSamples(8);
        
        //last line on this paragraph:
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        LocalScreen lss = new LocalScreen();
        stateManager.attach(lss);
    
        //initCrossHairs();

        GeometryGenerators.initialize(assetManager);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /**
     * A centred plus sign to help the player aim.
     * TODO: Delete this function, it's useless
     */
    protected void initCrossHairs() {
    //BitmapFont _guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
}
