package github.io.lucunji.explayerenderer;

import github.io.lucunji.explayerenderer.api.config.ConfigPersistence;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.config.GsonConfigPersistence;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {

    public static final String MOD_ID = "explayerenderer";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(Main.MOD_ID).get().getMetadata().getName();

    public static final KeyBinding CONFIG_KEY = new KeyBinding(
            "key.%s.openMenu".formatted(MOD_ID),
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            "key.%s.category".formatted(MOD_ID));
    public static final Configs CONFIGS  = new Configs();
    public static final ConfigPersistence CONFIG_PERSISTENCE = new GsonConfigPersistence(FabricLoader.getInstance().getConfigDir().resolve(Main.MOD_ID + "_v3.json"));

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        Main.CONFIG_PERSISTENCE.load(Main.CONFIGS.getOptions());
        KeyBindingHelper.registerKeyBinding(CONFIG_KEY);
    }
}
