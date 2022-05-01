package com.osiris.betterthread.modules;

import com.osiris.betterthread.BThread;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BThreadPrinter;

import static org.fusesource.jansi.Ansi.ansi;

public class Status implements BThreadPrinterModule {

    @Override
    public void append(BThreadManager manager, BThreadPrinter printer, BThread thread, StringBuilder line) {
        final String name = thread.getName();
        final long now = thread.getNow();
        final byte percent = thread.getPercent();
        final String status = thread.getStatus();

        if (now > 0) {
            if (thread.isSkipped()) line.append(ansi().a("[" + name + "] " + status));
            else line.append(ansi().a("[" + name + "][" + percent + "%] " + status));
        } else {
            line.append(ansi().a("[" + name + "] " + status));
        }
        line.append(ansi().reset());
    }
}
