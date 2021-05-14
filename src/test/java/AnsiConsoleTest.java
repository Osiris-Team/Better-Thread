
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.Test;

import static org.fusesource.jansi.Ansi.ansi;

public class AnsiConsoleTest {

    @Test
    public static void test() {
        AnsiConsole.systemInstall();
        System.out.println(ansi().bg(Ansi.Color.WHITE).fg(Ansi.Color.GREEN).a("Initialised").reset());
        if (AnsiConsole.out()!=null){
            System.out.println("Success!");
        }
    }

}
