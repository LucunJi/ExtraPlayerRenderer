package github.io.lucunji.explayerenderer.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import github.io.lucunji.explayerenderer.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class PlayerHUD extends DrawableHelper {
    private static final MinecraftClient client  = MinecraftClient.getInstance();

    public void render(int ticks) {
        int scaledWidth = client.window.getScaledWidth();
        int scaledHeight = client.window.getScaledHeight();

        PlayerEntity player = client.player;

        float posX = 60;
        float posY = scaledHeight * 1.5f;
        float size = scaledHeight / 2f;

        posX += Main.Settings.OFFSET_X.get().orElse(0);
        posY += Main.Settings.OFFSET_Y.get().orElse(0);
        size *= Main.Settings.SIZE.get().orElse(1d);
        boolean mirror = Main.Settings.MIRROR.get().orElse(false);

        float headYaw = -15;
        float pitchRange = 20;

        if (player.isInSneakingPose()) posY -= 30;
        if (player.isFallFlying()) posY -= 120;

        {
            GlStateManager.enableColorMaterial();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(posX, posY, 50.0F);
            GlStateManager.scalef(size * (mirror ? 1 : -1), size, size);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float f = player.field_6283;
            float g = player.yaw;
            float h = player.pitch;
            float i = player.prevHeadYaw;
            float j = player.headYaw;
            float handSwingProgress = player.handSwingProgress;
            float lastHandSwingProgress = player.lastHandSwingProgress;
            GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
            DiffuseLighting.enable();
            GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            player.field_6283 = 0;
            player.headYaw = headYaw;
            player.pitch = MathHelper.clamp(player.pitch, -pitchRange, pitchRange);
            player.handSwingProgress = player.getHandSwingProgress(client.getTickDelta());
            player.lastHandSwingProgress = player.getHandSwingProgress(client.getTickDelta());
            GlStateManager.translatef(0.0F, 0.0F, 0.0F);
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
            entityRenderDispatcher.method_3945(180.0F);
            entityRenderDispatcher.setRenderShadows(false);
            entityRenderDispatcher.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0f, true);
            entityRenderDispatcher.setRenderShadows(true);
            player.field_6283 = f;
            player.yaw = g;
            player.pitch = h;
            player.prevHeadYaw = i;
            player.headYaw = j;
            player.handSwingProgress = handSwingProgress;
            player.lastHandSwingProgress = lastHandSwingProgress;
            GlStateManager.popMatrix();
            DiffuseLighting.disable();
            GlStateManager.disableRescaleNormal();
            GlStateManager.activeTexture(GLX.GL_TEXTURE1);
            GlStateManager.disableTexture();
            GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        }
    }
}
