package zimnycat.utilrun.base;

import com.google.gson.*;
import net.minecraft.client.util.InputUtil;
import zimnycat.utilrun.libs.FileLib;

import java.util.Arrays;

public class BindCmd extends CommandBase {
    public BindCmd() { super("bind", "Bind " + Utilrun.name + " command"); }

    @Override
    public void run(String[] args) {
        if (args.length <= 1) {
            clientMessage("Syntax: \"" + Utilrun.highlight("bind <key> <add/clear/get>") + "\"");
            return;
        }

        String code = null;
        try {
            code = String.valueOf(InputUtil.fromTranslationKey("key.keyboard." + args[0].toLowerCase()).getCode());
        } catch (Exception e) { clientMessage("Invalid key"); }
        if (code == null) return;

        if (!FileLib.path.resolve("binds.json").toFile().exists()) {
            FileLib.createFile("binds.json");
            FileLib.write("binds.json", "{}", FileLib.WriteMode.OVERWRITE);
        }

        StringBuilder sb = new StringBuilder();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileLib.read("binds.json").forEach(sb::append);
        JsonObject data = JsonParser.parseString(sb.toString()).getAsJsonObject();

        switch (args[1]) {
            case "add" -> {
                StringBuilder builder = new StringBuilder();
                for (String s : args) if (Arrays.asList(args).indexOf(s) > 1) builder.append(" " + s);
                if (builder.length() == 0) {
                    clientMessage("Syntax: \"" + Utilrun.highlight("bind <key> <add> <" + Utilrun.name + " command>") + "\"");
                    return;
                }

                if (!data.has(code)) {
                    JsonArray array = new JsonArray();
                    array.add(builder.substring(1));
                    data.add(code, array);
                }
                else data.get(code).getAsJsonArray().add(builder.substring(1));

                FileLib.write("binds.json", gson.toJson(data), FileLib.WriteMode.OVERWRITE);
                clientMessage(
                        "Bound " + Utilrun.highlight("\"" + builder.substring(1) + "\"") + " to " + Utilrun.highlight(args[0] + " (" + code + ")")
                );
            } case "clear" -> {
                data.remove(code);
                FileLib.write("binds.json", gson.toJson(data), FileLib.WriteMode.OVERWRITE);
                clientMessage("Cleared " + Utilrun.highlight(args[0] + " (" + code + ")"));
            } case "get" -> {
                if (data.has(code)) {
                    clientMessage("Command(s) bound to " + Utilrun.highlight(args[0] + " (" + code + ")" + ":"));
                    data.get(code).getAsJsonArray().forEach(element -> clientMessage(element.getAsString()));
                }
                else clientMessage("No commands bound to " + Utilrun.highlight(args[0] + " (" + code + ")"));
            }
        }
    }
}
