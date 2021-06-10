package github.io.lucunji.explayerenderer.mixin;

import github.io.lucunji.explayerenderer.Main;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * This class fixes rendering bug when "mirror" option is enabled.
 */
@Mixin(HeldItemRenderer.class)
public class HeldItemFeatureRendererMixin {
    @ModifyArg(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V"
            )
    )
    private ModelTransformation.Mode fixMirror(ModelTransformation.Mode mode) {
        if (Main.PLAYER_HUD_RENDERER.needFixMirrorItem()) {
            if (mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND) {
                return ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
            } else if (mode == ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND) {
                return ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;
            }
        }
        return mode;
    }

    @ModifyArg(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V"
            )
    )
    private boolean fixMirror(boolean leftHanded) {
        return Main.PLAYER_HUD_RENDERER.needFixMirrorItem() ^ leftHanded;
    }

    @ModifyArg(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V"
            )
    )
    private MatrixStack fixMirror(MatrixStack matrixStack) {
        if (Main.PLAYER_HUD_RENDERER.needFixMirrorItem()) matrixStack.scale(-1, 1, 1);
        return matrixStack;
    }
}
