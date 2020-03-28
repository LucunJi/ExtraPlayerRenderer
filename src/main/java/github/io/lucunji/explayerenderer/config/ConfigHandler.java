package github.io.lucunji.explayerenderer.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.*;

public class ConfigHandler implements IConfigHandler {
    public static ConfigHandler INSTANCE = new ConfigHandler();

    private static final String FILE_PATH = "./config/explayerenderer.json";
    private static final File CONFIG_DIR = new File("./config");

    @Override
    public void load() {
        File settingFile = new File(FILE_PATH);
        if (settingFile.isFile() && settingFile.exists()) {
            JsonElement jsonElement = JsonUtils.parseJsonFile(settingFile);
            if (jsonElement instanceof JsonObject) {
                ConfigUtils.readConfigBase((JsonObject)jsonElement, "Parameters", Configs.Category.PARAMETERS.getConfigs());
            }
        }
    }

    @Override
    public void save() {
        if ((CONFIG_DIR.exists() && CONFIG_DIR.isDirectory()) || CONFIG_DIR.mkdirs()) {
            JsonObject configRoot = new JsonObject();

            ConfigUtils.writeConfigBase(configRoot, "Parameters", Configs.Category.PARAMETERS.getConfigs());

            JsonUtils.writeJsonToFile(configRoot, new File(FILE_PATH));
        }
    }
}
