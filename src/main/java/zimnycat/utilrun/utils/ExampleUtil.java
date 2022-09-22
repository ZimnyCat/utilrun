package zimnycat.utilrun.utils;

import com.google.common.eventbus.Subscribe;
import zimnycat.utilrun.base.UtilBase;
import zimnycat.utilrun.base.settings.SettingBool;
import zimnycat.utilrun.base.settings.SettingNum;
import zimnycat.utilrun.base.settings.SettingString;
import zimnycat.utilrun.events.TickEvent;

public class ExampleUtil extends UtilBase {
    int ticks = 0;

    public ExampleUtil() {
        super(
                "Example", "Prints messages",
                new SettingNum("delay", 40, 10, 100),
                new SettingString("msg", "test"),
                new SettingBool("jump", true)
        );
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (ticks > setting("delay").num().value) {
            clientMessage(setting("msg").string().value);
            if (setting("jump").bool().value) mc.player.jump();
            ticks = 0;
        } else ticks++;
    }
}
