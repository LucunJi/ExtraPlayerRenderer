package github.io.lucunji.explayerenderer.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.interfaces.IRenderer;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.mixin.EntityInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class PlayerHUDRenderer implements IRenderer {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public PlayerHUDRenderer() {
    }

    /**
     * This method is invoked by malilib before {@code render()} method in {@link net.minecraft.client.gui.hud.InGameHud}
     * returns.
     * <p>
     * TODO: fix lightDegree
     */
    @Override
    public void onRenderGameOverlayPost(MatrixStack matrixStack) {
        if (client.skipGameRender || client.currentScreen != null) return;
        doRender(client.getTickDelta(), matrixStack);
    }

    public void doRender(float partialTicks, MatrixStack matrixStack) {
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

        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();
        double posX = Configs.OFFSET_X.getDoubleValue() * scaledWidth;
        double posY = Configs.OFFSET_Y.getDoubleValue() * scaledHeight;
        if (targetEntity.isFallFlying())
            posY += Configs.ELYTRA_OFFSET_Y.getDoubleValue();
        else if (targetEntity.isInSneakingPose())
            posY += Configs.SNEAKING_OFFSET_Y.getDoubleValue();
        else if (targetEntity.isSwimming())
            posY += Configs.SWIM_OFFSET_Y.getDoubleValue();
        double size = Configs.SIZE.getDoubleValue() * scaledHeight;
        boolean mirror = Configs.MIRROR.getBooleanValue();
//        double lightDegree = Configs.LIGHT_DEGREE.getDoubleValue();

        /* *************** store entity data *************** */
        float bodyYaw = targetEntity.bodyYaw;
        float pitch = targetEntity.getPitch();
        float prevBodyYaw = targetEntity.prevBodyYaw;
        float prevHeadYaw = targetEntity.prevHeadYaw;
        float prevPitch = targetEntity.prevPitch;
        float headYaw = targetEntity.getHeadYaw();
        float handSwingProgress = targetEntity.handSwingProgress;
        float lastHandSwingProgress = targetEntity.lastHandSwingProgress;
        int hurtTime = targetEntity.hurtTime;
        int fireTicks = targetEntity.getFireTicks();
        boolean flag0 = ((EntityInvoker) targetEntity).callGetFlag(0);
        ((EntityInvoker) targetEntity).callSetFlag(0, false);

        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        boolean renderHitbox = entityRenderDispatcher.shouldRenderHitboxes();

        /* *************** modify entity data *************** */
        targetEntity.prevBodyYaw = targetEntity.bodyYaw = 180 - (float) MathHelper.clamp(bodyYaw, Configs.BODY_YAW_MIN.getDoubleValue(), Configs.BODY_YAW_MAX.getDoubleValue());
        targetEntity.prevHeadYaw = targetEntity.headYaw = 180 - (float) MathHelper.clamp(headYaw, Configs.HEAD_YAW_MIN.getDoubleValue(), Configs.HEAD_YAW_MAX.getDoubleValue());
        targetEntity.prevPitch = (float) (MathHelper.clamp(pitch, Configs.PITCH_MIN.getDoubleValue(), Configs.PITCH_MAX.getDoubleValue()) + Configs.PITCH_OFFSET.getDoubleValue());
        targetEntity.setPitch(targetEntity.prevPitch);

        if (!Configs.SWING_HANDS.getBooleanValue()) {
            targetEntity.handSwingProgress = 0;
            targetEntity.lastHandSwingProgress = 0;
        }

        if (!Configs.HURT_FLASH.getBooleanValue()) {
            targetEntity.hurtTime = 0;
        }

        targetEntity.setFireTicks(0);

        /* *************** do rendering *************** */
        MatrixStack matrixStack1 = RenderSystem.getModelViewStack();
        matrixStack1.push();
        matrixStack1.translate(0, 0, 550.0D);
        matrixStack1.scale(mirror ? -1 : 1, 1, -1);

        RenderSystem.applyModelViewMatrix();

        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate((mirror ? -1 : 1) * posX, posY, 1000.0D);
        matrixStack2.scale((float) size, (float) size, (float) size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion((float) Configs.ROTATION_X.getDoubleValue());
        quaternion2.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) Configs.ROTATION_Y.getDoubleValue()));
        quaternion2.hamiltonProduct(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) Configs.ROTATION_Z.getDoubleValue()));
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);

        DiffuseLighting.method_34742();
        quaternion2.conjugate();

        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderHitboxes(false);
        entityRenderDispatcher.setRenderShadows(false);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        LivingEntity finalEntity = targetEntity;
        //noinspection deprecation
        RenderSystem.runAsFancy(() ->
                entityRenderDispatcher.render(finalEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStack2, immediate, getLight(finalEntity, partialTicks))
        );

        // disable cull to fix item rendering glitches when mirror option is on
        RenderSystem.disableCull();
        immediate.draw();
        RenderSystem.enableCull();

        entityRenderDispatcher.setRenderShadows(true);
        entityRenderDispatcher.setRenderHitboxes(renderHitbox);

        matrixStack1.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();

        /* *************** restore data *************** */
        targetEntity.setBodyYaw(bodyYaw);
        targetEntity.setPitch(pitch);
        targetEntity.prevBodyYaw = prevBodyYaw;
        targetEntity.prevHeadYaw = prevHeadYaw;
        targetEntity.prevPitch = prevPitch;
        targetEntity.setHeadYaw(headYaw);
        targetEntity.handSwingProgress = handSwingProgress;
        targetEntity.lastHandSwingProgress = lastHandSwingProgress;
        targetEntity.hurtTime = hurtTime;
        targetEntity.setFireTicks(fireTicks);
        ((EntityInvoker) targetEntity).callSetFlag(0, flag0);
    }

    private static int getLight(LivingEntity entity, float tickDelta) {
        if (Configs.USE_WORLD_LIGHT.getBooleanValue()) {
            World world = entity.world;
            int blockLight = world.getLightLevel(LightType.BLOCK, new BlockPos(entity.getCameraPosVec(tickDelta)));
            int skyLight = world.getLightLevel(LightType.SKY, new BlockPos(entity.getCameraPosVec(tickDelta)));
            int min = Configs.WORLD_LIGHT_MIN.getIntegerValue();
            blockLight = MathHelper.clamp(blockLight, min, 15);
            skyLight = MathHelper.clamp(skyLight, min, 15);
            return LightmapTextureManager.pack(blockLight, skyLight);
        }
        return LightmapTextureManager.pack(15, 15);
    }
}
