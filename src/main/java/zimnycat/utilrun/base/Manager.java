package zimnycat.utilrun.base;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import zimnycat.utilrun.base.settings.SettingBool;
import zimnycat.utilrun.base.settings.SettingNum;
import zimnycat.utilrun.base.settings.SettingString;
import zimnycat.utilrun.events.SendPacketEvent;
import zimnycat.utilrun.libs.FileLib;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    static MinecraftClient mc = MinecraftClient.getInstance();

    public static List<CommandBase> commands = new ArrayList<>();
    public static List<UtilBase> utils = new ArrayList<>();

    public void init() {
        Utilrun.logger.info("Loading commands");
        commands.add(new UtilCmd());
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

        try {
            StringBuilder sb = new StringBuilder();
            FileLib.read("settings.json").forEach(sb::append);
            JsonObject data = JsonParser.parseString(sb.toString()).getAsJsonObject();

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
        } catch (Exception e) {
            FileLib.createFile("settings.json");
        }
    }

    @Subscribe
    public void command(SendPacketEvent event) {
        if (!(event.getPacket() instanceof ChatMessageC2SPacket)) return;
        String msg = ((ChatMessageC2SPacket) event.getPacket()).getChatMessage();

        if (!msg.startsWith(Utilrun.prefix)) return;
        event.setCancelled(true);

        if (msg.equals(Utilrun.prefix)) {
            mc.inGameHud.getChatHud().addMessage(Text.of(
                    "Commands (" + Utilrun.highlight(String.valueOf(Manager.commands.size())) + "):"
            ));
            Manager.commands.forEach(c -> mc.inGameHud.getChatHud().addMessage(Text.of(
                    c.getName() + Utilrun.highlight(" - ") + c.getDesc()
            )));
            return;
        }

        runCommand(msg.replace(Utilrun.prefix, ""));
    }

    public static void runCommand(String cmd) {
        String[] split = cmd.split(" ");
        commands.forEach(c -> {
            if (c.getName().equals(split[0])) {
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
