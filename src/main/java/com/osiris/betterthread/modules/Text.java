package com.osiris.betterthread.modules;

import com.osiris.betterthread.BThread;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BThreadPrinter;
import org.fusesource.jansi.Ansi;

public class Text implements BThreadPrinterModule {
    public Ansi content;

    public Text(Ansi content) {
        this.content = content;
    }

    @Override
    public void append(BThreadManager manager, BThreadPrinter printer, BThread thread, StringBuilder line) {
        line.append(content);
    }
}
