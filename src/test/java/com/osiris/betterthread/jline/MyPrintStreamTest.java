package com.osiris.betterthread.jline;

import com.osiris.betterthread.Constants;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;

class MyPrintStreamTest {

    private static final JLineSection MY_DISPLAY = Constants.MY_DISPLAY;

    @Test
    void bufferStuff() {
        PrintStream old = System.out;
        MyPrintStream myPrintStream = new MyPrintStream(old);
        System.setOut(myPrintStream);
        System.out.println("TEST");
        // TODO
    }
}