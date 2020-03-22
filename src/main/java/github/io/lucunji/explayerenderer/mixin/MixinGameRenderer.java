package github.io.lucunji.explayerenderer.mixin;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.render.GameRenderer.class)
public class MixinGameRenderer {

    @Unique MinecraftClient client  = MinecraftClient.getInstance();

    @Inject(method = "render(FJZ)V", at = @At(
            value = "RETURN"
    ))
    private void onRenderGameHud(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (client.skipGameRender || !tick || MinecraftClient.getInstance().world == null) return;
        if (client.currentScreen != null) return;

        int scaledWidth = MinecraftClient.getInstance().window.getScaledWidth();
        int scaledHeight = MinecraftClient.getInstance().window.getScaledHeight();

        PlayerEntity player = client.player;

        float posX = 60;
        float posY = scaledHeight * 1.5f;
        float size = scaledHeight / 2f;

        float headYaw = -15;
        float pitchRange = 20;

        if (player.isInSneakingPose()) posY -= 30;
        if (player.isFallFlying()) posY -= 90;

        {
            GlStateManager.enableColorMaterial();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(posX, posY, 50.0F);
            GlStateManager.scalef(-size, size, size);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float f = player.field_6283;
            float g = player.yaw;
            float h = player.pitch;
            float i = player.prevHeadYaw;
            float j = player.headYaw;
            float handSwingProgress = player.handSwingProgress;
            GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
            DiffuseLighting.enable();
            GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            player.field_6283 = 0;
            player.headYaw = headYaw;
            player.pitch = MathHelper.clamp(player.pitch, -pitchRange, pitchRange);
            GlStateManager.translatef(0.0F, 0.0F, 0.0F);
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
            entityRenderDispatcher.method_3945(180.0F);
            entityRenderDispatcher.setRenderShadows(false);
            entityRenderDispatcher.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
            entityRenderDispatcher.setRenderShadows(true);
            player.field_6283 = f;
            player.yaw = g;
            player.pitch = h;
            player.prevHeadYaw = i;
            player.headYaw = j;
            player.handSwingProgress = handSwingProgress;
            GlStateManager.popMatrix();
            DiffuseLighting.disable();
            GlStateManager.disableRescaleNormal();
            GlStateManager.activeTexture(GLX.GL_TEXTURE1);
            GlStateManager.disableTexture();
            GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        }
    }
}
