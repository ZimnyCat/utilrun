package zimnycat.utilrun.base.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import zimnycat.utilrun.base.SettingBase;
import zimnycat.utilrun.Utilrun;

public class SettingBool extends SettingBase {
    public boolean value;

    public SettingBool(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public void setValue(boolean value) {
        this.value = value;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(
                Utilrun.highlight(">> ") + name + Utilrun.highlight(" = ") + value
        ));
    }
}
