package com.osiris.betterthread;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JLineTests {
    private Terminal terminal;
    private Display display;
    private int progress = 0;

    @Test
    void replaceMultipleLinesTest() throws InterruptedException, IOException {
        terminal = TerminalBuilder.terminal();

        resize();
        for (int i = 0; i < 30; i++) {
            terminal.writer().println("TEST");
        }


        for (int i = 0; i < 5; i++) {
            display.update(Arrays.asList(
                    AttributedString.fromAnsi("Line 1 with data " + i),
                    AttributedString.fromAnsi("Line 2 with data " + i)
            ), -1); //-1
            Thread.sleep(1000);
        }
    }

    private void resize(){
        display = new Display(terminal, false);
        Size size = terminal.getSize(); // Need to initialize the size on the display with
        display.resize(size.getRows(), size.getColumns());
    }

}
