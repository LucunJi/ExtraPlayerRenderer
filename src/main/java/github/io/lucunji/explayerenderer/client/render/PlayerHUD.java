package github.io.lucunji.explayerenderer.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public class PlayerHUD extends DrawableHelper {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public void render(int ticks) {
        PlayerEntity player;
        if (Configs.SPECTATOR_AUTO_SWITCH.getBooleanValue()) {
            Entity cameraEntity = MinecraftClient.getInstance().getCameraEntity();
            if (cameraEntity instanceof PlayerEntity && !cameraEntity.isSpectator()) {
                player = (PlayerEntity)MinecraftClient.getInstance().getCameraEntity();
            } else {
                return;
            }
        } else {
            player = client.world.getPlayers().stream().filter(p -> p.getName().getString().equals(Configs.PLAYER_NAME.getStringValue())).findFirst().orElse(client.player);
        }

        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();
        double posX = Configs.OFFSET_X.getDoubleValue() * scaledWidth;
        double posY = Configs.OFFSET_Y.getDoubleValue() * scaledHeight;
        if (player.isInSneakingPose()) posY += Configs.SNEAKING_OFFSET_Y.getDoubleValue();
        if (player.isFallFlying()) posY += Configs.ELYTRA_OFFSET_Y.getDoubleValue();
        double size = Configs.SIZE.getDoubleValue() * scaledHeight;
        boolean mirror = Configs.MIRROR.getBooleanValue();

        RenderSystem.pushMatrix();
        {
            RenderSystem.translatef((float) posX, (float) posY, 1050.0F);
            RenderSystem.scalef(1.0F, 1.0F, -1.0F);
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.translate(0.0D, 0.0D, 2000.0D);
            matrixStack.scale((float) size * (mirror ? 1 : -1), (float) size, (float) size);
            Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
            Quaternion quaternion2 = Vector3f.POSITIVE_X.getDegreesQuaternion(0);
            quaternion.hamiltonProduct(quaternion2);
            matrixStack.multiply(quaternion);

            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((float)Configs.ROTATION_X.getDoubleValue()));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)Configs.ROTATION_Y.getDoubleValue()));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)Configs.ROTATION_Z.getDoubleValue()));

            float bodyYaw = player.bodyYaw;
            float yaw = player.yaw;
            float pitch = player.pitch;
            float prevHeadYaw = player.prevHeadYaw;
            float headYaw = player.headYaw;
            float handSwingProgress = player.handSwingProgress;
            int hurtTime = player.hurtTime;
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();

            player.bodyYaw = 180 - (float) MathHelper.clamp(player.bodyYaw, Configs.BODY_YAW_MIN.getDoubleValue(), Configs.BODY_YAW_MAX.getDoubleValue());
            player.headYaw = 180 - (float) MathHelper.clamp(player.headYaw, Configs.HEAD_YAW_MIN.getDoubleValue(), Configs.HEAD_YAW_MAX.getDoubleValue());
            player.pitch = (float) (MathHelper.clamp(player.pitch, Configs.PITCH_MIN.getDoubleValue(), Configs.PITCH_MAX.getDoubleValue()) + Configs.PITCH_OFFSET.getDoubleValue());

            if (Configs.SWING_HANDS.getBooleanValue()) {
                player.handSwingProgress = player.getHandSwingProgress(client.getTickDelta());
            } else {
                player.handSwingProgress = 0;
            }

            if (!Configs.HURT_FLASH.getBooleanValue()) {
                player.hurtTime = 0;
            }

            quaternion2.conjugate();
            entityRenderDispatcher.setRotation(quaternion2);
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            entityRenderDispatcher.getRenderer(player).render(player, yaw, 1, matrixStack, immediate, 15728880);
            immediate.draw();

            player.bodyYaw = bodyYaw;
            player.yaw = yaw;
            player.pitch = pitch;
            player.prevHeadYaw = prevHeadYaw;
            player.headYaw = headYaw;
            player.handSwingProgress = handSwingProgress;
            player.hurtTime = hurtTime;
        }
        RenderSystem.popMatrix();
    }
}
