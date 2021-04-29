package com.osiris.betterthread.jline;

import java.io.*;
import java.util.Locale;

/**
 * Caches everything send to this PrintStream
 * instead of printing it.
 */
public class MyPrintStream extends PrintStream {
    private final StringBuilder cache = new StringBuilder();

    public MyPrintStream(OutputStream out) {
        super(out);
    }

    public String getCacheAsString(){
        return cache.toString();
    }

    @Override
    public void flush() {
        // Does nothing
    }

    @Override
    public void write(int b) {
        synchronized (this) {
            try(CharArrayWriter writer = new CharArrayWriter()){
                writer.write(b);
                cache.append(writer);
            }
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        try {
            synchronized (this) {
                try(ByteArrayOutputStream tempOut = new ByteArrayOutputStream()){
                    tempOut.write(buf, off, len);
                    cache.append(tempOut);
                }
            }
        }
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException x) {
            //trouble = true;
        }
    }

    private void write(String s) {
        synchronized (this) {
            cache.append(s);
        }
    }


    @Override
    public void print(boolean b) {
        write(String.valueOf(b));
    }

    @Override
    public void print(char c) {
        write(String.valueOf(c));
    }

    @Override
    public void print(int i) {
        write(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        write(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        write(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        write(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        write(String.valueOf(s));
    }

    @Override
    public void print(String s) {
        write(String.valueOf(s));
    }

    @Override
    public void print(Object obj) {
        write(String.valueOf(obj));
    }

    // TODO BELOW

    @Override
    public void println() {
        //super.println();
    }

    @Override
    public void println(boolean x) {
        //super.println(x);
    }

    @Override
    public void println(char x) {
        //super.println(x);
    }

    @Override
    public void println(int x) {
        //super.println(x);
    }

    @Override
    public void println(long x) {
        //super.println(x);
    }

    @Override
    public void println(float x) {
        //super.println(x);
    }

    @Override
    public void println(double x) {
        //super.println(x);
    }

    @Override
    public void println(char[] x) {
        //super.println(x);
    }

    @Override
    public void println(String x) {
        //super.println(x);
    }

    @Override
    public void println(Object x) {
        //super.println(x);
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        return super.printf(format, args);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        return super.printf(l, format, args);
    }

    @Override
    public PrintStream format(String format, Object... args) {
        return super.format(format, args);
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        return super.format(l, format, args);
    }

    @Override
    public PrintStream append(CharSequence csq) {
        return super.append(csq);
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        return super.append(csq, start, end);
    }

    @Override
    public PrintStream append(char c) {
        return super.append(c);
    }
}
