package github.io.lucunji.explayerenderer;

import github.io.lucunji.explayerenderer.settings.SettingManager;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public class KeyBindHandler implements ClientTickCallback {

    private boolean lastMasterControlState = false;
    private boolean lastKeyStateM = false;

    @Override
    public void tick(MinecraftClient client) {
        if (client.skipGameRender || MinecraftClient.getInstance().world == null) return;
        if (client.currentScreen != null) return;

        if (Main.MASTER_CONTROL.isPressed()) {
            if (InputUtil.isKeyPressed(client.window.getHandle(), InputUtil.fromName("key.keyboard.up").getKeyCode())) {
                Main.OFFSET_Y.set(Main.OFFSET_Y.get().orElse(0) - 1);
            }
            if (InputUtil.isKeyPressed(client.window.getHandle(), InputUtil.fromName("key.keyboard.down").getKeyCode())) {
                Main.OFFSET_Y.set(Main.OFFSET_Y.get().orElse(0) + 1);
            }
            if (InputUtil.isKeyPressed(client.window.getHandle(), InputUtil.fromName("key.keyboard.left").getKeyCode())) {
                Main.OFFSET_X.set(Main.OFFSET_X.get().orElse(0) - 1);
            }
            if (InputUtil.isKeyPressed(client.window.getHandle(), InputUtil.fromName("key.keyboard.right").getKeyCode())) {
                Main.OFFSET_X.set(Main.OFFSET_X.get().orElse(0) + 1);
            }
            if (InputUtil.isKeyPressed(client.window.getHandle(), InputUtil.fromName("key.keyboard.minus").getKeyCode())) {
                Main.SIZE.set(Main.SIZE.get().orElse(1d) - 0.005);
            }
            if (InputUtil.isKeyPressed(client.window.getHandle(), InputUtil.fromName("key.keyboard.equal").getKeyCode())) {
                Main.SIZE.set(Main.SIZE.get().orElse(1d) + 0.005);
            }
            if (!lastKeyStateM && InputUtil.isKeyPressed(client.window.getHandle(), InputUtil.fromName("key.keyboard.m").getKeyCode())) {
                Main.MIRROR.set(!Main.MIRROR.get().orElse(false));
            }
        }

        if (lastMasterControlState && !Main.MASTER_CONTROL.isPressed()) {
            SettingManager.INSTANCE.save();
        }

        lastKeyStateM = InputUtil.isKeyPressed(client.window.getHandle(), InputUtil.fromName("key.keyboard.m").getKeyCode());
        lastMasterControlState = Main.MASTER_CONTROL.isPressed();
    }
}
