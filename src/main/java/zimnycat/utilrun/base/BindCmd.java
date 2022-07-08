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
            clientMessage("Syntax: \"" + Utilrun.highlight("bind <key> <client command/clear>") + "\"");
            return;
        }

        String code = String.valueOf(InputUtil.fromTranslationKey("key.keyboard." + args[0].toLowerCase()).getCode());
        if (!FileLib.path.resolve("binds.json").toFile().exists()) {
            FileLib.createFile("binds.json");
            FileLib.write("binds.json", "{}", FileLib.WriteMode.OVERWRITE);
        }

        StringBuilder sb = new StringBuilder();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileLib.read("binds.json").forEach(sb::append);
        JsonObject data = JsonParser.parseString(sb.toString()).getAsJsonObject();

        if (args[1].equalsIgnoreCase("clear")) {
            data.remove(code);
            FileLib.write("binds.json", gson.toJson(data), FileLib.WriteMode.OVERWRITE);
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (String s : args) if (Arrays.asList(args).indexOf(s) != 0) builder.append(" " + s);

        if (!data.has(code)) {
            JsonArray array = new JsonArray();
            array.add(builder.substring(1));
            data.add(code, array);
        }
        else data.get(code).getAsJsonArray().add(builder.substring(1));

        FileLib.write("binds.json", gson.toJson(data), FileLib.WriteMode.OVERWRITE);
    }
}
