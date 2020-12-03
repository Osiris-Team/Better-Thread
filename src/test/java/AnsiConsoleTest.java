import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

public class AnsiConsoleTest {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        System.out.println(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.GREEN).a("Initialised").reset());
        if (AnsiConsole.out()!=null){
            System.out.println("Success!");
        }
    }

}
