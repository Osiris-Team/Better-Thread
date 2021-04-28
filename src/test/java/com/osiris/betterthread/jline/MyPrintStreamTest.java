package com.osiris.betterthread.jline;

import com.osiris.betterthread.Constants;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MyPrintStreamTest {

    private static final MyDisplay MY_DISPLAY = Constants.MY_DISPLAY;

    @Test
    void bufferStuff() {
        PrintStream old = System.out;
        MyPrintStream myPrintStream = new MyPrintStream(old, MY_DISPLAY);
        System.setOut(myPrintStream);
        // TODO
    }
}