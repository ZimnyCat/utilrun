package zimnycat.utilrun.base;

import com.google.common.eventbus.Subscribe;
import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zimnycat.utilrun.Utilrun;
import zimnycat.utilrun.base.commands.AliasCmd;
import zimnycat.utilrun.base.commands.BindCmd;
import zimnycat.utilrun.base.commands.UtilCmd;
import zimnycat.utilrun.base.settings.SettingBool;
import zimnycat.utilrun.base.settings.SettingNum;
import zimnycat.utilrun.base.settings.SettingString;
import zimnycat.utilrun.events.ClientStopEvent;
import zimnycat.utilrun.events.KeyPressEvent;
import zimnycat.utilrun.events.SendPacketEvent;
import zimnycat.utilrun.libs.ModFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Manager {
    static MinecraftClient mc = MinecraftClient.getInstance();
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<CommandBase> commands = new ArrayList<>();
    public static List<UtilBase> utils = new ArrayList<>();

    public static void loadData() {
        commands.add(new AliasCmd());
        commands.add(new BindCmd());
        commands.add(new UtilCmd());

        Utilrun.logger.info("Loading utils data");
        ModFile modFile = new ModFile("utils.json");
        if (modFile.readAsList().isEmpty()) modFile.write("{}", ModFile.WriteMode.OVERWRITE);
        JsonObject data = JsonParser.parseString(modFile.readAsString()).getAsJsonObject();

        utils.forEach(u -> {
            JsonElement je = data.get(u.getName().toLowerCase());
            if (je != null) {
                if (je.getAsJsonObject().get("enabled").getAsBoolean()) u.setEnabled(true);
                u.getSettings().forEach(s -> {
                    JsonElement val = je.getAsJsonObject().get(s.name.toLowerCase());
                    if (val != null) {
                        if (s instanceof SettingNum) s.num().value = val.getAsDouble();
                        if (s instanceof SettingString) s.string().value = val.getAsString();
                        if (s instanceof SettingBool) s.bool().value = val.getAsBoolean();
                    }
                });
            }
        });
    }

    @Subscribe
    public void command(SendPacketEvent event) {
        if (!(event.getPacket() instanceof ChatMessageC2SPacket)) return;
        String msg = ((ChatMessageC2SPacket) event.getPacket()).chatMessage();

        if (!msg.startsWith(Utilrun.prefix)) return;
        event.setCancelled(true);

        if (msg.equals(Utilrun.prefix)) {
            mc.inGameHud.getChatHud().addMessage(Text.of(
                    Utilrun.highlight(">> ") + "Commands (" + Utilrun.highlight(String.valueOf(commands.size())) + "):"
            ));
            commands.forEach(c -> mc.inGameHud.getChatHud().addMessage(Text.of(
                    Utilrun.highlight(">> ") + c.getName() + Utilrun.highlight(" - ") + c.getDesc()
            )));
            return;
        }

        String[] split = msg.replace(Utilrun.prefix, "").split(" ");

        ModFile modFile = new ModFile("aliases.json");
        if (modFile.readAsList().isEmpty()) modFile.write("{}", ModFile.WriteMode.OVERWRITE);
        JsonObject aliases = JsonParser.parseString(modFile.readAsString()).getAsJsonObject();

        aliases.entrySet().forEach(alias -> {
            if (split[0].equals(alias.getKey())) split[0] = alias.getValue().getAsString();
        });

        runCommand(String.join(" ", split));
    }

    @Subscribe
    public void saveUtilsData(ClientStopEvent event) {
        Utilrun.logger.info("Saving utils data");
        JsonObject data = new JsonObject();
        utils.forEach(u -> {
            JsonObject util = new JsonObject();
            util.addProperty("enabled", u.isEnabled());
            u.getSettings().forEach(s -> {
                if (s instanceof SettingNum) util.addProperty(s.name.toLowerCase(), s.num().value);
                if (s instanceof SettingString) util.addProperty(s.name.toLowerCase(), s.string().value);
                if (s instanceof SettingBool) util.addProperty(s.name.toLowerCase(), s.bool().value);
            });
            data.add(u.getName().toLowerCase(), util);
        });
        ModFile modFile = new ModFile("utils.json");
        modFile.write(gson.toJson(data), ModFile.WriteMode.OVERWRITE);
    }

    @Subscribe
    public void keyPress(KeyPressEvent event) {
        ModFile modFile = new ModFile("binds.json");
        if (modFile.readAsList().isEmpty()) modFile.write("{}", ModFile.WriteMode.OVERWRITE);
        String key = String.valueOf(event.getKey());
        JsonArray array = JsonParser.parseString(modFile.readAsString()).getAsJsonObject().getAsJsonArray(key);
        if (array != null) array.forEach(element -> runCommand(element.getAsString()));
    }

    public static void runCommand(String cmd) {
        mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.GRAY + cmd));
        String[] split = cmd.split(" ");
        try {
            Optional<CommandBase> c = commands.stream().filter(command -> command.getName().startsWith(split[0])).findFirst();
            if (c.isPresent()) c.get().run(split);
            else
                mc.inGameHud.getChatHud().addMessage(Text.of(Utilrun.highlight(">> ") + "No such command! Try " + Utilrun.highlight(Utilrun.prefix)));
        } catch (Exception e) {
            mc.inGameHud.getChatHud().addMessage(Text.of(Utilrun.highlight(">> ") + "Exception caught! Check logs for more info."));
        }
    }

    public static UtilBase getUtilByName(String uName) {
        return utils.stream().filter(util -> util.getName().toLowerCase().startsWith(uName.toLowerCase())).findFirst().orElse(null);
    }
}
