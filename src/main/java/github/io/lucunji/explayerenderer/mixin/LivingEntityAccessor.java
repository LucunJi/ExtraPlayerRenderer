package github.io.lucunji.explayerenderer.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    float getLeaningPitch();

    @Accessor
    void setLeaningPitch(float leaningPitch);

    @Accessor
    float getLastLeaningPitch();

    @Accessor
    void setLastLeaningPitch(float lastLeaningPitch);

    @Accessor
    void setFallFlyingTicks(int fallFlyingTicks);
}
