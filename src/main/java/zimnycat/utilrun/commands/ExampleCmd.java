package zimnycat.utilrun.commands;

import zimnycat.utilrun.base.CommandBase;
import zimnycat.utilrun.Utilrun;

public class ExampleCmd extends CommandBase {
    public ExampleCmd() { super("example", "Print a message"); }

    public void run(String[] args) {
        if (args.length == 0) {
            clientMessage("Syntax: \"" + Utilrun.highlight("example <message>") + "\"");
            return;
        }

        clientMessage(String.join(" ", args));
    }
}
