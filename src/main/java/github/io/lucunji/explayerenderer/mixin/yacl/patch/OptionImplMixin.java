package github.io.lucunji.explayerenderer.mixin.yacl.patch;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.impl.OptionImpl;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.config.OptionPatch;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = OptionImpl.class, priority = Integer.MIN_VALUE)
public abstract class OptionImplMixin<T> implements Option<T>, OptionPatch<T> {
    @Shadow
    @Final
    private Binding<T> binding;
    @Shadow private T pendingValue;
    @Shadow public abstract boolean applyValue();

    @Shadow public abstract void requestSet(@NotNull T value);

    /**
     * We should rename it to isDirty
     */
    @Shadow public abstract boolean changed();

    @Shadow public abstract boolean isPendingValueDefault();

    @Unique private T savedValue;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    public void onInit(
            @NotNull Text name,
            @NotNull Function<T, OptionDescription> descriptionFunction,
            @NotNull Function<Option<T>, Controller<T>> controlGetter,
            @NotNull Binding<T> binding,
            boolean available,
            ImmutableSet<OptionFlag> flags,
            @NotNull Collection<BiConsumer<Option<T>, T>> listeners,
            CallbackInfo ci) {
        this.savedValue = binding.getValue();
    }

    @Inject(method = "changed", at = @At("HEAD"), cancellable = true, remap = false)
    public void onChanged(CallbackInfoReturnable<Boolean> cir) {
        if (!Configs.isConfigScreen(MinecraftClient.getInstance().currentScreen)) return;
        // the second case is needed to allow applyValue to work when running Undo/Cancel
        cir.setReturnValue(!Objects.equals(pendingValue, savedValue) || !Objects.equals(pendingValue, binding.getValue()));
    }


    @Unique
    @Override
    public T extraPlayerRenderer$getSavedValue() {
        return savedValue;
    }

    @Unique
    @Override
    public boolean extraPlayerRenderer$savePendingValue() {
        if (changed()) {
            binding().setValue(savedValue = pendingValue);
            return true;
        }
        return false;
    }

    @Unique
    @Override
    public void extraPlayerRenderer$restoreSavedValue() {
        requestSet(savedValue);
    }
}
