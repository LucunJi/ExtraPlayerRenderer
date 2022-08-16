package github.io.lucunji.explayerenderer.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityInvoker {
    @Invoker
    boolean callGetFlag(int index);

    @Invoker
    void callSetFlag(int index, boolean value);
}
