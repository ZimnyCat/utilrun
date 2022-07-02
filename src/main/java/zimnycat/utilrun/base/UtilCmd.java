package zimnycat.utilrun.base;

import zimnycat.utilrun.base.settings.SettingBool;
import zimnycat.utilrun.base.settings.SettingNum;
import zimnycat.utilrun.base.settings.SettingString;

public class UtilCmd extends CommandBase {
    public UtilCmd() { super("util", "Manage utils"); }

    @Override
    public void run(String[] args) {
        if (args.length == 0) {
            clientMessage("Syntax: \"" + Utilrun.highlight("util toggle/list/settings") + "\"");
            return;
        }

        switch (args[0]) {
            case "toggle" -> {
                if (args.length == 1) {
                    clientMessage("Syntax: \"" + Utilrun.highlight("util toggle <utility name>") + "\"");
                    return;
                }
                UtilBase util = Manager.getUtilByName(args[1]);
                if (util == null) {
                    clientMessage("\"" + args[1].toLowerCase() + "\" util not found");
                    return;
                }
                util.toggle();
            }
            case "list" -> {
                clientMessage("Utils (" + Utilrun.highlight(String.valueOf(Manager.utils.size())) + "):");
                Manager.utils.forEach(u -> clientMessage(u.getName() + Utilrun.highlight(" - ")
                        + (u.isEnabled() ? "ON" : "OFF") +  Utilrun.highlight(" - ") + u.getDesc()));
            }
            case "settings" -> {
                if (args.length == 1) {
                    clientMessage("Syntax: \"" + Utilrun.highlight("util settings <module> <setting name> <value>") + "\"");
                    return;
                }
                UtilBase util = Manager.getUtilByName(args[1]);
                if (util == null) {
                    clientMessage("\"" + args[1].toLowerCase() + "\" util not found");
                    return;
                }
                switch (args.length) {
                    case 2 -> {
                        if (util.getSettings().isEmpty()) {
                            clientMessage(util.getName() + " has no settings");
                            return;
                        }
                        for (SettingBase setting : util.getSettings()) {
                            if (setting instanceof SettingNum)
                                clientMessage(setting.name + Utilrun.highlight(" = ") + setting.num().value
                                        + " (" + setting.num().getMin() + Utilrun.highlight("-") + setting.num().getMax() + ")");
                            if (setting instanceof SettingString)
                                clientMessage(setting.name + Utilrun.highlight(" = ") + "\"" + setting.string().value + "\"");
                            if (setting instanceof SettingBool)
                                clientMessage(setting.name + Utilrun.highlight(" = ") + setting.bool().value);
                        }
                    }
                    case 3, 4, 5 -> {
                        SettingBase setting = util.setting(args[2]);
                        if (setting instanceof SettingNum) {
                            if (args.length == 3)
                                clientMessage(setting.name + Utilrun.highlight(" = ") + setting.num().value);
                            else {
                                try {
                                    double val = Double.parseDouble(args[3]);
                                    if (isValid(setting.num(), val) || (args.length == 5 && args[4].equalsIgnoreCase("force")))
                                        setting.num().value = val;
                                    else clientMessage("Invalid value");
                                } catch (Exception e) {
                                    clientMessage("Not a number");
                                }
                            }
                        } if (setting instanceof SettingString) {
                            if (args.length == 3)
                                clientMessage(setting.name + Utilrun.highlight(" = ") + "\"" + setting.string().value + "\"");
                            else setting.string().value = args[3];
                        } if (setting instanceof SettingBool) {
                            if (args.length == 3)
                                clientMessage(setting.name + Utilrun.highlight(" = ") + setting.bool().value);
                            else {
                                if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false"))
                                    setting.bool().value = Boolean.parseBoolean(args[3]);
                                else if (args[3].equalsIgnoreCase("toggle"))
                                    setting.bool().value = !setting.bool().value;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isValid(SettingNum setting, double val) { return val >= setting.getMin() && val <= setting.getMax(); }
}