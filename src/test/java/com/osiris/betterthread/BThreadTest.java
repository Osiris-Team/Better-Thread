package com.osiris.betterthread;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BThreadTest {

    @Test
    void getPercent() throws Exception {
        BThread thread = new BThread(new BThreadManager());
        thread.setNow(30);
        assertEquals(30, thread.getPercent());
        thread.setNow(0);
        assertEquals(0, thread.getPercent());
    }
}