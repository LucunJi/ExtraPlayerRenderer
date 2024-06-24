package github.io.lucunji.explayerenderer;

import github.io.lucunji.explayerenderer.config.Configs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Main implements ClientModInitializer {

    public static final String MOD_ID = "explayerenderer";


    @Override
    public void onInitializeClient() {
        var configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + MOD_ID + ".openMenu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "key." + MOD_ID + ".category"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (configKey.wasPressed()) {
                client.setScreen(Configs.genGui(client.currentScreen));
            }
        });
    }
}
