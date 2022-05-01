package com.osiris.betterthread.modules;

import com.osiris.betterthread.BThread;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BThreadPrinter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.fusesource.jansi.Ansi.ansi;

public class Date implements BThreadPrinterModule {
    public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Date() {
    }

    public Date(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    @Override
    public void append(BThreadManager manager, BThreadPrinter printer, BThread thread, StringBuilder line) {
        line.append(ansi().a("[" + dateFormatter.format(LocalDateTime.now()) + "]")
                .reset());
    }
}
