package com.osiris.betterthread.jline;

import java.io.*;
import java.util.Formatter;
import java.util.Locale;

/**
 * Caches everything send to this PrintStream
 * instead of printing it.
 */
public class CachedPrintStream extends PrintStream {
    /**
     * Should contain everything that was written by this classes methods. <br>
     * 'Should', because since Java 14+ there are other methods that can be used to write stuff. <br>
     * If those methods are used, stuff gets written to {@link #cache2}. <br>
     */
    private final StringBuilder cache1;
    /**
     * Contains information if Java 14+ methods were used to write stuff. <br>
     * Otherwise all the information you'll need is inside {@link #cache1}.
     */
    private final ByteArrayOutputStream cache2;
    private Formatter formatter;

    public CachedPrintStream() {
        this(new StringBuilder(), new ByteArrayOutputStream());
    }

    public CachedPrintStream(StringBuilder cache1, ByteArrayOutputStream cache2) {
        super(cache2);
        this.cache1 = cache1;
        this.cache2 = cache2;
    }

    /**
     * See {@link #cache1} for details.
     */
    public String getCache1() {
        return cache1.toString();
    }

    /**
     * See {@link #cache2} for details.
     */
    public ByteArrayOutputStream getCache2() {
        return cache2;
    }


    @Override
    public void flush() {
        // Does nothing
    }

    /*
    THESE METHODS CAN'T GET OVERWRITTEN BECAUSE
    THEY WERE ADDED IN JAVA 14.
    THIS MEANS THAT INFORMATION WRITTEN TO
    THIS STREAM VIA THOSE METHODS GETS LOST!

    @Override
    public void write(byte[] buf) throws IOException {
        super.write(buf);
    }

    @Override
    public void writeBytes(byte[] buf) {
        super.writeBytes(buf);
    }

     */

    @Override
    public void write(int b) {
        synchronized (this) {
            try (CharArrayWriter writer = new CharArrayWriter()) {
                writer.write(b);
                cache1.append(writer);
            }
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        try {
            synchronized (this) {
                try (ByteArrayOutputStream tempOut = new ByteArrayOutputStream()) {
                    tempOut.write(buf, off, len);
                    cache1.append(tempOut);
                }
            }
        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            //trouble = true;
        }
    }

    private void write(String s) {
        synchronized (this) {
            cache1.append(s);
        }
    }

    private void writeln(String s) {
        synchronized (this) {
            cache1.append(s);
            cache1.append(System.lineSeparator());
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


    @Override
    public void println() {
        writeln("");
    }

    @Override
    public void println(boolean x) {
        writeln(String.valueOf(x));
    }

    @Override
    public void println(char x) {
        writeln(String.valueOf(x));
    }

    @Override
    public void println(int x) {
        writeln(String.valueOf(x));
    }

    @Override
    public void println(long x) {
        writeln(String.valueOf(x));
    }

    @Override
    public void println(float x) {
        writeln(String.valueOf(x));
    }

    @Override
    public void println(double x) {
        writeln(String.valueOf(x));
    }

    @Override
    public void println(char[] x) {
        writeln(String.valueOf(x));
    }

    @Override
    public void println(String x) {
        writeln(String.valueOf(x));
    }

    @Override
    public void println(Object x) {
        writeln(String.valueOf(x));
    }


    @Override
    public PrintStream printf(String format, Object... args) {
        return this.format(format, args);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        return this.format(l, format, args);
    }

    @Override
    public PrintStream format(String format, Object... args) {
        synchronized (this) {
            if ((formatter == null)
                    || (formatter.locale() !=
                    Locale.getDefault(Locale.Category.FORMAT)))
                formatter = new Formatter((Appendable) this);
            formatter.format(Locale.getDefault(Locale.Category.FORMAT),
                    format, args);
        }
        return this;
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        synchronized (this) {
            if ((formatter == null)
                    || (formatter.locale() != l))
                formatter = new Formatter(this, l);
            formatter.format(l, format, args);
        }
        return this;
    }


    @Override
    public PrintStream append(CharSequence csq) {
        this.print(String.valueOf(csq));
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        if (csq == null) csq = "null";
        return this.append(csq.subSequence(start, end));
    }

    @Override
    public PrintStream append(char c) {
        this.print(c);
        return this;
    }
}
