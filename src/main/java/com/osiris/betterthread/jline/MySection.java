package com.osiris.betterthread.jline;

import org.fusesource.jansi.Ansi;
import org.jline.utils.AttributedString;

/**
 * Represents a section of the console. <br>
 * Can contain multiple lines. <br>
 * Each line is represented as {@link AttributedString}. <br>
 * See {@link #MySection(AttributedString[], int)} for details.
 */
public class MySection {
    private AttributedString[] attributedStrings;
    private int position;

    /**
     * Sets its position to -1. <br>
     * See {@link #MySection(AttributedString[], int)} for details.
     * @param line Can be a regular or {@link Ansi} String.
     */
    public MySection(String line) {
        this(line, -1);
    }

    /**
     * Creates an {@link AttributedString} out of the provided String. <br>
     * See {@link #MySection(AttributedString[], int)} for details.
     * @param line Can be a regular or {@link Ansi} String.
     * @param position Any negative value (ex. -1), will cause this
     *                 object to get added to the {@link JLineSection}s allLines lists end.
     */
    public MySection(String line, int position) {
        this(new AttributedString[]{AttributedString.fromAnsi(line)},
                position);
    }

    /**
     * Creates a {@link MySection} which holds an {@link AttributedString} and its position
     * (in the {@link JLineSection} allLines list).
     * @param attributedStrings Must be an {@link AttributedString}.
     * @param position Any negative value (ex. -1), will cause this
     *                 object to get added to the {@link JLineSection}s allLines lists end.
     */
    public MySection(AttributedString[] attributedStrings, int position) {
        this.attributedStrings = attributedStrings;
        this.position = position;
    }

    /**
     * Adds a new line to this section. <br>
     * Can be an {@link Ansi} string. <br>
     * Converts the given String into an {@link AttributedString}.
     */
    public void addLine(String str){
        addLine(AttributedString.fromAnsi(str));
    }

    /**
     * Adds the provided {@link AttributedString} to this section. <br>
     * Note that this alone won't update the console! <br>
     * For that to happen call {@link JLineSection#updateSections(MySection...)}.
     */
    public synchronized void addLine(AttributedString newString){
        AttributedString[] newArray = new AttributedString[attributedStrings.length + 1]; // +1 of space for the new string

        // Copy the current arrays data to the new array
        System.arraycopy(attributedStrings, 0, newArray, 0, attributedStrings.length);

        // Add the new string to the end of the new array
        newArray[newArray.length - 1] = newString;

        // Set the new array
        attributedStrings = newArray;
    }

    /**
     * Convenience method for returning the {@link AttributedString} at index 0.
     */
    public AttributedString getFirstAttributedString(){
        return attributedStrings[0];
    }

    /**
     * If the content fits into the console you can
     * access all of it through {@link #getFirstAttributedString()}. <br>
     * If not then it gets split into multiple {@link AttributedString}s
     * and its content can be accessed through this method.
     */
    public AttributedString[] getAttributedStrings() {
        return attributedStrings;
    }

    /**
     * See {@link #getAttributedStrings()} for details.
     */
    public void setAttributedStrings(AttributedString[] attributedStrings) {
        this.attributedStrings = attributedStrings;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
