package github.io.lucunji.explayerenderer.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.interfaces.IRenderer;
import github.io.lucunji.explayerenderer.client.render.DataBackup.DataBackupEntry;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.config.PoseOffsetMethod;
import github.io.lucunji.explayerenderer.mixin.ClientPlayerEntityAccessor;
import github.io.lucunji.explayerenderer.mixin.EntityMixin;
import github.io.lucunji.explayerenderer.mixin.LivingEntityMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.*;

import java.lang.Math;
import java.util.List;


public class PlayerHUDRenderer implements IRenderer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final List<DataBackupEntry<LivingEntity, ?>> LIVINGENTITY_BACKUP_ENTRIES = ImmutableList.of(
            new DataBackupEntry<LivingEntity, EntityPose>(LivingEntity::getPose, LivingEntity::setPose),
            new DataBackupEntry<LivingEntity, Boolean>(Entity::isInSneakingPose, (e, flag) -> {
                if (e instanceof ClientPlayerEntity) ((ClientPlayerEntityAccessor) e).setInSneakingPose(flag);
            }),
            new DataBackupEntry<LivingEntity, Float>(e -> ((LivingEntityMixin) e).getLeaningPitch(), (e, pitch) -> ((LivingEntityMixin) e).setLeaningPitch(pitch)),
            new DataBackupEntry<LivingEntity, Float>(e -> ((LivingEntityMixin) e).getLastLeaningPitch(), (e, pitch) -> ((LivingEntityMixin) e).setLastLeaningPitch(pitch)),
            new DataBackupEntry<LivingEntity, Boolean>(LivingEntity::isFallFlying, (e, flag) -> ((EntityMixin) e).callSetFlag(7, flag)),
            new DataBackupEntry<LivingEntity, Integer>(LivingEntity::getFallFlyingTicks, (e, ticks) -> ((LivingEntityMixin) e).setFallFlyingTicks(ticks)),
            new DataBackupEntry<LivingEntity, Entity>(LivingEntity::getVehicle, (e, vehicle) -> ((EntityMixin) e).setVehicle(vehicle)),

            new DataBackupEntry<LivingEntity, Float>(e -> e.prevBodyYaw, (e, yaw) -> e.prevBodyYaw = yaw),
            new DataBackupEntry<LivingEntity, Float>(e -> e.bodyYaw, (e, yaw) -> e.bodyYaw = yaw),
            new DataBackupEntry<LivingEntity, Float>(e -> e.prevHeadYaw, (e, yaw) -> e.prevHeadYaw = yaw),
            new DataBackupEntry<LivingEntity, Float>(e -> e.headYaw, (e, yaw) -> e.headYaw = yaw),
            new DataBackupEntry<LivingEntity, Float>(e -> e.prevPitch, (e, pitch) -> e.prevPitch = pitch),
            new DataBackupEntry<LivingEntity, Float>(LivingEntity::getPitch, LivingEntity::setPitch),

            new DataBackupEntry<LivingEntity, Float>(e -> e.handSwingProgress, (e, prog) -> e.handSwingProgress = prog),
            new DataBackupEntry<LivingEntity, Float>(e -> e.lastHandSwingProgress, (e, prog) -> e.lastHandSwingProgress = prog),
            new DataBackupEntry<LivingEntity, Integer>(e -> e.hurtTime, (e, time) -> e.hurtTime = time),
            new DataBackupEntry<LivingEntity, Integer>(LivingEntity::getFireTicks, LivingEntity::setFireTicks),
            new DataBackupEntry<LivingEntity, Boolean>(e -> ((EntityMixin) e).callGetFlag(0), (e, flag) -> ((EntityMixin) e).callSetFlag(0, flag)) // on fire

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
    public void onRenderGameOverlayPost(DrawContext context) {
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

        var backup = new DataBackup<>(targetEntity, LIVINGENTITY_BACKUP_ENTRIES);
        backup.save();

        transformEntity(targetEntity, partialTicks, poseOffsetMethod == PoseOffsetMethod.FORCE_STANDING);

        DataBackup<LivingEntity> vehicleBackup = null;
        if (Configs.RENDER_VEHICLE.getBooleanValue() && poseOffsetMethod != PoseOffsetMethod.FORCE_STANDING && targetEntity.hasVehicle()) {
            var vehicle = targetEntity.getVehicle();
            assert vehicle != null;

            if (vehicle instanceof LivingEntity livingVehicle) {
                vehicleBackup = new DataBackup<>(livingVehicle, LIVINGENTITY_BACKUP_ENTRIES);
                vehicleBackup.save();
                transformEntity(livingVehicle, partialTicks, false);
            }

            try {
                var method = Entity.class.getDeclaredMethod("getPassengerAttachmentPos", Entity.class, EntityDimensions.class, float.class);
                method.setAccessible(true);
                performRendering(vehicle,
                        Configs.OFFSET_X.getDoubleValue() * scaledWidth,
                        Configs.OFFSET_Y.getDoubleValue() * scaledHeight,
                        Configs.SIZE.getDoubleValue() * scaledHeight,
                        Configs.MIRRORED.getBooleanValue(),
                        getVehicleOffset(targetEntity, vehicle),
                        PoseOffsetMethod.AUTO,
                        Configs.LIGHT_DEGREE.getDoubleValue(),
                        partialTicks);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }


        performRendering(targetEntity,
                Configs.OFFSET_X.getDoubleValue() * scaledWidth,
                Configs.OFFSET_Y.getDoubleValue() * scaledHeight,
                Configs.SIZE.getDoubleValue() * scaledHeight,
                Configs.MIRRORED.getBooleanValue(),
                new Vector3f(0, (float) getPoseOffsetY(targetEntity, partialTicks, poseOffsetMethod), 0),
                poseOffsetMethod,
                Configs.LIGHT_DEGREE.getDoubleValue(),
                partialTicks);

        if (vehicleBackup != null) vehicleBackup.restore();

        backup.restore();
    }

    public boolean needFixMirrorItem() {
        return needFixMirroredItem;
    }

    private double getPoseOffsetY(LivingEntity targetEntity, float partialTicks, IConfigOptionListEntry poseOffsetMethod) {
        if (poseOffsetMethod == PoseOffsetMethod.AUTO) {
            final float defaultPlayerEyeHeight = PlayerEntity.DEFAULT_EYE_HEIGHT;
            final float defaultPlayerSwimmingBBHeight = PlayerEntity.field_30650;
            final float eyeHeightRatio = 0.85f;
            if (targetEntity.isFallFlying()) {
                return (defaultPlayerEyeHeight - targetEntity.getStandingEyeHeight()) * getFallFlyingLeaning(targetEntity, partialTicks);
            } else if (targetEntity.isInSwimmingPose()) {
                return targetEntity.getLeaningPitch(partialTicks) <= 0 ? 0 : defaultPlayerEyeHeight - targetEntity.getStandingEyeHeight();
            } else if (!targetEntity.isInSwimmingPose() && targetEntity.getLeaningPitch(partialTicks) > 0) { // for swimming/crawling pose, only smooth the falling edge
                return (defaultPlayerEyeHeight - defaultPlayerSwimmingBBHeight * eyeHeightRatio * 0.85) * targetEntity.getLeaningPitch(partialTicks);
            } else {
                return PlayerEntity.DEFAULT_EYE_HEIGHT - targetEntity.getStandingEyeHeight();
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
            ((EntityMixin) targetEntity).setVehicle(null);

            ((LivingEntityMixin) targetEntity).setLeaningPitch(0);
            ((LivingEntityMixin) targetEntity).setLastLeaningPitch(0);

            ((EntityMixin) targetEntity).callSetFlag(7, false);
            ((LivingEntityMixin) targetEntity).setFallFlyingTicks(0);
        }

        float headLerp = MathHelper.lerp(partialTicks, targetEntity.prevHeadYaw, targetEntity.headYaw);
        float headClamp = (float) MathHelper.clamp(headLerp,
                Configs.HEAD_YAW_MIN.getDoubleValue(), Configs.HEAD_YAW_MAX.getDoubleValue());
        float bodyLerp = MathHelper.lerp(partialTicks, targetEntity.prevBodyYaw, targetEntity.bodyYaw);
        float diff = headLerp - bodyLerp;

        targetEntity.prevHeadYaw = targetEntity.headYaw = 180 - headClamp;
        targetEntity.prevBodyYaw = targetEntity.bodyYaw = 180 - (float) MathHelper.clamp(
                wrapDegree180(headClamp - diff),
                Configs.BODY_YAW_MIN.getDoubleValue(), Configs.BODY_YAW_MAX.getDoubleValue());
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
        ((EntityMixin) targetEntity).callSetFlag(0, false);
    }

    @SuppressWarnings("deprecation")
    private void performRendering(Entity targetEntity, double posX, double posY, double size, boolean mirror,
                                  Vector3f offset, IConfigOptionListEntry poseOffsetMethod, double lightDegree,
                                  float partialTicks) {
        if (poseOffsetMethod == PoseOffsetMethod.MANUAL) {
            posY += offset.y;
        }

        EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();


        Matrix4fStack matrixStack1 = RenderSystem.getModelViewStack();
        matrixStack1.pushMatrix();
        matrixStack1.translate(0, 0, 550.0f);
        matrixStack1.scale(mirror ? -1 : 1, 1, -1);
        matrixStack1.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) lightDegree));

        RenderSystem.applyModelViewMatrix();

        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-(float) lightDegree));
        matrixStack2.translate((mirror ? -1 : 1) * posX, posY, 1000.0D);
        matrixStack2.scale((float) size, (float) size, (float) size);
        Quaternionf quaternion = RotationAxis.POSITIVE_Z.rotationDegrees(180.0F);
        Quaternionf quaternion2 = RotationAxis.POSITIVE_X.rotationDegrees((float) Configs.ROTATION_X.getDoubleValue());
        quaternion2.mul(RotationAxis.POSITIVE_Y.rotationDegrees((float) Configs.ROTATION_Y.getDoubleValue()));

        // FIXME: temporary fix for non-living vehicles
        // only affects vehicles because non-living entity is not even rendered as target in spectator mode
        if (targetEntity instanceof BoatEntity)
            quaternion2.mul(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        else if (targetEntity instanceof MinecartEntity)
            quaternion2.mul(RotationAxis.POSITIVE_Y.rotationDegrees(90));

        quaternion2.mul(RotationAxis.POSITIVE_Z.rotationDegrees((float) Configs.ROTATION_Z.getDoubleValue()));
        quaternion.mul(quaternion2);
        matrixStack2.multiply(quaternion);

        DiffuseLighting.method_34742();
        quaternion2.conjugate();

        if (poseOffsetMethod == PoseOffsetMethod.AUTO) {
            matrixStack2.translate(offset.x, offset.y, offset.z);
        }

        entityRenderDispatcher.setRotation(quaternion2);
        boolean renderHitbox = entityRenderDispatcher.shouldRenderHitboxes();
        entityRenderDispatcher.setRenderHitboxes(false);
        entityRenderDispatcher.setRenderShadows(false);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() ->
                entityRenderDispatcher.render(targetEntity, 0, 0, 0, 0, partialTicks, matrixStack2, immediate, getLight(targetEntity, partialTicks))
        );
        // disable cull to fix item rendering glitches when mirror option is on
        needFixMirroredItem = mirror;
        immediate.draw();
        needFixMirroredItem = false;

        // do not need to restore this value in fact
        entityRenderDispatcher.setRenderShadows(true);
        entityRenderDispatcher.setRenderHitboxes(renderHitbox);

        matrixStack1.popMatrix();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    private static int getLight(Entity entity, float tickDelta) {
        if (Configs.USE_WORLD_LIGHT.getBooleanValue()) {
            World world = entity.getWorld();
            int blockLight = world.getLightLevel(LightType.BLOCK, BlockPos.ofFloored(entity.getCameraPosVec(tickDelta)));
            int skyLight = world.getLightLevel(LightType.SKY, BlockPos.ofFloored(entity.getCameraPosVec(tickDelta)));
            int min = Configs.WORLD_LIGHT_MIN.getIntegerValue();
            blockLight = MathHelper.clamp(blockLight, min, 15);
            skyLight = MathHelper.clamp(skyLight, min, 15);
            return LightmapTextureManager.pack(blockLight, skyLight);
        }
        return LightmapTextureManager.pack(15, 15);
    }

    private static float getFallFlyingLeaning(LivingEntity entity, float partialTicks) {
        float ticks = partialTicks + entity.getFallFlyingTicks();
        return MathHelper.clamp(ticks * ticks / 100f, 0f, 1f);
    }

    /**
     * Wrap angle between [-180, 180] in degrees.
     */
    private static float wrapDegree180(float x) {
        x += 180;
        if (x < 0) x += (float) (360 * Math.ceil(-x / 360));
        return (x % 360) - 180;
    }

    /**
     * Compute offset of vehicle relative to rider.
     */
    private static Vector3f getVehicleOffset(Entity rider, Entity vehicle) {
        return vehicle.getPassengerRidingPos(rider).subtract(vehicle.getPos()).toVector3f();
//        Vector3f ret;
//        if (vehicle instanceof LivingEntity) {
//            // FIXME: glitches when the vehicle is a horse
//            ret = ((EntityMixin) vehicle).callGetPassengerAttachmentPos(rider, vehicle.getDimensions(vehicle.getPose()), ((LivingEntityMixin) vehicle).callGetScaleFactor());
//        } else {
//            ret = ((EntityMixin) vehicle).callGetPassengerAttachmentPos(rider, ((EntityMixin) vehicle).getDimensions(), 1f);
//        }
//        return ret.add(0, rider.getRidingOffset(vehicle), 0).mul(1, -1, 1);
    }
}
