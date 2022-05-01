package com.osiris.betterthread.modules;

import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to build a printer modules list easier and faster.
 */
public class BuilderBThreadModules {
    public List<BThreadPrinterModule> list = new ArrayList<>();

    public List<BThreadPrinterModule> build() {
        return list;
    }

    public BuilderBThreadModules custom(BThreadPrinterModule module) {
        this.list.add(module);
        return this;
    }

    public BuilderBThreadModules date() {
        list.add(new Date());
        return this;
    }

    public BuilderBThreadModules text(Ansi s) {
        list.add(new Text(s));
        return this;
    }

    public BuilderBThreadModules space() {
        list.add(new Space());
        return this;
    }

    public BuilderBThreadModules spinner() {
        list.add(new Spinner());
        return this;
    }

    public BuilderBThreadModules status() {
        list.add(new Status());
        return this;
    }
}
