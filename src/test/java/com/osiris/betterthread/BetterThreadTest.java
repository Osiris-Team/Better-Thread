package com.osiris.betterthread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class BetterThreadTest {

    @Test
    void getPercent() throws Exception {
        BetterThread thread = new BetterThread(new BetterThreadManager());
        thread.setNow(30);
        assertEquals(30, thread.getPercent());
        thread.setNow(0);
        assertEquals(0, thread.getPercent());
    }
}