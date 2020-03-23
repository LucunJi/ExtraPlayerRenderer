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

        posX += Main.OFFSET_X.get().orElse(0);
        posY += Main.OFFSET_Y.get().orElse(0);
        size *= Main.SIZE.get().orElse(1d);
        boolean mirror = Main.MIRROR.get().orElse(false);

        if (player.isInSneakingPose()) posY += Main.SNEAKING_OFFSET_Y.get().orElse(-30d).floatValue();
        if (player.isFallFlying()) posY += Main.ELYTRA_OFFSET_Y.get().orElse(-120d).floatValue();

        GlStateManager.enableColorMaterial();
        {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(posX, posY, 50.0F);
            GlStateManager.scalef(size * (mirror ? 1 : -1), size, size);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);

            float field_6283 = player.field_6283;
            float yaw = player.yaw;
            float pitch = player.pitch;
            float prevHeadYaw = player.prevHeadYaw;
            float headYaw = player.headYaw;
            float handSwingProgress = player.handSwingProgress;
            float lastHandSwingProgress = player.lastHandSwingProgress;
            int hurtTime = player.hurtTime;

            GlStateManager.rotatef(Main.LIGHT_DEGREE.get().orElse(0d).floatValue(), 0.0F, 1.0F, 0.0F);
            DiffuseLighting.enable();
            GlStateManager.rotatef(-Main.LIGHT_DEGREE.get().orElse(0d).floatValue(), 0.0F, 1.0F, 0.0F);

            player.field_6283 = MathHelper.clamp(player.field_6283, Main.BODY_YAW_MIN.get().orElse(0d).floatValue(), Main.BODY_YAW_MAX.get().orElse(0d).floatValue());
            player.headYaw = MathHelper.clamp(player.headYaw, Main.HEAD_YAW_MIN.get().orElse(-15d).floatValue(), Main.HEAD_YAW_MAX.get().orElse(-15d).floatValue());
            player.pitch = MathHelper.clamp(player.pitch, Main.PITCH_MIN.get().orElse(-20d).floatValue(), Main.PITCH_MAX.get().orElse(20d).floatValue());
            if (Main.SWING_HANDS.get().orElse(true)) {
                player.handSwingProgress = player.getHandSwingProgress(client.getTickDelta());
                player.lastHandSwingProgress = player.getHandSwingProgress(client.getTickDelta());
            } else {
                player.handSwingProgress = 0;
                player.lastHandSwingProgress = 0;
            }

            if (!Main.HURT_FLASH.get().orElse(true)) {
                player.hurtTime = 0;
            }

            GlStateManager.translatef(0.0F, 0.0F, 0.0F);
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
            entityRenderDispatcher.method_3945(180.0F);
            entityRenderDispatcher.setRenderShadows(false);
            entityRenderDispatcher.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0f, true);
            entityRenderDispatcher.setRenderShadows(true);

            player.field_6283 = field_6283;
            player.yaw = yaw;
            player.pitch = pitch;
            player.prevHeadYaw = prevHeadYaw;
            player.headYaw = headYaw;
            player.handSwingProgress = handSwingProgress;
            player.lastHandSwingProgress = lastHandSwingProgress;
            player.hurtTime = hurtTime;
        }

        GlStateManager.popMatrix();
        DiffuseLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
}
