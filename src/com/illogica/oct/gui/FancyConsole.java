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
package com.illogica.oct.gui;

import com.illogica.oct.server.Server;
import com.illogica.oct.states.Engine;
import com.illogica.oct.states.KeysSelect;
import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.Network;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.ConsoleCommands;
import de.lessvoid.nifty.controls.ConsoleCommands.ConsoleCommand;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton to manage the hud console behavior. There is only one console in
 * the whole application
 *
 * @author Loris
 */
public class FancyConsole implements Commands {

    private Console console;
    private Nifty nifty;
    private SimpleApplication app;
    private Client client;
    private Server server;

    public FancyConsole(Console console, Nifty nifty, SimpleApplication app) {
        this.console = console;
        this.nifty = nifty;
        this.app = app;
        ConsoleCommands consoleCommands = new ConsoleCommands(nifty, console);

        consoleCommands.registerCommand("clear", new ClearCommand());
        ConnectCommand connectCommand = new ConnectCommand();
        consoleCommands.registerCommand("connect", connectCommand);
        consoleCommands.registerCommand("connect address", connectCommand);
        consoleCommands.registerCommand("connect address port", connectCommand);
        consoleCommands.registerCommand("quit", new QuitCommand());
        consoleCommands.registerCommand("setmat mat_id", new SetMaterialCommand());
        consoleCommands.registerCommand("setmovespeed speed", new SetMoveSpeedCommand());
        consoleCommands.registerCommand("start", new StartCommand());
        consoleCommands.registerCommand("stop", new StopCommand());

        // finally enable command completion
        consoleCommands.enableCommandCompletion(true);
        console.output("Hello :) \\#fa0#press F1 to switch between console and 3d world");
    }
    
    private void exitConsole(){
        app.getStateManager().getState(KeysSelect.class).switchTo3dEditMode();
    }

    public void print(String line) {
        console.outputError(line);
    }

    @Override
    public void clear() {
        console.clear();
    }

    @Override
    public int connect() {
        try {
            client = Network.connectToServer("127.0.0.1", Server.PORT);
        } catch (IOException ex) {
            Logger.getLogger(FancyConsole.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        client.start();
        return 0;
    }

    @Override
    public int connect(String host) {
        try {
            client = Network.connectToServer(host, Server.PORT);
        } catch (IOException ex) {
            Logger.getLogger(FancyConsole.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        client.start();
        return 0;
    }

    @Override
    public int connect(String host, int port) {
        try {
            client = Network.connectToServer(host, port);
        } catch (IOException ex) {
            Logger.getLogger(FancyConsole.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        client.start();
        return 0;
    }

    @Override
    public void quit() {
        if (client != null) {
            client.close();
        }
        stop();
        app.stop();
    }

    @Override
    public int start() {
        server = new Server();
        return server.start();
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public void setMoveSpeed(float speed) {
        app.getFlyByCamera().setMoveSpeed(speed);
        exitConsole();
    }

    @Override
    public void setMaterial(int material) {
        app.getStateManager().getState(Engine.class).setMaterial(material);
        exitConsole();
    }

    /**
     * *****
     * COMMANDS THAT CAN BE EXECUTED ON THIS CONSOLE
     */
    class ClearCommand implements ConsoleCommand {

        @Override
        public void execute(String... strings) {
            clear();
        }
    }

    class ConnectCommand implements ConsoleCommand {

        @Override
        public void execute(String... args) {
            if (args.length == 1) {
                if (connect() != 0) {
                    console.output("error");
                }
            } else if (args.length == 2) {
                if (connect(args[1]) != 0) {
                    console.output("error");
                }
            } else if (args.length > 2) {
                if (connect(args[1], Integer.parseInt(args[2])) != 0);
                console.output("error");
            }
        }
    }

    class QuitCommand implements ConsoleCommand {

        @Override
        public void execute(String... strings) {
            quit();
        }
    }
    
    class SetMaterialCommand implements ConsoleCommand {

        @Override
        public void execute(String... strings) {
            if (strings.length == 1) {
                console.output("Missing material id");
            }
            if (strings.length > 1) {
                int material;
                try{
                    material = Integer.parseInt(strings[1]);    
                    setMaterial(material);
                } catch (NumberFormatException e){
                    console.output("Material must be an Integer number");
                }
            }
        }
        
    }

    class SetMoveSpeedCommand implements ConsoleCommand {

        @Override
        public void execute(String... strings) {
            if (strings.length == 1) {
                console.output("Missing speed value");
            }
            if (strings.length > 1) {
                float speed;
                try{
                    speed = Float.parseFloat(strings[1]);    
                    setMoveSpeed(speed);
                } catch (NumberFormatException e){
                    console.output("Speed value must be a number.");
                }
            }
        }
    }
    
    class StartCommand implements ConsoleCommand {

        @Override
        public void execute(String... strings) {
            int retValue = start();
            if (retValue >= 0) {
                console.output("Server started with id " + retValue);
            } else {
                console.output("Error starting server");
            }
        }
    }

    class StopCommand implements ConsoleCommand {

        @Override
        public void execute(String... strings) {
            stop();
        }
    }
}

class ConsoleUninitializedException extends RuntimeException {

    public ConsoleUninitializedException() {
        super();
    }

    public ConsoleUninitializedException(String message) {
        super(message);
    }

    public ConsoleUninitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsoleUninitializedException(Throwable cause) {
        super(cause);
    }
}
