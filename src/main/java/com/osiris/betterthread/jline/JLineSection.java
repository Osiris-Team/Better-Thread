package com.osiris.betterthread.jline;

import org.jline.terminal.Size;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.osiris.betterthread.Constants.TERMINAL;

/**
 * Manages terminal line updates.
 * Since the limit of lines for one single {@link Display} is the terminals height,
 * we need this class. <br>
 * There is no limit of lines we can update with this class.
 */
public class JLineSection {
    private Map<Display, List<AttributedString>> displayAndLines = new HashMap<>();

    /**
     * This map contains the {@link Display}s and their lines.
     */
    public Map<Display, List<AttributedString>> getDisplayAndLines() {
        return displayAndLines;
    }

    public JLineSection() {
        this(null);
    }

    /**
     * Init with at least one display. <br>
     * @param display If null we create and init a new one.
     */
    public JLineSection(Display display) {
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
     * The regular {@link Display#update(List, int)} method fails when there are
     * more lines to update, than the terminal has rows (terminals height). <br>
     * This method does the following: <br>
     * If we have more lines than the terminal rows,
     * instead of only using one {@link Display} we use as many as we need to
     * display all the lines from the newLines list.
     * @param newLines Old lines get replaced with these.
     * @param targetCursorPos To start at the start of each line use -1.
     */
    public synchronized void update(List<AttributedString> newLines, int targetCursorPos) {

        int currentHeight = newLines.size();
        int maxHeight = TERMINAL.getSize().getRows();
        if (!(maxHeight<=0) && currentHeight > maxHeight){
            // maxHeight is not allowed to be 0 or smaller than 0 and
            // currentHeight must be bigger than maxHeight

            int loopCount = 0;
            int startIndex = 0; // Where to start splitting
            int endIndex = 0; // Where to end splitting

            while(currentHeight > maxHeight){
                endIndex = endIndex + maxHeight;
                setValueAtIndex(loopCount, newLines.subList(startIndex, endIndex));
                startIndex = endIndex + 1; // +1 so we start at the next char in the next loop
                currentHeight = currentHeight - maxHeight;
                loopCount++;
            }

            // Add the last small part of the list
            setValueAtIndex(loopCount, newLines.subList(startIndex, newLines.size() - 1 )); // -1 because we need the last lines index position
        }

        // Finally do the updating
        displayAndLines.forEach((display, lines) -> {
            display.update(lines, targetCursorPos);
        });
    }

    /**
     * Sets the displayAndLines maps value at the given index. <br>
     * If the index doesn't exist a new key ({@link Display}) is created and initialised ({@link #initAndGetNewDisplay()}) <br>
     * Note that this key won't be created a the given index, but
     * at the next free position of the map. <br>
     */
    private void setValueAtIndex(int index, List<AttributedString> newLines){

        Display display = null;
        int i = 0;
        for (Display d :
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

    private Display initAndGetNewDisplay(){
        try{
            Display display = new Display(TERMINAL, false);
            Size size = TERMINAL.getSize(); // Need to initialize the size on the display with
            display.resize(size.getRows(), size.getColumns());
            return display;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Display!");
        }
    }


}
