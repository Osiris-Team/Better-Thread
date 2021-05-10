package com.osiris.betterthread.jline;

import org.jline.terminal.Size;
import org.jline.utils.AttributedString;

import java.util.*;

import static com.osiris.betterthread.Constants.TERMINAL;

// TODO WORK IN PROGRESS
/**
 * Manages terminal line updates.
 * Since the limit of lines for one single {@link org.jline.utils.Display} is the terminals height,
 * we need this class. <br>
 * There is no limit of lines we can update with this class.
 */
public class Display {
    private Map<org.jline.utils.Display, List<AttributedString>> displayAndLines = new HashMap<>();

    /**
     * This map contains the {@link org.jline.utils.Display}s and their lines.
     */
    public Map<org.jline.utils.Display, List<AttributedString>> getDisplayAndLines() {
        return displayAndLines;
    }

    public Display() {
        this(null);
    }

    /**
     * Init with at least one display. <br>
     * @param display If null we create and init a new one.
     */
    public Display(org.jline.utils.Display display) {
        if (display!=null)
            displayAndLines.put(display, new ArrayList<AttributedString>());
        else
            displayAndLines.put(initAndGetNewDisplay(), new ArrayList<AttributedString>());
    }

    /**
     * See {@link #update(List, int)} for details.
     */
    public synchronized void update(List<AttributedString> newLines){
        this.update(newLines, -1);
    }

    /**
     * Replaces the old lines of this section with the provided new ones. <br>
     * Special about this is that there is no limit in lines we can update at a time. <br>
     * The regular {@link org.jline.utils.Display#update(List, int)} method fails when there are
     * more lines to update, than the terminal has rows (terminals height). <br>
     * This method does the following: <br>
     * If we have more lines than the terminal rows,
     * instead of only using one {@link org.jline.utils.Display} we use as many as we need to
     * display all the lines from the newLines list.
     * @param newLines Old lines get replaced with these.
     * @param targetCursorPos The cursors position after updating these lines. <br>
     *                        To set it at the start use -1. <br>
     *                        In other words: <br>
     *                        The terminal is basically a table. It has columns at the
     *                        x axis (horizontal or width) and rows at the y axis (vertical or height). <br>
     *                        One row is a line. That line is a string of chars. Each char
     *                        is inside of one column.
     *                        Now you select the future position of the cursor by
     *                        entering its position (index) on the x axis (width).
     *                        Note: Lets imagine we have a width of 30, then the 30th char is already on the next line.
     */
    public synchronized void update(List<AttributedString> newLines, int targetCursorPos) {

        int currentHeight = newLines.size();
        int maxWidth = TERMINAL.getSize().getColumns();
        int maxHeight = TERMINAL.getSize().getRows() - 20; // -10 because idk
        if (!(maxHeight<=0) && currentHeight > maxHeight){
            // maxHeight is not allowed to be 0 or smaller than 0 and
            // currentHeight must be bigger than maxHeight

            int loopCount = 0;
            int startIndex = 0; // Where to start splitting
            int endIndex = 0; // Where to end splitting

            while(currentHeight > maxHeight){
                endIndex = endIndex + maxHeight;
                System.out.println("SUBLIST: "+startIndex+"-"+endIndex);
                setValueAtIndex(loopCount, newLines.subList(startIndex, endIndex));
                startIndex = endIndex + 1; // +1 so we start at the next char in the next loop
                currentHeight = currentHeight - maxHeight;
                loopCount++;
            }

            // Add the last small part of the list
            setValueAtIndex(loopCount, subList(newLines, startIndex, newLines.size() - 1 )); // -1 because we need the last lines index position
            System.out.println("SUBLIST: "+startIndex+"-"+(newLines.size() - 1));

        }

        // To ensure proper updating of each display

        // Finally do the updating
        displayAndLines.forEach((display, lines) -> {
            // Calculate the targetCursorPosition because it needs to be at the next line
            int totalCharCount = lines.size() * maxWidth; // TODO can happen that one line is actually 2 lines because it length is bigger than maxWidth
            display.update(lines, totalCharCount);
        });
    }

    private List<AttributedString> subList(List<AttributedString> parentList, int startIndex, int endIndex){

        List<AttributedString> subList;
        try{
            subList = parentList.subList(startIndex, endIndex);
        } catch (IllegalArgumentException e) {
            // Example: list has 31 lines, but the max is 30. Lines 0-30 have been matched to a display.
            // Remaining is 1 line. What the code above then does is subList(31, 30) which results in an exception.
            // That's why we simply add the last one like this:
            subList = new ArrayList<>();
            subList.add(parentList.get(parentList.size()-1)); // -1 because we need the last lines index position
        }

        return subList;
    }

    /**
     * Sets the displayAndLines maps value at the given index. <br>
     * If the index doesn't exist a new key ({@link org.jline.utils.Display}) is created and initialised ({@link #initAndGetNewDisplay()}) <br>
     * Note that this key won't be created a the given index, but
     * at the next free position of the map. <br>
     */
    private void setValueAtIndex(int index, List<AttributedString> newLines){

        org.jline.utils.Display display = null;
        int i = 0;
        for (org.jline.utils.Display d :
                displayAndLines.keySet()) {
            if (i==index){
                display = d;
                break;
            }
            i++;
        }

        if(display!=null) // Replace the existing entry
            displayAndLines.replace(display, newLines);
        else // Create a new entry with new display
            displayAndLines.put(initAndGetNewDisplay(), newLines);
    }

    private org.jline.utils.Display initAndGetNewDisplay(){
        try{
            System.out.println("CREATED NEW DISPLAY");
            TERMINAL.writer().println();
            org.jline.utils.Display display = new org.jline.utils.Display(TERMINAL, false);
            Size size = TERMINAL.getSize(); // Need to initialize the size on the display with
            display.resize(size.getRows(), size.getColumns());
            return display;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Display!");
        }
    }


}
