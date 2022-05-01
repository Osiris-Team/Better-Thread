package com.osiris.betterthread.modules;

import com.osiris.betterthread.BThread;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BThreadPrinter;

public interface BThreadPrinterModule {
    void append(BThreadManager manager, BThreadPrinter printer, BThread thread, StringBuilder line);
}
