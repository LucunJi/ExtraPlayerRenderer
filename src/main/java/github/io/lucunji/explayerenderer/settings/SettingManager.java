package github.io.lucunji.explayerenderer.settings;

import com.google.common.collect.Maps;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;

public class SettingManager {
    public static SettingManager INSTANCE = new SettingManager();

    private static final String FILE_PATH = "./config/explayerenderer.json";
    private static final File CONFIG_DIR = new File("./config");
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, AbstractSetting> SETTINGS = Maps.newHashMap();

    public Map<String, AbstractSetting> getSettings() {
        return SETTINGS;
    }

    public void load() {
        File settingFile = new File(FILE_PATH);
        if (settingFile.isFile() && settingFile.exists()) {
            try (FileReader fileReader = new FileReader(settingFile)) {
                JsonReader jsonReader = new JsonReader(new BufferedReader(fileReader));
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String key = jsonReader.nextName();
                    AbstractSetting setting = SETTINGS.get(key);
                    if (setting != null) {
                        setting.read(jsonReader);
                    }
                }
                jsonReader.endObject();
            } catch (Exception e) {
                LOGGER.error("Error occurred during saving settings for ChatUtil.");
                e.printStackTrace();
            }
        }
    }

    public void save() {
        if (!CONFIG_DIR.exists())
            CONFIG_DIR.mkdirs();
        try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
            JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(fileWriter));
            jsonWriter.setIndent("    ");
            jsonWriter.setLenient(true);
            jsonWriter.beginObject();
            for (Map.Entry<String, AbstractSetting> entry : SETTINGS.entrySet()) {
                String key = entry.getKey();
                AbstractSetting setting = entry.getValue();
                jsonWriter.name(key);
                setting.write(jsonWriter);
            }
            jsonWriter.endObject();
            jsonWriter.flush();
        } catch (Exception e) {
            LOGGER.error("Error occurred during saving settings for ChatUtil.");
            e.printStackTrace();
        }
    }
}
