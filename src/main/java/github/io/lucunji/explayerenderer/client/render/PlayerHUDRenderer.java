package github.io.lucunji.explayerenderer.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.interfaces.IRenderer;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.config.PoseOffsetMethod;
import github.io.lucunji.explayerenderer.mixin.ClientPlayerEntityAccessor;
import github.io.lucunji.explayerenderer.mixin.EntityInvoker;
import github.io.lucunji.explayerenderer.mixin.LivingEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.List;

import static github.io.lucunji.explayerenderer.client.render.DataBackup.DataBackupEntry;

public class PlayerHUDRenderer implements IRenderer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final List<DataBackupEntry<LivingEntity, ?>> ENTITY_DATA_BACKUP_ENTRIES = ImmutableList.of(
            new DataBackupEntry<>(LivingEntity::getPose, LivingEntity::setPose),
            new DataBackupEntry<>(Entity::isInSneakingPose, (e, flag) -> {
                if (e instanceof ClientPlayerEntity) ((ClientPlayerEntityAccessor) e).setInSneakingPose(flag);
            }),
            new DataBackupEntry<>(e -> ((LivingEntityAccessor) e).getLeaningPitch(), (e, pitch) -> ((LivingEntityAccessor) e).setLeaningPitch(pitch)),
            new DataBackupEntry<>(e -> ((LivingEntityAccessor) e).getLastLeaningPitch(), (e, pitch) -> ((LivingEntityAccessor) e).setLastLeaningPitch(pitch)),
            new DataBackupEntry<>(LivingEntity::isFallFlying, (e, flag) -> ((EntityInvoker) e).callSetFlag(7, flag)),
            new DataBackupEntry<>(LivingEntity::getRoll, (e, roll) -> ((LivingEntityAccessor) e).setRoll(roll)),

            new DataBackupEntry<>(e -> e.prevBodyYaw, (e, yaw) -> e.prevBodyYaw = yaw),
            new DataBackupEntry<>(e -> e.bodyYaw, (e, yaw) -> e.bodyYaw = yaw),
            new DataBackupEntry<>(e -> e.prevHeadYaw, (e, yaw) -> e.prevHeadYaw = yaw),
            new DataBackupEntry<>(e -> e.headYaw, (e, yaw) -> e.headYaw = yaw),
            new DataBackupEntry<>(e -> e.prevPitch, (e, pitch) -> e.prevPitch = pitch),
            new DataBackupEntry<>(LivingEntity::getPitch, LivingEntity::setPitch),

            new DataBackupEntry<>(e -> e.handSwingProgress, (e, prog) -> e.handSwingProgress = prog),
            new DataBackupEntry<>(e -> e.lastHandSwingProgress, (e, prog) -> e.lastHandSwingProgress = prog),
            new DataBackupEntry<>(e -> e.hurtTime, (e, time) -> e.hurtTime = time),
            new DataBackupEntry<>(LivingEntity::getFireTicks, LivingEntity::setFireTicks),
            new DataBackupEntry<>(e -> ((EntityInvoker) e).callGetFlag(0), (e, flag) -> ((EntityInvoker) e).callSetFlag(0, flag)) // on fire

    );


    private boolean needFixMirroredItem;

    public PlayerHUDRenderer() {
        needFixMirroredItem = false;
    }

    /**
     * This method is invoked by malilib before {@code render()} method in {@link net.minecraft.client.gui.hud.InGameHud}
     * returns.
     */
    @Override
    public void onRenderGameOverlayPost(MatrixStack matrixStack) {
        if (client.skipGameRender || client.currentScreen != null) return;
        doRender(client.getTickDelta());
    }

    /**
     * Mimics the code in {@link InventoryScreen#drawEntity}
     */
    public void doRender(float partialTicks) {
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
        IConfigOptionListEntry poseOffsetMethod = Configs.POSE_OFFSET_METHOD.getOptionListValue();

        DataBackup<LivingEntity> backup = new DataBackup<>(targetEntity, ENTITY_DATA_BACKUP_ENTRIES);
        backup.save();

        transformEntity(targetEntity, partialTicks, poseOffsetMethod == PoseOffsetMethod.FORCE_STANDING);
        performRendering(targetEntity,
                Configs.OFFSET_X.getDoubleValue() * scaledWidth,
                Configs.OFFSET_Y.getDoubleValue() * scaledHeight,
                Configs.SIZE.getDoubleValue() * scaledHeight,
                Configs.MIRRORED.getBooleanValue(),
                getPoseOffsetY(targetEntity, partialTicks, poseOffsetMethod),
                poseOffsetMethod,
                Configs.LIGHT_DEGREE.getDoubleValue(),
                partialTicks);

        backup.restore();
    }

    public boolean needFixMirrorItem() {
        return needFixMirroredItem;
    }

    private double getPoseOffsetY(LivingEntity targetEntity, float partialTicks, IConfigOptionListEntry poseOffsetMethod) {
        if (poseOffsetMethod == PoseOffsetMethod.AUTO) {
            final float defaultPlayerEyeHeight = PlayerEntity.field_30651;
            final float defaultPlayerSwimmingBBHeight = PlayerEntity.field_30650;
            final float eyeHeightRatio = 0.85f;
            if (targetEntity.isFallFlying()) {
                return (defaultPlayerEyeHeight - targetEntity.getStandingEyeHeight()) * getFallFlyingLeaning(targetEntity, partialTicks);
            } else if (targetEntity.isInSwimmingPose()) {
                return targetEntity.getLeaningPitch(partialTicks) <= 0 ? 0 : defaultPlayerEyeHeight - targetEntity.getStandingEyeHeight();
            } else if (!targetEntity.isInSwimmingPose() && targetEntity.getLeaningPitch(partialTicks) > 0) { // for swimming/crawling pose, only smooth the falling edge
                return (defaultPlayerEyeHeight - defaultPlayerSwimmingBBHeight * eyeHeightRatio * 0.85) * targetEntity.getLeaningPitch(partialTicks);
            } else {
                return PlayerEntity.field_30651 /* DEFAULT_EYE_HEIGHT */ - targetEntity.getStandingEyeHeight();
            }
        } else if (poseOffsetMethod == PoseOffsetMethod.MANUAL) {
            if (targetEntity.isFallFlying()) {
                return Configs.ELYTRA_OFFSET_Y.getDoubleValue() * getFallFlyingLeaning(targetEntity, partialTicks);
            } else if ((targetEntity.isInSwimmingPose()) && targetEntity.getLeaningPitch(partialTicks) > 0) { // require nonzero leaning to filter out glitch
                return Configs.SWIM_CRAWL_OFFSET_Y.getDoubleValue();
            } else if (!targetEntity.isInSwimmingPose() && targetEntity.getLeaningPitch(partialTicks) > 0) { // for swimming/crawling pose, only smooth the falling edge
                return Configs.SWIM_CRAWL_OFFSET_Y.getDoubleValue() * targetEntity.getLeaningPitch(partialTicks);
            } else if (targetEntity.isInSneakingPose()) {
                return Configs.SNEAK_OFFSET_Y.getDoubleValue();
            }
        }
        return 0;
    }

    private void transformEntity(LivingEntity targetEntity, float partialTicks, boolean forceStanding) {
        // synchronize values to remove glitch
        if (!targetEntity.isSwimming() && !targetEntity.isFallFlying() && !targetEntity.isCrawling()) {
            targetEntity.setPose(targetEntity.isInSneakingPose() ? EntityPose.CROUCHING : EntityPose.STANDING);
        }

        if (forceStanding) {
            if (targetEntity instanceof ClientPlayerEntity) {
                ((ClientPlayerEntityAccessor) targetEntity).setInSneakingPose(false);
            }

            ((LivingEntityAccessor) targetEntity).setLeaningPitch(0);
            ((LivingEntityAccessor) targetEntity).setLastLeaningPitch(0);

            ((EntityInvoker) targetEntity).callSetFlag(7, false);
            ((LivingEntityAccessor) targetEntity).setRoll(0);
        }

        targetEntity.prevBodyYaw = targetEntity.bodyYaw = 180 - (float) MathHelper.clamp(
                MathHelper.lerp(partialTicks, targetEntity.prevBodyYaw, targetEntity.bodyYaw),
                Configs.BODY_YAW_MIN.getDoubleValue(), Configs.BODY_YAW_MAX.getDoubleValue());
        targetEntity.prevHeadYaw = targetEntity.headYaw = 180 - (float) MathHelper.clamp(
                MathHelper.lerp(partialTicks, targetEntity.prevHeadYaw, targetEntity.headYaw),
                Configs.HEAD_YAW_MIN.getDoubleValue(), Configs.HEAD_YAW_MAX.getDoubleValue());
        targetEntity.setPitch(targetEntity.prevPitch = (float) (MathHelper.clamp(
                        MathHelper.lerp(partialTicks, targetEntity.prevPitch, targetEntity.getPitch()),
                        Configs.PITCH_MIN.getDoubleValue(), Configs.PITCH_MAX.getDoubleValue())
                        + Configs.PITCH_OFFSET.getDoubleValue())
        );

        if (!Configs.SWING_HANDS.getBooleanValue()) {
            targetEntity.handSwingProgress = 0;
            targetEntity.lastHandSwingProgress = 0;
        }

        if (!Configs.HURT_FLASH.getBooleanValue()) {
            targetEntity.hurtTime = 0;
        }

        targetEntity.setFireTicks(0);
        ((EntityInvoker) targetEntity).callSetFlag(0, false);
    }

    private void performRendering(LivingEntity targetEntity, double posX, double posY, double size, boolean mirror,
                                  double poseOffsetY, IConfigOptionListEntry poseOffsetMethod, double lightDegree,
                                  float partialTicks) {
        if (poseOffsetMethod == PoseOffsetMethod.MANUAL) {
            posY += poseOffsetY;
        }

        EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();

        MatrixStack matrixStack1 = RenderSystem.getModelViewStack();
        matrixStack1.push();
        matrixStack1.translate(0, 0, 550.0D);
        matrixStack1.scale(mirror ? -1 : 1, 1, -1);
        matrixStack1.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) lightDegree));

        RenderSystem.applyModelViewMatrix();

        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-(float) lightDegree));
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

        if (poseOffsetMethod == PoseOffsetMethod.AUTO) {
            matrixStack2.translate(0, poseOffsetY, 0);
        }

        entityRenderDispatcher.setRotation(quaternion2);
        boolean renderHitbox = entityRenderDispatcher.shouldRenderHitboxes();
        entityRenderDispatcher.setRenderHitboxes(false);
        entityRenderDispatcher.setRenderShadows(false);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        //noinspection deprecation
        RenderSystem.runAsFancy(() ->
                entityRenderDispatcher.render(targetEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStack2, immediate, getLight(targetEntity, partialTicks))
        );
        // disable cull to fix item rendering glitches when mirror option is on
        needFixMirroredItem = mirror;
        immediate.draw();
        needFixMirroredItem = false;

        // do not need to restore this value in fact
        entityRenderDispatcher.setRenderShadows(true);
        entityRenderDispatcher.setRenderHitboxes(renderHitbox);

        matrixStack1.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
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

    private static float getFallFlyingLeaning(LivingEntity entity, float partialTicks) {
        float ticks = partialTicks + entity.getRoll();
        return MathHelper.clamp(ticks * ticks / 100f, 0f, 1f);
    }
}
