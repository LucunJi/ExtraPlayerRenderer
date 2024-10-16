/*
 *     Highly configurable paper doll mod.
 *     Copyright (C) 2024  LucunJi, And all  Contributors
 *
 *     This file is part of Extra Player Renderer.
 *
 *     Extra Player Renderer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Extra Player Renderer is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Extra Player Renderer.  If not, see <https://www.gnu.org/licenses/>.
 */

package github.io.lucunji.extraplayerrenderer.hud;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.io.lucunji.extraplayerrenderer.ExtraPlayerRenderer;
import github.io.lucunji.extraplayerrenderer.config.Configs;
import github.io.lucunji.extraplayerrenderer.mixininterface.ImmediateMixinInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class ExtraPlayerHud {
    private static final List<DataBackup.DataBackupEntry<LivingEntity, ?>> LIVINGENTITY_BACKUP_ENTRIES = ImmutableList.of(
            new DataBackup.DataBackupEntry<>(LivingEntity::getPose, LivingEntity::setPose),
            new DataBackup.DataBackupEntry<>(Entity::isCrouching, (e, flag) -> {
                if (e instanceof LocalPlayer) {
                    ExtraPlayerHud.setIsCrouching((LocalPlayer) e, flag);
                }
            }),
            new DataBackup.DataBackupEntry<>(ExtraPlayerHud::getSwimAmount, ExtraPlayerHud::setSwimAmount),
            new DataBackup.DataBackupEntry<>(ExtraPlayerHud::getSwimAmountO, ExtraPlayerHud::setSwimAmountO),
            new DataBackup.DataBackupEntry<>(LivingEntity::isFallFlying, (e, flag) -> ExtraPlayerHud.setFallFlying(e, flag)),
            new DataBackup.DataBackupEntry<>(ExtraPlayerHud::getFallFlyTicks, ExtraPlayerHud::setFallFlyingTicks),
            new DataBackup.DataBackupEntry<>(LivingEntity::getVehicle, ExtraPlayerHud::setVehicle),
            new DataBackup.DataBackupEntry<>(e -> e.yBodyRotO, (e, yaw) -> e.yBodyRotO = yaw),
            new DataBackup.DataBackupEntry<>(e -> e.yBodyRot, (e, yaw) -> e.yBodyRot = yaw),
            new DataBackup.DataBackupEntry<>(e -> e.yHeadRotO, (e, yaw) -> e.yHeadRotO = yaw),
            new DataBackup.DataBackupEntry<>(e -> e.yHeadRot, (e, yaw) -> e.yHeadRot = yaw),
            new DataBackup.DataBackupEntry<>(e -> e.xRotO, (e, pitch) -> e.xRotO = pitch),
            new DataBackup.DataBackupEntry<>(LivingEntity::getXRot, LivingEntity::setXRot),
            new DataBackup.DataBackupEntry<>(e -> e.attackAnim, (e, prog) -> e.attackAnim = prog),
            new DataBackup.DataBackupEntry<>(e -> e.oAttackAnim, (e, prog) -> e.oAttackAnim = prog),
            new DataBackup.DataBackupEntry<>(e -> e.hurtTime, (e, time) -> e.hurtTime = time),
            new DataBackup.DataBackupEntry<>(LivingEntity::getRemainingFireTicks, LivingEntity::setRemainingFireTicks),
            new DataBackup.DataBackupEntry<>(e -> e.isOnFire(), (e, flag) -> ExtraPlayerHud.setOnFire(e, flag)) // on fire
    );
    private final Minecraft client;

    public ExtraPlayerHud(Minecraft client) {
        this.client = client;
    }

    private static void setSwimAmount(LivingEntity livingEntity, float swimAmount) {
        livingEntity.swimAmount = swimAmount; // 假设 swimAmount 是 float 类型
    }

    private static float getSwimAmount(LivingEntity livingEntity) {
        return livingEntity.swimAmount; // 假设返回的是 float 类型
    }

    private static float getSwimAmountO(LivingEntity livingEntity) {
        return livingEntity.swimAmountO; // 假设返回的是 float 类型
    }

    private static void setSwimAmountO(LivingEntity livingEntity, float swimAmountO) {
        livingEntity.swimAmountO = swimAmountO; // 假设 swimAmountO 是 float 类型
    }

    private static void setVehicle(LivingEntity livingEntity, Entity entity) {
        livingEntity.vehicle = entity;
    }

    private static int getFallFlyTicks(LivingEntity livingEntity) {
        return livingEntity.fallFlyTicks; // 假设返回的是 int 类型
    }

    private static void setIsCrouching(LocalPlayer player, boolean flag) {
        player.crouching = flag; // 假设 crouching 是 boolean 类型
    }

    private static int getLight(Entity entity, float tickDelta) {
        if (ExtraPlayerRenderer.CONFIGS.useWorldLight.getValue()) {
            Level world = entity.level();
            int blockLight = world.getBrightness(LightLayer.BLOCK, BlockPos.containing(entity.getEyePosition(tickDelta)));
            int skyLight = world.getBrightness(LightLayer.SKY, BlockPos.containing(entity.getEyePosition(tickDelta)));
            int min = ExtraPlayerRenderer.CONFIGS.worldLightMin.getValue();
            blockLight = Mth.clamp(blockLight, min, 15);
            skyLight = Mth.clamp(skyLight, min, 15);
            return LightTexture.pack(blockLight, skyLight);
        }
        return LightTexture.pack(15, 15);
    }

    private static float getFallFlyingLeaning(LivingEntity entity, float partialTicks) {
        float ticks = partialTicks + entity.getFallFlyingTicks();
        return Mth.clamp(ticks * ticks / 100f, 0f, 1f);
    }

    private static void setFallFlying(LivingEntity targetEntity, boolean b) {
    }

    private static void setFallFlyingTicks(LivingEntity targetEntity, int i) {
    }

    private static void setOnFire(LivingEntity targetEntity, boolean b) {
    }

    /**
     * Mimics the code in {@link InventoryScreen#renderEntityInInventory}
     */
    public void render(float partialTicks) {
        if (client.level == null || client.player == null || !ExtraPlayerRenderer.CONFIGS.enabled.getValue()) return;
        LivingEntity targetEntity = client.level.players().stream().filter(p -> p.getName().getString().equals(ExtraPlayerRenderer.CONFIGS.playerName.getValue())).findFirst().orElse(client.player);
        if (ExtraPlayerRenderer.CONFIGS.spectatorAutoSwitch.getValue() && client.player.isSpectator()) {
            Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
            if (cameraEntity instanceof LivingEntity) {
                targetEntity = (LivingEntity) cameraEntity;
            } else if (cameraEntity != null) {
                return;
            }
        }

        int scaledWidth = client.getWindow().getGuiScaledWidth();
        int scaledHeight = client.getWindow().getGuiScaledHeight();
        Configs.PoseOffsetMethod poseOffsetMethod = ExtraPlayerRenderer.CONFIGS.poseOffsetMethod.getValue();

        var backup = new DataBackup<>(targetEntity, LIVINGENTITY_BACKUP_ENTRIES);
        backup.save();

        transformEntity(targetEntity, partialTicks, poseOffsetMethod == Configs.PoseOffsetMethod.FORCE_STANDING);

        DataBackup<LivingEntity> vehicleBackup = null;
        if (ExtraPlayerRenderer.CONFIGS.renderVehicle.getValue() && poseOffsetMethod != Configs.PoseOffsetMethod.FORCE_STANDING && targetEntity.isPassenger()) {
            var vehicle = targetEntity.getVehicle();
            assert vehicle != null;

            // get the overall yaw before transforming
            var yawLerped = vehicle.getViewYRot(partialTicks);

            // the rendered yaw of minecart is determined non-trivially in its MinecartEntityRenderer#render, so it cannot be fixed to 0 easily
            if (vehicle instanceof LivingEntity livingVehicle) {
                vehicleBackup = new DataBackup<>(livingVehicle, LIVINGENTITY_BACKUP_ENTRIES);
                vehicleBackup.save();
                transformEntity(livingVehicle, partialTicks, false);
            }

            performRendering(vehicle,
                    ExtraPlayerRenderer.CONFIGS.offsetX.getValue() * scaledWidth,
                    ExtraPlayerRenderer.CONFIGS.offsetY.getValue() * scaledHeight,
                    ExtraPlayerRenderer.CONFIGS.size.getValue() * scaledHeight,
                    ExtraPlayerRenderer.CONFIGS.mirrored.getValue(),
                    vehicle.getPosition(partialTicks).subtract(targetEntity.getPosition(partialTicks))
                            .yRot((float) Math.toRadians(yawLerped)).toVector3f(), // undo the rotation
                    ExtraPlayerRenderer.CONFIGS.lightDegree.getValue(),
                    partialTicks);
        }


        performRendering(targetEntity,
                ExtraPlayerRenderer.CONFIGS.offsetX.getValue() * scaledWidth,
                ExtraPlayerRenderer.CONFIGS.offsetY.getValue() * scaledHeight,
                ExtraPlayerRenderer.CONFIGS.size.getValue() * scaledHeight,
                ExtraPlayerRenderer.CONFIGS.mirrored.getValue(),
                new Vector3f(0, (float) getPoseOffsetY(targetEntity, partialTicks, poseOffsetMethod), 0),
                ExtraPlayerRenderer.CONFIGS.lightDegree.getValue(),
                partialTicks);

        if (vehicleBackup != null) vehicleBackup.restore();

        backup.restore();
    }

    private double getPoseOffsetY(LivingEntity targetEntity, float partialTicks, Configs.PoseOffsetMethod poseOffsetMethod) {
        if (poseOffsetMethod == Configs.PoseOffsetMethod.AUTO) {
            final float defaultPlayerEyeHeight = Player.DEFAULT_EYE_HEIGHT;
            final float defaultPlayerSwimmingBBHeight = Player.SWIMMING_BB_HEIGHT;
            final float eyeHeightRatio = 0.85f;
            if (targetEntity.isFallFlying()) {
                return (defaultPlayerEyeHeight - targetEntity.getEyeHeight()) * getFallFlyingLeaning(targetEntity, partialTicks);
            } else if (targetEntity.isAutoSpinAttack()) {
                return defaultPlayerEyeHeight - defaultPlayerSwimmingBBHeight * eyeHeightRatio * 0.8;
            } else if (targetEntity.isVisuallySwimming()) {
                return targetEntity.getSwimAmount(partialTicks) <= 0 ? 0 : defaultPlayerEyeHeight - targetEntity.getEyeHeight();
            } else if (!targetEntity.isVisuallySwimming() && targetEntity.getSwimAmount(partialTicks) > 0) { // for swimming/crawling pose, only smooth the falling edge
                return (defaultPlayerEyeHeight - defaultPlayerSwimmingBBHeight * eyeHeightRatio * 0.85) * targetEntity.getSwimAmount(partialTicks);
            } else {
                return Player.DEFAULT_EYE_HEIGHT - targetEntity.getEyeHeight();
            }
        } else if (poseOffsetMethod == Configs.PoseOffsetMethod.MANUAL) {
            if (targetEntity.isFallFlying()) {
                return ExtraPlayerRenderer.CONFIGS.elytraOffsetY.getValue() * getFallFlyingLeaning(targetEntity, partialTicks);
            } else if ((targetEntity.isVisuallySwimming()) && targetEntity.getSwimAmount(partialTicks) > 0 || targetEntity.isAutoSpinAttack()) { // require nonzero leaning to filter out glitch
                return ExtraPlayerRenderer.CONFIGS.swimCrawlOffsetY.getValue();
            } else if (!targetEntity.isVisuallySwimming() && targetEntity.getSwimAmount(partialTicks) > 0) { // for swimming/crawling pose, only smooth the falling edge
                return ExtraPlayerRenderer.CONFIGS.swimCrawlOffsetY.getValue() * targetEntity.getSwimAmount(partialTicks);
            } else if (targetEntity.isCrouching()) {
                return ExtraPlayerRenderer.CONFIGS.sneakOffsetY.getValue();
            }
        }
        return 0;
    }

    private void transformEntity(LivingEntity targetEntity, float partialTicks, boolean forceStanding) {
        // Sync values to avoid visual inconsistencies
        if (!targetEntity.isSwimming() && !targetEntity.isFallFlying() && !targetEntity.isVisuallyCrawling()) {
            targetEntity.setPose(targetEntity.isCrouching() ? Pose.CROUCHING : Pose.STANDING);
        }

        if (forceStanding) {
            // If the entity is a player, force standing by cancelling sneaking pose
            if (targetEntity instanceof LocalPlayer) {
                setIsCrouching((LocalPlayer) targetEntity, false);
            }
            // Cancel riding
            setVehicle(targetEntity, null);

            // Reset leaningPitch and lastLeaningPitch
            setSwimAmount(targetEntity, 0);
            setSwimAmountO(targetEntity, 0);

            // Cancel fall flying state
            setFallFlying(targetEntity, false);
            setFallFlyingTicks(targetEntity, 0);
        }

        // Set body and head rotation angles
        targetEntity.yBodyRot = targetEntity.yBodyRotO;
        targetEntity.yHeadRot = targetEntity.yHeadRotO;

        // Reset attack animation related properties
        targetEntity.attackAnim = targetEntity.oAttackAnim;

        // Reset hurt time
        targetEntity.hurtTime = 0;

        // Clear fire status
        setRemainingFireTicks(targetEntity, 0);
        setOnFire(targetEntity, false);

        // Reset rotation angle
        targetEntity.setXRot(targetEntity.xRotO);
    }

    private void setRemainingFireTicks(LivingEntity targetEntity, int i) {
    }

    @SuppressWarnings("deprecation")
    private void performRendering(Entity targetEntity, double posX, double posY, double size, boolean mirror,
                                  Vector3f offset, double lightDegree, float partialTicks) {
        EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();

        Matrix4fStack matrixStack1 = RenderSystem.getModelViewStack();
        matrixStack1.pushMatrix();
        matrixStack1.scale(mirror ? -1 : 1, 1, -1);
        // IDK what shit Mojang made but let's add 180 deg to restore the old behavior
        matrixStack1.rotateY((float) Math.toRadians(lightDegree + 180));

        RenderSystem.applyModelViewMatrix();

        PoseStack matrixStack2 = new PoseStack();
        matrixStack2.mulPose(Axis.YP.rotationDegrees(-(float) lightDegree - 180));
        matrixStack2.translate((mirror ? -1 : 1) * posX, posY, 0);
        matrixStack2.scale((float) size, (float) size, (float) size);
        Quaternionf quaternion = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternion2 = new Quaternionf()
                .rotateXYZ((float) Math.toRadians(ExtraPlayerRenderer.CONFIGS.rotationX.getValue()),
                        (float) Math.toRadians(ExtraPlayerRenderer.CONFIGS.rotationY.getValue()),
                        (float) Math.toRadians(ExtraPlayerRenderer.CONFIGS.rotationZ.getValue()));

        quaternion.mul(quaternion2);
        matrixStack2.mulPose(quaternion);

        if (targetEntity instanceof Boat) {
            matrixStack2.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180)));
        }

        Lighting.setupForEntityInInventory();
        quaternion2.conjugate();

        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        boolean renderHitbox = entityRenderDispatcher.shouldRenderHitBoxes();
        entityRenderDispatcher.setRenderHitBoxes(false);
        entityRenderDispatcher.setRenderShadow(false);

        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() ->
                entityRenderDispatcher.render(targetEntity, offset.x, offset.y, offset.z, 0, partialTicks, matrixStack2, immediate, getLight(targetEntity, partialTicks))
        );
        // disable cull to fix item rendering glitches when mirror option is on
        ImmediateMixinInterface immediateMixined = (ImmediateMixinInterface) immediate;
        immediateMixined.extraPlayerRenderer$setForceDisableCulling(mirror);
        immediate.endBatch();
        immediateMixined.extraPlayerRenderer$setForceDisableCulling(false);

        // do not need to restore this value in fact
        entityRenderDispatcher.setRenderShadow(true);
        entityRenderDispatcher.setRenderHitBoxes(renderHitbox);

        matrixStack1.popMatrix();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

}
