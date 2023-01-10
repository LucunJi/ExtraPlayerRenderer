package github.io.lucunji.explayerenderer;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import github.io.lucunji.explayerenderer.client.render.PlayerHUDRenderer;
import github.io.lucunji.explayerenderer.config.ConfigHandler;
import github.io.lucunji.explayerenderer.config.Configs;
import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {

    public static final String MOD_ID = "explayerenderer";

    public static final PlayerHUDRenderer PLAYER_HUD_RENDERER = new PlayerHUDRenderer();

    @Override
    public void onInitializeClient() {
        ConfigManager.getInstance().registerConfigHandler(MOD_ID, new ConfigHandler());
        //noinspection InstantiationOfUtilityClass
        new Configs();  // just load the class and run static code block
        ConfigHandler.loadFile();
        InputEventHandler.getKeybindManager().registerKeybindProvider(new KeybindProvider());
        Configs.MENU_OPEN_KEY.getKeybind().setCallback(new KeyBindHandler());
        RenderEventHandler.getInstance().registerGameOverlayRenderer(PLAYER_HUD_RENDERER);
    }
}
