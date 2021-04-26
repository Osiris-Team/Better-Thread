package com.osiris.betterthread.jline;

import org.fusesource.jansi.Ansi;
import org.jline.utils.AttributedString;

/**
 * Wraps around {@link AttributedString} to provide
 * more functionality. <br>
 * See {@link #MyLine(AttributedString, int)} for details.
 */
public class MyLine {
    private AttributedString attributedString;
    private int position;

    /**
     * Sets its position to -1. <br>
     * See {@link #MyLine(AttributedString, int)} for details.
     * @param str Can be a regular or {@link Ansi} String.
     */
    public MyLine(String str) {
        this(str, -1);
    }

    /**
     * Creates an {@link AttributedString} out of the provided String. <br>
     * See {@link #MyLine(AttributedString, int)} for details.
     * @param str Can be a regular or {@link Ansi} String.
     * @param position Any negative value (ex. -1), will cause this
     *                 object to get added to the {@link MyDisplay}s allLines lists end.
     */
    public MyLine(String str, int position) {
        this(AttributedString.fromAnsi(str), position);
    }

    /**
     * Creates a {@link MyLine} which holds an {@link AttributedString} and its position
     * (in the {@link MyDisplay} allLines list).
     * @param attributedString Must be an {@link AttributedString}.
     * @param position Any negative value (ex. -1), will cause this
     *                 object to get added to the {@link MyDisplay}s allLines lists end.
     */
    public MyLine(AttributedString attributedString, int position) {
        this.attributedString = attributedString;
        this.position = position;
    }


    public AttributedString getAttributedString() {
        return attributedString;
    }

    public void setAttributedString(AttributedString attributedString) {
        this.attributedString = attributedString;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
