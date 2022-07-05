package zimnycat.utilrun.libs;

import net.minecraft.client.MinecraftClient;
import zimnycat.utilrun.base.Utilrun;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileLib {
    public static Path path = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), Utilrun.name, "/");

    public static void write(String fileName, String content, WriteMode mode) {
        Path filePath = path.resolve(fileName);
        try {
            createFile(filePath.getFileName().toString());
            String oldContent = (mode == WriteMode.APPEND ? new String(Files.readAllBytes(filePath)) : "");
            FileWriter fw = new FileWriter(filePath.toFile());
            fw.write(oldContent + content);
            fw.close();
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    public static List<String> read(String fileName) {
        try {
            return Files.readAllLines(path.resolve(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void createFile(String fileName) {
        try {
            if (!path.resolve(fileName).toFile().exists()) Files.createFile(path.resolve(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum WriteMode {
        OVERWRITE,
        APPEND
    }
}
