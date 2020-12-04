package github.io.lucunji.explayerenderer.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.mixin.EntityInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public class PlayerHUD extends DrawableHelper {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    // TODO: lightDegree
    public void render(int ticks, float tickDelta) {
        if (client.world == null || client.player == null)  {
            return;
        }
        LivingEntity targetEntity = client.world.getPlayers().stream().filter(p -> p.getName().getString().equals(Configs.PLAYER_NAME.getStringValue())).findFirst().orElse(client.player);
        if (Configs.SPECTATOR_AUTO_SWITCH.getBooleanValue() && client.player.isSpectator()) {
            Entity cameraEntity = MinecraftClient.getInstance().getCameraEntity();
            if (cameraEntity instanceof LivingEntity) {
                targetEntity = (LivingEntity)cameraEntity;
            }
            else if (cameraEntity != null) {
                return;
            }
        }

        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();
        double posX = Configs.OFFSET_X.getDoubleValue() * scaledWidth;
        double posY = Configs.OFFSET_Y.getDoubleValue() * scaledHeight;
        if (targetEntity.isInSneakingPose()) posY += Configs.SNEAKING_OFFSET_Y.getDoubleValue();
        if (targetEntity.isFallFlying()) posY += Configs.ELYTRA_OFFSET_Y.getDoubleValue();
        double size = Configs.SIZE.getDoubleValue() * scaledHeight;
        boolean mirror = Configs.MIRROR.getBooleanValue();
//        double lightDegree = Configs.LIGHT_DEGREE.getDoubleValue();

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

            // data storing
            float bodyYaw = targetEntity.bodyYaw;
            float yaw = targetEntity.yaw;
            float pitch = targetEntity.pitch;
            float prevBodyYaw = targetEntity.prevBodyYaw;
            float prevHeadYaw = targetEntity.prevHeadYaw;
            float prevPitch = targetEntity.prevPitch;
            float headYaw = targetEntity.headYaw;
            float handSwingProgress = targetEntity.handSwingProgress;
            int hurtTime = targetEntity.hurtTime;
            int fireTicks = targetEntity.getFireTicks();
            boolean flag0 = ((EntityInvoker)targetEntity).callGetFlag(0);
            ((EntityInvoker)targetEntity).callSetFlag(0, false);

            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
            boolean renderHitbox = entityRenderDispatcher.shouldRenderHitboxes();

            targetEntity.prevBodyYaw = targetEntity.bodyYaw = 180 - (float) MathHelper.clamp(targetEntity.bodyYaw, Configs.BODY_YAW_MIN.getDoubleValue(), Configs.BODY_YAW_MAX.getDoubleValue());
            targetEntity.prevHeadYaw = targetEntity.headYaw = 180 - (float) MathHelper.clamp(targetEntity.headYaw, Configs.HEAD_YAW_MIN.getDoubleValue(), Configs.HEAD_YAW_MAX.getDoubleValue());
            targetEntity.prevPitch = targetEntity.pitch = (float) (MathHelper.clamp(targetEntity.pitch, Configs.PITCH_MIN.getDoubleValue(), Configs.PITCH_MAX.getDoubleValue()) + Configs.PITCH_OFFSET.getDoubleValue());

            if (Configs.SWING_HANDS.getBooleanValue()) {
                targetEntity.handSwingProgress = targetEntity.getHandSwingProgress(client.getTickDelta());
            } else {
                targetEntity.handSwingProgress = 0;
            }

            if (!Configs.HURT_FLASH.getBooleanValue()) {
                targetEntity.hurtTime = 0;
            }

            targetEntity.setFireTicks(0);

            quaternion2.conjugate();
            entityRenderDispatcher.setRenderHitboxes(false);
            entityRenderDispatcher.setRotation(quaternion2);
            entityRenderDispatcher.setRenderShadows(false);
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            entityRenderDispatcher.render(targetEntity, 0.0D, 0.0D, 0.0D, 0.0F, tickDelta, matrixStack, immediate, getLight(targetEntity, tickDelta));
            immediate.draw();
            entityRenderDispatcher.setRenderShadows(true);
            entityRenderDispatcher.setRenderHitboxes(renderHitbox);

            // data restoring
            targetEntity.bodyYaw = bodyYaw;
            targetEntity.yaw = yaw;
            targetEntity.pitch = pitch;
            targetEntity.prevBodyYaw = prevBodyYaw;
            targetEntity.prevHeadYaw = prevHeadYaw;
            targetEntity.prevPitch = prevPitch;
            targetEntity.headYaw = headYaw;
            targetEntity.handSwingProgress = handSwingProgress;
            targetEntity.hurtTime = hurtTime;
            targetEntity.setFireTicks(fireTicks);
            ((EntityInvoker)targetEntity).callSetFlag(0, flag0);
        }
        RenderSystem.popMatrix();
    }

    private static int getLight(LivingEntity entity, float tickDelta)
    {
        int mixedLight = 15;
        if (Configs.USE_WORLD_LIGHT.getBooleanValue())
        {
            mixedLight = entity.world.getLightLevel(new BlockPos(entity.getCameraPosVec(tickDelta)));
        }
        return LightmapTextureManager.pack(mixedLight, mixedLight);
    }
}
