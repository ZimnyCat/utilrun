package zimnycat.utilrun.base.commands;

import zimnycat.utilrun.Utilrun;
import zimnycat.utilrun.base.CommandBase;
import zimnycat.utilrun.base.Manager;

import java.io.File;

public class ConfigCmd extends CommandBase {
    public ConfigCmd() { super("config", "Manage configurations"); }

    @Override
    public void run(String[] args) {
        if (args.length == 0) {
            clientMessage("Syntax: \"" + Utilrun.highlight("config list/save/load <name>.ur") + "\"");
            return;
        }

        switch (args[0]) {
            case "save" -> {
                if (args.length == 1) {
                    clientMessage("Syntax: \"" + Utilrun.highlight("config save <name>.ur") + "\"");
                    return;
                }

                String fileName = args[1].toLowerCase() + (args[1].toLowerCase().endsWith(".ur") ? "" : ".ur");
                Manager.saveConfig(fileName);
                clientMessage(Utilrun.highlight(fileName) + " config saved");
            }
            case "load" -> {
                if (args.length == 1) {
                    clientMessage("Syntax: \"" + Utilrun.highlight("config load <name>.ur") + "\"");
                    return;
                }

                String fileName = args[1].toLowerCase() + (args[1].toLowerCase().endsWith(".ur") ? "" : ".ur");
                Manager.loadConfig(fileName);
                clientMessage(Utilrun.highlight(fileName) + " config loaded");
                Manager.currentFile = fileName;
            }
            case "list" -> {
                clientMessage("Configs:");
                for (File file : Utilrun.path.toFile().listFiles())
                    if (!file.isDirectory() && file.getName().toLowerCase().endsWith(".ur"))
                        clientMessage(Utilrun.highlight(file.getName()));
            }
        }
    }
}
