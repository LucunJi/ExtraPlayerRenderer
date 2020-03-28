package github.io.lucunji.explayerenderer.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import github.io.lucunji.explayerenderer.config.Configs;
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

        PlayerEntity player = client.world.getPlayers().stream().filter(p -> p.getName().getString().equals(Configs.PLAYER_NAME.getStringValue())).findFirst().orElse(client.player);

        double posX = Configs.OFFSET_X.getDoubleValue() * scaledWidth;
        double posY = Configs.OFFSET_Y.getDoubleValue() * scaledHeight;
        double size = Configs.SIZE.getDoubleValue() * scaledHeight;
        boolean mirror = Configs.MIRROR.getBooleanValue();

        if (player.isInSneakingPose()) posY += Configs.SNEAKING_OFFSET_Y.getDoubleValue();
        if (player.isFallFlying()) posY += Configs.ELYTRA_OFFSET_Y.getDoubleValue();

        double lightDegree = Configs.LIGHT_DEGREE.getDoubleValue();

        GlStateManager.enableColorMaterial();
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(posX, posY, 50.0F);
            GlStateManager.scaled(size * (mirror ? 1 : -1), size, size);
            GlStateManager.rotated(180.0F, 0.0F, 0.0F, 1.0F);

            float bodyYaw = player.field_6283;
            float yaw = player.yaw;
            float pitch = player.pitch;
            float prevHeadYaw = player.prevHeadYaw;
            float headYaw = player.headYaw;
            float handSwingProgress = player.handSwingProgress;
            float horizontalSpeed = player.horizontalSpeed;
            int hurtTime = player.hurtTime;

            GlStateManager.rotated(lightDegree, 0.0F, 1.0F, 0.0F);
            DiffuseLighting.enable();
            GlStateManager.rotated(-lightDegree, 0.0F, 1.0F, 0.0F);

            player.field_6283 = (float) MathHelper.clamp(player.field_6283, Configs.BODY_YAW_MIN.getDoubleValue(), Configs.BODY_YAW_MAX.getDoubleValue());
            player.headYaw = (float) MathHelper.clamp(player.headYaw, Configs.HEAD_YAW_MIN.getDoubleValue(), Configs.HEAD_YAW_MAX.getDoubleValue());
            player.pitch = (float) (MathHelper.clamp(player.pitch, Configs.PITCH_MIN.getDoubleValue(), Configs.PITCH_MAX.getDoubleValue()) + Configs.PITCH_OFFSET.getDoubleValue());
            if (Configs.SWING_HANDS.getBooleanValue()) {
                player.handSwingProgress = player.getHandSwingProgress(client.getTickDelta());
            } else {
                player.handSwingProgress = 0;
            }

            if (!Configs.HURT_FLASH.getBooleanValue()) {
                player.hurtTime = 0;
            }

            GlStateManager.translated(0.0F, 0.0F, 0.0F);
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
            entityRenderDispatcher.method_3945(180.0F);
            entityRenderDispatcher.setRenderShadows(false);
            entityRenderDispatcher.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0f, true);
            entityRenderDispatcher.setRenderShadows(true);

            player.field_6283 = bodyYaw;
            player.yaw = yaw;
            player.pitch = pitch;
            player.prevHeadYaw = prevHeadYaw;
            player.headYaw = headYaw;
            player.handSwingProgress = handSwingProgress;
            player.hurtTime = hurtTime;
            player.horizontalSpeed = horizontalSpeed;
        }

        GlStateManager.popMatrix();
        DiffuseLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
}
