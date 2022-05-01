package com.osiris.betterthread.modules;

import org.fusesource.jansi.Ansi;

public class Space extends Text {
    public Space() {
        super(Ansi.ansi().a(" "));
    }
}
