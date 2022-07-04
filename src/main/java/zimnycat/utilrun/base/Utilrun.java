package zimnycat.utilrun.base;

import com.google.common.eventbus.EventBus;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zimnycat.utilrun.libs.FileLib;

public class Utilrun implements ModInitializer {
	public static String name = "utilrun";
	public static String prefix = ">>";

	public static final Logger logger = LoggerFactory.getLogger(name);
	public static EventBus bus = new EventBus();

	@Override
	public void onInitialize() {
		if (!FileLib.path.toFile().exists()) FileLib.path.toFile().mkdirs();

		Manager manager = new Manager();
		manager.init();
		bus.register(manager);
	}

	public static String highlight(String str) { return Formatting.GREEN + str + Formatting.WHITE; }
}
