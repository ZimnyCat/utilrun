package zimnycat.utilrun.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zimnycat.utilrun.base.Manager;
import zimnycat.utilrun.base.settings.SettingBool;
import zimnycat.utilrun.base.settings.SettingNum;
import zimnycat.utilrun.base.settings.SettingString;
import zimnycat.utilrun.libs.FileLib;

@Mixin(MinecraftClient.class)
public class MMinecraftClient {
    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
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
        FileLib.write("settings.json", gson.toJson(data), FileLib.WriteMode.OVERWRITE);
    }
}
