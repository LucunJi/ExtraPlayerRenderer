package github.io.lucunji.explayerenderer.mixin.patch;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import github.io.lucunji.explayerenderer.mixininterface.ImmediateMixinInterface;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VertexConsumerProvider.Immediate.class)
public abstract class ImmediateMixin implements ImmediateMixinInterface {

    @Unique
    private boolean forceDisableCulling = false;

    @Override
    public void extraPlayerRenderer$setForceDisableCulling(boolean disableCulling) { this.forceDisableCulling = disableCulling; }

    @WrapMethod(method = "draw(Lnet/minecraft/client/render/RenderLayer;)V")
    void forceDisableCulling(RenderLayer layer, Operation<Void> original) {
        if (this.forceDisableCulling) {
            RenderSystem.disableCull();
            original.call(layer);
            RenderSystem.enableCull();
        } else {
            original.call(layer);
        }
    }
}
