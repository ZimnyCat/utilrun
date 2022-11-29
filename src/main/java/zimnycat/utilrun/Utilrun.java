package zimnycat.utilrun;

import com.google.common.eventbus.EventBus;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zimnycat.utilrun.base.Manager;
import zimnycat.utilrun.commands.ExampleCmd;
import zimnycat.utilrun.utils.ExampleUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Utilrun implements ModInitializer {
	public static String name = "utilrun";
	public static String prefix = ">>";
	public static Path path = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), Utilrun.name, "/");

	public static final Logger logger = LoggerFactory.getLogger(name);
	public static EventBus bus = new EventBus();

	@Override
	public void onInitialize() {
		if (!path.toFile().exists()) {
			logger.info("Creating " + name + " folder");
			path.toFile().mkdirs();
		}

		Manager.commands.add(new ExampleCmd());

		Manager.utils.add(new ExampleUtil());

		Manager.start();
		bus.register(new Manager());
	}

	public static String highlight(String str) { return Formatting.GREEN + str + Formatting.WHITE; }
}
