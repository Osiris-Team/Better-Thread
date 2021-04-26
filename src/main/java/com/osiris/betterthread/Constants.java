package com.osiris.betterthread;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.Display;

import java.io.IOException;

public class Constants {

    public static Terminal TERMINAL;
    public static Display DISPLAY;

    static {

        // Init Terminal:
        try {
            TERMINAL = TerminalBuilder.terminal();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize the Terminal!");
        }

        // Init Display:
        try{
            DISPLAY = new Display(TERMINAL, false);
            Size size = TERMINAL.getSize(); // Need to initialize the size on the display with
            DISPLAY.resize(size.getRows(), size.getColumns());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Display!");
        }
    }

    public static void resizeDisplay(){
        Size size = TERMINAL.getSize(); // Need to initialize the size on the display with
        DISPLAY.resize(size.getRows(), size.getColumns());
    }
}
