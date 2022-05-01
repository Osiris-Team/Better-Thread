package com.osiris.betterthread.modules;

import com.osiris.betterthread.BThread;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BThreadPrinter;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class Spinner implements BThreadPrinterModule {
    public Ansi frame1 = ansi().a("[\\]");
    public Ansi frame2 = ansi().a("[|]");
    public Ansi frame3 = ansi().a("[/]");
    public Ansi frame4 = ansi().a("[-]");
    public Ansi frameSkip = ansi().fg(WHITE).a("[#]").reset();
    public Ansi frameSuccess = ansi().fg(GREEN).a("[#]").reset();
    public Ansi frameFail = ansi().fg(RED).a("[#]").reset();

    private Ansi frameWarn;
    private int currentFrame = 0;

    @Override
    public void append(BThreadManager manager, BThreadPrinter printer, BThread thread, StringBuilder line) {
        if (!thread.isFinished()) {
            switch (currentFrame) {
                case 0:
                    line.append(frame1);
                    currentFrame++;
                    break;
                case 1:
                    line.append(frame2);
                    currentFrame++;
                    break;
                case 2:
                    line.append(frame3);
                    currentFrame++;
                    break;
                default:
                    line.append(frame4);
                    currentFrame = 0;
            }
        } else {
            if (thread.isSkipped()) line.append(frameSkip);
            else if (!thread.getWarnList().isEmpty())
                line.append((frameWarn = ansi().fg(YELLOW).a("[" + thread.getWarnList().size() + "]").reset()));
            else if (thread.isSuccess()) line.append(frameSuccess);
            else line.append(frameFail);
        }
    }
}
