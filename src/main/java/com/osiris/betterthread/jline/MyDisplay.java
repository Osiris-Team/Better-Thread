package com.osiris.betterthread.jline;


import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.osiris.betterthread.Constants.MY_DISPLAY;
import static com.osiris.betterthread.Constants.TERMINAL;

public class MyDisplay extends Display {
    private List<MyLine> allLines = new ArrayList<>(); // Note that lines should never get removed from this list!
    Thread systemOutListenerThread;

    public MyDisplay(Terminal terminal, boolean fullscreen) {
        super(terminal, fullscreen);
        initSystemOutListener();
    }

    private void initSystemOutListener() {
        if (systemOutListenerThread==null){
            systemOutListenerThread = new Thread(()->{

            });
            systemOutListenerThread.start();
        }
    }


    private synchronized int addLineToLinesListAndGetPosition(MyLine s){
        allLines.add(s);
        return allLines.size()-1;
    }

    private synchronized void setLineInLinesList(int index, MyLine s){
        allLines.set(index, s);
    }

    private List<AttributedString> convertToAttributedStringList(List<MyLine> myLineList){
        List<AttributedString> attributedStringList = new ArrayList<>();
        for (MyLine s :
                myLineList) {
            attributedStringList.add(s.getAttributedString());
        }
        return attributedStringList;
    }

    /**
     * Gets the newest lines position.
     */
    public synchronized int getNewestLinesPosition(){
        return allLines.size()-1;
    }

    public synchronized void add(String... lines){
        addLines(lines);
    }

    public synchronized void addLines(String... stringLines){
        Objects.requireNonNull(stringLines);

        MyLine[] myLines = new MyLine[stringLines.length];
        for (int i = 0; i < stringLines.length; i++) {
            myLines[i] = new MyLine(stringLines[i]);
        }
        addLines(myLines);
    }

    public synchronized void addLines(MyLine... lines){
        updateLines(lines);
    }

    /**
     * Updates the provided lines.
     * Lines get added if they weren't yet.
     * @param lines
     */
    public synchronized void updateLines(MyLine... lines){
        Objects.requireNonNull(lines);

        for (MyLine l :
                lines) {

            // Check if l exists in the allLines list.
            // To be more exact. We check if l's position in the list exists.
            // If it doesn't, l gets added to the end of the allLines list.
            // Also note that its desired position is ignored in that case.
            // Also note, that when its position is -1 it gets added to the end of the list no matter what.
            MyLine existingLine = null;
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
        }


        // erase the lines
        //this.update(Collections.emptyList(), 0);
        this.update(convertToAttributedStringList(allLines), -1);
    }

    public synchronized void updateLine(int position, String newLine) {
        updateLines(new MyLine(newLine, position));
    }
}
