package com.osiris.betterthread.jline;


import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;

import java.util.*;

import static com.osiris.betterthread.Constants.TERMINAL;

public class JLineSection extends Display {
    private List<MySection> allLines = new ArrayList<>(); // Note that lines should never get removed from this list!

    public JLineSection(Terminal terminal, boolean fullscreen) {
        super(terminal, fullscreen);
    }

    private synchronized int addLineToLinesListAndGetPosition(MySection s){
        allLines.add(s);
        return allLines.size()-1;
    }

    private synchronized void setLineInLinesList(int index, MySection s){
        allLines.set(index, s);
    }

    private List<AttributedString[]> convertToAttributedStringList(List<MySection> mySectionList){
        List<AttributedString[]> attributedStringList = new ArrayList<>();
        for (MySection s :
                mySectionList) {
            attributedStringList.add(s.getAttributedStrings());
        }
        return attributedStringList;
    }

    /**
     * Gets the newest lines position.
     */
    public synchronized int getNewestLinesPosition(){
        return allLines.size()-1;
    }

    /**
     * Convenience method for {@link #addLines(String...)}.
     */
    public synchronized void add(String... lines){
        addLines(lines);
    }

    /**
     * Creates a new {@link MySection}, inserts
     * the provided lines and adds it to the terminal.
     */
    public synchronized void addLines(String... lines){
        Objects.requireNonNull(lines);

        AttributedString[] attrArray = new AttributedString[lines.length];
        for (int i = 0; i < lines.length; i++) {
            attrArray[i] = AttributedString.fromAnsi(lines[i]);
        }
        addSections(new MySection(attrArray, -1));
    }

    /**
     * The same as {@link #updateSections(MySection...)}.
     */
    public synchronized void addSections(MySection... sections){
        updateSections(sections);
    }


    /**
     * Updates the provided {@link MySection}s. <br>
     * Sections get added if they weren't yet.
     * @param sections
     */
    public synchronized void updateSections(MySection... sections){
        Objects.requireNonNull(sections);

        Size size = TERMINAL.getSize();
        int maxAmountOfLines = size.getRows(); // AKA the terminalHeight, is counted in amount of lines from top to bottom of the terminal
        // The terminalWidth is counted in amount of possible chars (styled chars excluded)
        // from the left- to the right-end of the terminal.
        // (Example with width: 100) In this case all chars from 0 to 99 are
        // on the first row and the 100th char on the next row.
        // That's why we do -1 below.
        int maxCharsPerLine = size.getColumns() - 1;
        int maxCharCountInOneUpdate = maxAmountOfLines * maxCharsPerLine;
        int totalCharCountOfAllLines = 0; // We will need this later


        // TODO LINE WRAPPING?
        List<MySection> sectionsEdited = new ArrayList<>();
        // This first loop, searches for lines
        // which exceed the terminals width
        // and breaks them down and adds them to the linesEdited list.
        for (MySection section :
                sections) {

            AttributedString[] copyOfLines = Arrays.copyOf(
                    section.getAttributedStrings(),
                    section.getAttributedStrings().length); // The original array may get edited in the loop below.
            // Loop through each line and check their width.
            // If the lines width is bigger than the width of the terminal:
            // Split the line right there and check the new overflowed line again.
            for (int i = 0; i < copyOfLines.length; i++) {

                AttributedString line = copyOfLines[i];
                int lineWidth = line.columnLength(); // I hope that this method ignores chars responsible for style...
                if (lineWidth > maxCharsPerLine){

                    List<String> splittedLines = new ArrayList<>();
                    int lastStartIndex = 0;
                    while (lineWidth > maxCharsPerLine){
                        int lineOverflow = lineWidth - maxCharsPerLine; // How much l is longer than the width
                        int allowedWidth = lineWidth - lineOverflow; // How much of l can fit in the width
                        //splittedLines.add(line.())
                        // TODO MAKE THIS WORK
                    }

                    //sectionsEdited.addAll(splittedLines);
                }
                else
                    sectionsEdited.add(section);
            }
            for (AttributedString line :
                    copyOfLines) {
            }
        }

        for (MySection l :
                sections) {

            // Check if l exists in the allLines list.
            // To be more exact. We check if l's position in the list exists.
            // If it doesn't, l gets added to the end of the allLines list.
            // Also note that its desired position is ignored in that case.
            // Also note, that when its position is -1 it gets added to the end of the list no matter what.
            MySection existingLine = null;
            try{
                existingLine = allLines.get(l.getPosition()); // Throws an exception if that line doesn't exist
            } catch (Exception ignored) {
            }

            if (existingLine!=null){
                // Means that this line already exists and should get updated
                setLineInLinesList(existingLine.getPosition(), l);
            }
            else{
                // This means that l doesn't exist yet in the list and should be added.
                // Update its position value:
                l.setPosition(addLineToLinesListAndGetPosition(l));
            }


            // We will need this later:
            //totalCharCountOfAllLines = totalCharCountOfAllLines + l.getAttributedStrings().columnLength(); // I guess this method ignores chars responsible for style...
        }




        if (totalCharCountOfAllLines > maxCharCountInOneUpdate){
            // Since the total count of chars of all lines is now bigger than
            // the allowed maximum for one update we need to split that number up until
            // it fits.
            List<List<MySection>> listOfListOfLines = new ArrayList<>(); // Funny
            while(totalCharCountOfAllLines > maxCharCountInOneUpdate){

            }

        }
        //else this.update(convertToAttributedStringList(allLines), -1); // -1 means that it starts at the last written char
    }

    public synchronized void updateLine(int position, String newLine) {
        updateSections(new MySection(newLine, position));
    }




}
