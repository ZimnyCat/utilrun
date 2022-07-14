package zimnycat.utilrun.base;

import com.google.common.eventbus.Subscribe;
import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import zimnycat.utilrun.base.settings.SettingBool;
import zimnycat.utilrun.base.settings.SettingNum;
import zimnycat.utilrun.base.settings.SettingString;
import zimnycat.utilrun.events.ClientStopEvent;
import zimnycat.utilrun.events.KeyPressEvent;
import zimnycat.utilrun.events.SendPacketEvent;
import zimnycat.utilrun.libs.ModFile;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    static MinecraftClient mc = MinecraftClient.getInstance();

    public static List<CommandBase> commands = new ArrayList<>();
    public static List<UtilBase> utils = new ArrayList<>();

    public void init() {
        Utilrun.logger.info("Loading commands");
        commands.add(new UtilCmd());
        commands.add(new BindCmd());
        Reflections ref = new Reflections(this.getClass().getPackage().getName().replace("base", "commands"));
        ref.getSubTypesOf(CommandBase.class).forEach(clazz -> {
            try {
                commands.add(clazz.getConstructor().newInstance());
            } catch (Exception e) {
                Utilrun.logger.info("Failed to load " + clazz.getSimpleName());
            }
        });

        Utilrun.logger.info("Loading utils");
        ref = new Reflections(this.getClass().getPackage().getName().replace("base", "utils"));
        ref.getSubTypesOf(UtilBase.class).forEach(clazz -> {
            try {
                utils.add(clazz.getConstructor().newInstance());
            } catch (Exception e) {
                Utilrun.logger.info("Failed to load " + clazz.getSimpleName());
            }
        });

        Utilrun.logger.info("Loading settings");
        ModFile modFile = new ModFile("settings.json");
        if (modFile.readAsList().isEmpty()) modFile.write("{}", ModFile.WriteMode.OVERWRITE);
        JsonObject data = JsonParser.parseString(modFile.readAsString()).getAsJsonObject();

        utils.forEach(u -> {
            JsonElement je = data.get(u.getName().toLowerCase());
            if (je != null) {
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
        String msg = ((ChatMessageC2SPacket) event.getPacket()).getChatMessage();

        if (!msg.startsWith(Utilrun.prefix)) return;
        event.setCancelled(true);

        if (msg.equals(Utilrun.prefix)) {
            mc.inGameHud.getChatHud().addMessage(Text.of(
                    Utilrun.highlight(">> ") + "Commands (" + Utilrun.highlight(String.valueOf(Manager.commands.size())) + "):"
            ));
            Manager.commands.forEach(c -> mc.inGameHud.getChatHud().addMessage(Text.of(
                    Utilrun.highlight(">> ") + c.getName() + Utilrun.highlight(" - ") + c.getDesc()
            )));
            return;
        }

        runCommand(msg.replace(Utilrun.prefix, ""));
    }

    @Subscribe
    public void saveSettings(ClientStopEvent event) {
        Utilrun.logger.info("Saving settings");
        JsonObject data = new JsonObject();
        Manager.utils.forEach(u -> {
            if (!u.getSettings().isEmpty()) {
                JsonObject settings = new JsonObject();
                u.getSettings().forEach(s -> {
                    if (s instanceof SettingNum) settings.addProperty(s.name.toLowerCase(), s.num().value);
                    if (s instanceof SettingString) settings.addProperty(s.name.toLowerCase(), s.string().value);
                    if (s instanceof SettingBool) settings.addProperty(s.name.toLowerCase(), s.bool().value);
                });
                data.add(u.getName().toLowerCase(), settings);
            }
        });
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ModFile modFile = new ModFile("settings.json");
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
        String[] split = cmd.split(" ");
        commands.forEach(c -> {
            if (c.getName().equals(split[0])) {
                mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.GRAY + cmd));

                try { c.run(ArrayUtils.remove(split, 0)); }
                catch (Exception e) {
                    c.clientMessage("Exception caught! Check logs for more info.");
                    e.printStackTrace();
                }
            }
        });
    }

    public static UtilBase getUtilByName(String uName) {
        for (UtilBase util : utils) if (util.getName().equalsIgnoreCase(uName)) return util;
        return null;
    }
}
