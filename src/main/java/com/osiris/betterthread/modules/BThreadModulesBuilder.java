package com.osiris.betterthread.modules;

import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to build a printer modules list easier and faster.
 */
public class BThreadModulesBuilder {
    public List<BThreadPrinterModule> list = new ArrayList<>();

    public List<BThreadPrinterModule> build() {
        return list;
    }

    public BThreadModulesBuilder custom(BThreadPrinterModule module) {
        this.list.add(module);
        return this;
    }

    public BThreadModulesBuilder date() {
        list.add(new Date());
        return this;
    }

    public BThreadModulesBuilder text(Ansi s) {
        list.add(new Text(s));
        return this;
    }

    public BThreadModulesBuilder space() {
        list.add(new Space());
        return this;
    }

    public BThreadModulesBuilder spinner() {
        list.add(new Spinner());
        return this;
    }

    public BThreadModulesBuilder status() {
        list.add(new Status());
        return this;
    }
}
