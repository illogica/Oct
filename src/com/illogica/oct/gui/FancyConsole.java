/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.illogica.oct.gui;

import com.illogica.oct.server.Server;
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
        consoleCommands.registerCommand("setmovespeed speed", new SetMoveSpeedCommand());
        consoleCommands.registerCommand("start", new StartCommand());
        consoleCommands.registerCommand("stop", new StopCommand());

        // finally enable command completion
        consoleCommands.enableCommandCompletion(true);
        console.output("Hello :) \\#fa0#press ESC to switch between console and 3d world");
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
