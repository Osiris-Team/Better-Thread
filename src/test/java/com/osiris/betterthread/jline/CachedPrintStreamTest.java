package com.osiris.betterthread.jline;

import com.osiris.betterthread.Constants;
import org.junit.jupiter.api.Test;

import java.io.*;

class CachedPrintStreamTest {


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

}