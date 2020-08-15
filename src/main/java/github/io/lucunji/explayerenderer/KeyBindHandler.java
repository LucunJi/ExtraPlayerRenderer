package github.io.lucunji.explayerenderer;

import github.io.lucunji.explayerenderer.client.render.screen.GuiConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;

public class KeyBindHandler implements ClientTickEvents.StartTick {

    @Override
    public void onStartTick(MinecraftClient client) {
        if (client.skipGameRender || MinecraftClient.getInstance().world == null) return;

        if (Main.MASTER_CONTROL.wasPressed() && client.currentScreen == null) {
            client.openScreen(new GuiConfig());
        }
    }
}
