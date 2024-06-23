package github.io.lucunji.explayerenderer.mixin.yacl;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(YACLScreen.class)
public abstract class YACLScreenMixin extends Screen {
    @Shadow(remap = false)
    public abstract boolean pendingChanges();

    @Shadow(remap = false)
    @Final
    public YetAnotherConfigLib config;

    @Shadow(remap = false)
    @Final
    public TabManager tabManager;

    @Shadow(remap = false) private boolean pendingChanges;

    protected YACLScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!Configs.isConfigScreen(this)) return;

        renderDarkening(guiGraphics);
        ci.cancel();
    }

    /**
     * Instantly apply changes, without saving to file or triggering any flag.
     * Force {@link #pendingChanges} to {@code true}
     */
    @Inject(method = "onOptionChanged", at = @At("RETURN"), remap = false)
    public void applyOnOptionChanged(Option<?> opt, CallbackInfo ci) {
        if (!Configs.isConfigScreen(this)) return;

        pendingChanges = true;

        OptionUtils.forEachOptions(config, Option::applyValue);
        OptionUtils.forEachOptions(config, option -> {
            if (option.changed()) {
                option.forgetPendingValue();
                YACLConstants.LOGGER.error("Option '{}' value mismatch after applying! Reset to binding's getter.", option.name().getString());
            }
        });

        if (tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab) {
            categoryTab.updateButtons();
            categoryTab.undoButton.active = false;
        }
    }

    @Inject(method = "cancelOrReset", at = @At("HEAD"), remap = false)
    public void reloadOnCancelled(CallbackInfo ci) {
        if (Configs.isConfigScreen(this)) return;

        if (pendingChanges()) {
            Configs.HANDLER.load();
        }
    }
}
