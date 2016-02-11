/*
 * Copyright (c) 2016, Illogica - Loris Pederiva
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

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

        GeometryGenerators.initialize(assetManager);
       
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
