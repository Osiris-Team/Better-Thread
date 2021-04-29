package com.osiris.betterthread;

import com.osiris.betterthread.jline.JLineSection;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.Display;

import java.io.IOException;

public class Constants {

    public static Terminal TERMINAL;
    public static Display DISPLAY;
    public static JLineSection MY_DISPLAY;

    static {
        // Init the main system Terminal:
        try {
            TERMINAL = TerminalBuilder.terminal();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize the Terminal!");
        }
    }
}
