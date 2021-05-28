package github.io.lucunji.explayerenderer.client.render;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.malilib.interfaces.IRenderer;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class PlayerHUDRenderer extends DrawableHelper implements IRenderer {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onRenderGameOverlayPost(float partialTicks) {
        if (client.skipGameRender || client.currentScreen != null) return;
        this.doRender(partialTicks);
    }

    public void doRender(float tickDelta) {
        if (client.world == null || client.player == null || !Configs.ENABLED.getBooleanValue()) return;
        LivingEntity targetEntity = client.world.getPlayers().stream().filter(p -> p.getName().getString().equals(Configs.PLAYER_NAME.getStringValue())).findFirst().orElse(client.player);
        if (Configs.SPECTATOR_AUTO_SWITCH.getBooleanValue() && client.player.isSpectator()) {
            Entity cameraEntity = MinecraftClient.getInstance().getCameraEntity();
            if (cameraEntity instanceof LivingEntity) {
                targetEntity = (LivingEntity) cameraEntity;
            } else if (cameraEntity != null) {
                return;
            }
        }

        int scaledWidth = client.window.getScaledWidth();
        int scaledHeight = client.window.getScaledHeight();
        double posX = Configs.OFFSET_X.getDoubleValue() * scaledWidth;
        double posY = Configs.OFFSET_Y.getDoubleValue() * scaledHeight;
        if (targetEntity.isInSneakingPose()) posY += Configs.SNEAKING_OFFSET_Y.getDoubleValue();
        if (targetEntity.isFallFlying()) posY += Configs.ELYTRA_OFFSET_Y.getDoubleValue();
        double size = Configs.SIZE.getDoubleValue() * scaledHeight;
        boolean mirror = Configs.MIRROR.getBooleanValue();
        double lightDegree = Configs.LIGHT_DEGREE.getDoubleValue();

        GlStateManager.enableColorMaterial();
        {
            GlStateManager.pushMatrix();
            GlStateManager.translated(posX, posY, -500.0F);
            GlStateManager.scaled(size * (mirror ? 1 : -1), size, size);
            GlStateManager.rotated(180.0F, 0.0F, 0.0F, 1.0F);

            /* *************** data storing *************** */
            float bodyYaw = targetEntity.field_6283;
            float pitch = targetEntity.pitch;
            float prevBodyYaw = targetEntity.field_6220;
            float prevHeadYaw = targetEntity.prevHeadYaw;
            float prevPitch = targetEntity.prevPitch;
            float headYaw = targetEntity.headYaw;
            float handSwingProgress = targetEntity.handSwingProgress;
            int hurtTime = targetEntity.hurtTime;

            GlStateManager.rotated(lightDegree, 0.0F, 1.0F, 0.0F);
            DiffuseLighting.enable();
            GlStateManager.rotated(-lightDegree, 0.0F, 1.0F, 0.0F);

            targetEntity.field_6220 = targetEntity.field_6283 = (float) MathHelper.clamp(targetEntity.field_6283, Configs.BODY_YAW_MIN.getDoubleValue(), Configs.BODY_YAW_MAX.getDoubleValue());
            targetEntity.prevHeadYaw = targetEntity.headYaw = (float) MathHelper.clamp(targetEntity.headYaw, Configs.HEAD_YAW_MIN.getDoubleValue(), Configs.HEAD_YAW_MAX.getDoubleValue());
            targetEntity.prevPitch = targetEntity.pitch = (float) (MathHelper.clamp(targetEntity.pitch, Configs.PITCH_MIN.getDoubleValue(), Configs.PITCH_MAX.getDoubleValue()) + Configs.PITCH_OFFSET.getDoubleValue());

            if (!Configs.SWING_HANDS.getBooleanValue())
                targetEntity.handSwingProgress = 0;

            if (!Configs.HURT_FLASH.getBooleanValue()) {
                targetEntity.hurtTime = 0;
            }

            GlStateManager.rotatef(((float) Configs.ROTATION_X.getDoubleValue()), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(((float) Configs.ROTATION_Y.getDoubleValue()), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(((float) Configs.ROTATION_Z.getDoubleValue()), 0.0F, 0.0F, 1.0F);

            GlStateManager.translated(0.0F, 0.0F, 0.0F);
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
            entityRenderDispatcher.method_3945(180.0F);

            boolean renderShadow = entityRenderDispatcher.shouldRenderShadows();
            entityRenderDispatcher.setRenderShadows(renderShadow);
            entityRenderDispatcher.render(targetEntity, 0.0D, 0.0D, 0.0D, 0.0F, tickDelta, true);
            entityRenderDispatcher.setRenderShadows(renderShadow);

            /* *************** data restoring *************** */
            targetEntity.field_6283 = bodyYaw;
            targetEntity.pitch = pitch;
            targetEntity.field_6220 = prevBodyYaw;
            targetEntity.prevHeadYaw = prevHeadYaw;
            targetEntity.prevPitch = prevPitch;
            targetEntity.headYaw = headYaw;
            targetEntity.handSwingProgress = handSwingProgress;
            targetEntity.hurtTime = hurtTime;
        }

        GlStateManager.popMatrix();
        DiffuseLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
}
