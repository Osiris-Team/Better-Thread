package com.osiris.betterthread.jline;

import com.osiris.betterthread.Constants;
import org.junit.jupiter.api.Test;

import java.io.*;

class MyPrintStreamTest {


    @Test
    void maxBuffer() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))){
            for (int i = 0; i < 100000; i++) {
                writer.write(""+i);
                writer.newLine();
            }
            System.out.println(out.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void bufferStuff() {
        PrintStream old = System.out;
        MyPrintStream myPrintStream = new MyPrintStream();
        System.setOut(myPrintStream);
        System.out.println("TEST");
        // TODO
    }
}